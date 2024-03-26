package com.sparta.devcamp_spring.payment.controller;

import com.sparta.devcamp_spring.auth.entity.User;
import com.sparta.devcamp_spring.payment.dto.OrderInfoDto;
import com.sparta.devcamp_spring.payment.dto.PaymentResultDto;
import com.sparta.devcamp_spring.payment.facade.PaymentFacade;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;


@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {
    private final PaymentFacade paymentFacade;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/checkout")
    public ResponseEntity<OrderInfoDto> getCheckoutData(@RequestParam Long orderId) throws Exception {
        OrderInfoDto orderInfo = paymentFacade.getOrderInfo(orderId);
        return ResponseEntity.ok(orderInfo);
    }

    /**
     * 토스 결제모듈을 통해서 실제 결제 처리가 어떻게 이루어지는지 확인하는 예시 컨트롤러 입니다.
     * 토스를 포함한 대부분의 결제로직은 "인증"절차와 "승인"절차로 나뉘어져 있습니다.
     * "인증"은 말 그대로 고객이 해당 고객이 맞는지, 그리고 해당 결제를 지불할 능력이 있는지 인증하는 절차이고,
     * "승인"은 인증이 끝난 고객의 결제처리를 "승인"하는 절차입니다.
     * "인증" 절차는 클라이언트에서 직접 토스(pg사)서버와 연동해서 이루어지고,
     * 그 처리 결과를 토스 서버 혹은 우리의 클라이언트 쪽에서 직접 우리쪽으로 전달합니다.
     * 토스의 경우에는 클라이언트에서 우리 서버를 다시 호출하도록 구현되어있습니다.
     * 아래는 해당 호출을 받아서 결제 "승인" 처리를 하는 컨트롤러의 예시입니다.
     * 참고 : https://docs.tosspayments.com/guides/payment-widget/integration
     * @param jsonBody 요청 데이터
     * @return 결제 처리 결과 응답 데이터
     * @throws Exception exception
     */
    @RequestMapping(value = "/confirm")
    public ResponseEntity<PaymentResultDto> confirmPayment(@RequestBody String jsonBody, @AuthenticationPrincipal User user) throws Exception {
        // TODO: 위의 참고 링크에 들어가시면 토스에서 제공한 예시코드를 확인 하실 수 있습니다. 해당코드는 예시코드를 최대한 그대로 사옹하려 했습니다.
        // TODO: 실제로 모듈을 구현할 때는 역할을 분리해서 메서드를 나눠서 구현하셔야 유지보수에 편리한 코드가 될 수 있습니다.
        PaymentResultDto result = new PaymentResultDto();
        // 예시코드를 그대로 사용하기 위해서 requestDto를 사용하지 않고 직접 파싱했습니다.
        JSONParser parser = new JSONParser();
        String orderId;
        String amount;
        String paymentKey;
        try {
            // 클라이언트에서 받은 JSON 요청 데이터 입니다.
            JSONObject requestData = (JSONObject) parser.parse(jsonBody);
            paymentKey = (String) requestData.get("paymentKey");
            orderId = (String) requestData.get("orderId");
            amount = (String) requestData.get("amount");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        };

        // 클라이언트에서 보낸 요청 정보는 조작될 수 있기에, 실제 db에서 주문정보와 결제금액이 요청받은 결제금액과 같은지 확인해야 합니다.
        OrderInfoDto orderInfo = paymentFacade.getOrderInfo(Long.parseLong(orderId));
        if (Integer.parseInt(amount) != orderInfo.getTotalPrice()) {
           throw new Exception("주문 정보가 상이합니다.");
        }

        // 결제가 승인될 수 있도록 결제 대기상태로 만듭니다.
        paymentFacade.prepareOrder(Long.parseLong(orderId));

        JSONObject obj = new JSONObject();
        obj.put("orderId", orderId);
        obj.put("amount", amount);
        obj.put("paymentKey", paymentKey);

        String widgetSecretKey = "test_sk_4yKeq5bgrpPdRYpyg1zJrGX0lzW6";
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes("UTF-8"));
        String authorizations = "Basic " + new String(encodedBytes, 0, encodedBytes.length);

        // Toss 결제 승인 api 호출하기
        URL url = new URL("https://api.tosspayments.com/v1/payments/confirm");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", authorizations);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(obj.toString().getBytes("UTF-8"));

        int code = connection.getResponseCode();
        boolean isSuccess = code == 200;

        InputStream responseStream = isSuccess ? connection.getInputStream() : connection.getErrorStream();

        Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8);
        JSONObject jsonObject = (JSONObject) parser.parse(reader);
        responseStream.close();

        // 결제 승인 api 호출 결과에서 결제 상태 정보값을 받아와서 대조합니다.
        String status = (String) jsonObject.get("status");
        // 결제가 성공한 경우
        if (isSuccess && status.equalsIgnoreCase("DONE")) {
            try {
                // 우리쪽 결제도 완료처리를 진행합니다
                paymentFacade.completeOrder(Long.parseLong(orderId), user);
            } catch (Exception e) {
                // 우리쪽 결제 완료처리에 에러가 발생한 경우 토스 취소 api를 호출해줍니다.
                String cancelUrlString = "https://api.tosspayments.com/v1/payments/" + paymentKey + "/cancel";
                URL cancelUrl = new URL(cancelUrlString);
                HttpURLConnection cancelConnection = (HttpURLConnection) cancelUrl.openConnection();
                cancelConnection.setRequestProperty("Authorization", authorizations);
                cancelConnection.setRequestProperty("Content-Type", "application/json");
                cancelConnection.setRequestMethod("POST");
                cancelConnection.setDoOutput(true);

                OutputStream cancelOutputStream = connection.getOutputStream();
                cancelOutputStream.write(obj.toString().getBytes("UTF-8"));

                result.setPaymentSuccess(false);
                result.setMessage("결제 완료 처리중 에러 발생하여 취소 api 호출에 완료했습니다.");
                return ResponseEntity.ok(result);
            }
            // 토스 결제 승인 api 호출 성공 && 우리쪽 결제 완료처리 성공
            result.setPaymentSuccess(true);
            result.setMessage("결제 성공!");
            return ResponseEntity.ok(result);
        }

        // 결제가 성공하지 못한경우 주문 상태를 다시 대기로 되돌립니다.
        paymentFacade.undoOrder(Long.parseLong(orderId));
        result.setPaymentSuccess(false);
        result.setMessage("결제 승인 처리가 정상적으로 완료되지 않았습니다. \nStatus : " + (String) jsonObject.get("status"));
        return ResponseEntity.status(code).body(result);
    }
}
