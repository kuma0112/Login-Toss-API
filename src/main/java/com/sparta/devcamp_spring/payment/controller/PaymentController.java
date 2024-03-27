package com.sparta.devcamp_spring.payment.controller;

import com.sparta.devcamp_spring.auth.entity.User;
import com.sparta.devcamp_spring.payment.dto.OrderInfoDto;
import com.sparta.devcamp_spring.payment.dto.PaymentConfirmationDto;
import com.sparta.devcamp_spring.payment.dto.PaymentRequestDto;
import com.sparta.devcamp_spring.payment.dto.PaymentResultDto;
import com.sparta.devcamp_spring.payment.facade.PaymentFacade;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
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

    public ResponseEntity<PaymentResultDto> confirmPayment(@RequestBody String jsonBody, @AuthenticationPrincipal User user) throws Exception {
        PaymentResultDto result = new PaymentResultDto();

        // JSON 요청 파싱
        PaymentRequestDto requestDto = parsePaymentRequest(jsonBody);

        // 주문 정보 확인
        validateOrder(requestDto);

        // 결제 승인 API 호출
        PaymentConfirmationDto confirmationDto = callPaymentConfirmationApi(requestDto);

        // 결제 상태에 따라 처리
        if (confirmationDto.isSuccessful()) {
            // 결제 완료 처리
            completePayment(requestDto, user);

            result.setPaymentSuccess(true);
            result.setMessage("결제 성공!");
            return ResponseEntity.ok(result);
        } else {
            // 결제 실패 시 주문 상태 복구
            paymentFacade.undoOrder(Long.parseLong(requestDto.getOrderId()));
            result.setPaymentSuccess(false);
            result.setMessage("결제 승인 처리가 정상적으로 완료되지 않았습니다. \nStatus : " + confirmationDto.getStatus());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
    }

    /**
     * Json 요청 파싱 메서드
     * @param jsonBody
     * @return
     */
    private PaymentRequestDto parsePaymentRequest(String jsonBody) {
        try {
            JSONParser parser = new JSONParser();
            JSONObject requestData = (JSONObject) parser.parse(jsonBody);
            String paymentKey = (String) requestData.get("paymentKey");
            String orderId = (String) requestData.get("orderId");
            String amount = (String) requestData.get("amount");

            return new PaymentRequestDto(paymentKey, orderId, amount);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * db에 저장한 결제 금액 합산과 토스가 건넨 결제 금액 합산이 같은지 확인
     * @param requestDto 주문 정보
     */
    private void validateOrder(PaymentRequestDto requestDto) throws Exception {
        OrderInfoDto orderInfo = paymentFacade.getOrderInfo(Long.parseLong(requestDto.getOrderId()));
        if (Integer.parseInt(requestDto.getAmount()) != orderInfo.getTotalPrice()) {
            throw new Exception("주문 정보가 상이합니다.");
        }
        paymentFacade.prepareOrder(Long.parseLong(requestDto.getOrderId()));
    }

    /**
     * 결제 승인 API 호출
     * @param requestDto
     * @return
     * @throws Exception
     */
    private PaymentConfirmationDto callPaymentConfirmationApi(PaymentRequestDto requestDto) throws Exception {
        // 토스에서 발급받은 위젯 시크릿 키
        String widgetSecretKey = "test_sk_4yKeq5bgrpPdRYpyg1zJrGX0lzW6";
        // 인코딩 후 string 타입 객체 authorizations에 담기
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes("UTF-8"));
        String authorizations = "Basic " + new String(encodedBytes, 0, encodedBytes.length);

        // Json으로 결제 정보 구성
        JSONObject obj = new JSONObject();
        obj.put("orderId", requestDto.getOrderId());
        obj.put("amount", requestDto.getAmount());
        obj.put("paymentKey", requestDto.getPaymentKey());

        // 토스 승인 url에 위에서 만든 authorizations 전송. http 메서드와 전송할 데이터 설정 후 전송
        URL url = new URL("https://api.tosspayments.com/v1/payments/confirm");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        setConnectionProperties(connection, authorizations);
        writeToConnection(connection, obj.toString().getBytes("UTF-8"));

        return parsePaymentConfirmationResponse(connection);
    }


    /**
     * 결제 완료 처리
     * @param requestDto
     * @param user
     */
    private void completePayment(PaymentRequestDto requestDto, User user) {
        try {
            paymentFacade.completeOrder(Long.parseLong(requestDto.getOrderId()), user);
        } catch (Exception e) {
            // 우리쪽 결제 완료처리에 에러가 발생한 경우 토스 취소 api를 호출.
            cancelPayment(requestDto.getPaymentKey());
            throw new RuntimeException("결제 완료 처리중 에러 발생", e);
        }
    }

    /**
     * 결제 취소 처리
     * @param paymentKey
     */
    private void cancelPayment(String paymentKey) {
        try {
            String widgetSecretKey = "test_sk_4yKeq5bgrpPdRYpyg1zJrGX0lzW6";
            Base64.Encoder encoder = Base64.getEncoder();
            byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes("UTF-8"));
            String authorizations = "Basic " + new String(encodedBytes, 0, encodedBytes.length);

            String cancelUrlString = "https://api.tosspayments.com/v1/payments/" + paymentKey + "/cancel";
            URL cancelUrl = new URL(cancelUrlString);
            HttpURLConnection cancelConnection = (HttpURLConnection) cancelUrl.openConnection();
            setConnectionProperties(cancelConnection, authorizations);

            JSONObject obj = new JSONObject(); // 필요한 경우 취소 요청에 대한 추가 데이터를 여기에 포함시킵니다.
            writeToConnection(cancelConnection, obj.toString().getBytes("UTF-8"));
            // 취소 요청의 응답 처리는 필요에 따라 추가합니다.
        } catch (Exception e) {
            throw new RuntimeException("결제 취소 처리중 에러 발생", e);
        }
    }

    /**
     * http 연결 설정
     * @param connection
     * @param authorizations
     * @throws ProtocolException
     */
    private void setConnectionProperties(HttpURLConnection connection, String authorizations) throws ProtocolException {
        connection.setRequestProperty("Authorization", authorizations);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
    }

    /**
     * 데이터 쓰기
     * @param connection
     * @param outputBytes
     * @throws IOException
     */
    private void writeToConnection(HttpURLConnection connection, byte[] outputBytes) throws IOException {
        try (OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write(outputBytes);
        }
    }


    /**
     * 토스에 인증 요청 보낸 후 응답을 파싱하는 메서드
     * @param connection
     * @return
     * @throws IOException
     * @throws ParseException
     */
    private PaymentConfirmationDto parsePaymentConfirmationResponse(HttpURLConnection connection) throws IOException, ParseException {
        int code = connection.getResponseCode();
        boolean isSuccess = code == 200;

        // 토스에게서 200 응답이 오면 응답 스트림을 읽은 후 이를 문자열로 변. 이후 Json화하여 dto에 담기
        try (InputStream responseStream = isSuccess ? connection.getInputStream() : connection.getErrorStream();
             Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8)) {
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(reader);
            String status = (String) jsonObject.get("status");

            return new PaymentConfirmationDto(isSuccess, status);
        }
    }
}
