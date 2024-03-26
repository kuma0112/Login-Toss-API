# Devcamp - Spring 결제 시스템 구현
- 토스 API를 사용하여 결제 시스템을 구현합니다.
- 결제 로직을 이해하고 환불 기능을 추가하여 구현합니다. 


## 주요 기능

- **주문서 생성**: 사용자가 결제에 필요한 정보를 입력하여 주문서를 생성합니다.
- **쿠폰, 포인트 사용**: 결제 시 쿠폰이나 포인트를 사용할 수 있습니다.
- **결제 및 결제 취소**: 사용자의 결제 요청을 처리하고 필요 시 결제를 취소합니다.
- **환불 기능**: 사용자의 환불 요청을 처리합니다.

## 디렉토리 구조
프로젝트의 디렉토리 구조는 다음과 같이 구성되어 있습니다:

```plaintext
📂payment
┣ 📂dto
┃ ┣ 📜CreateOrderDto.java
┃ ┣ 📜OrderInfoDto.java
┃ ┣ 📜PaymentResultDto.java
┃ ┗ 📜RefundRequestDto.java
┣ 📂entity
┃ ┣ 📜Order.java
┃ ┣ 📜OrderItem.java
┃ ┗ 📜ShippingInfo.java
┣ 📂facade
┃ ┗ 📜PaymentFacade.java
┣ 📂repository
┃ ┣ 📜OrderRepository.java
┃ ┣ 📜RefundRepository.java
┃ ┗ 📜ShippingInfoRepository.java
┣ 📂service
┃ ┣ 📜OrderService.java
┃ ┣ 📜RefundService.java
┃ ┗ 📜ShippingService.java
┗ 📜PaymentController.java

📂payment
┣ 📂dto
┃ ┣ 📜CreateOrderDto.java
┃ ┣ 📜OrderInfoDto.java
┃ ┣ 📜PaymentResultDto.java
┃ ┗ 📜RefundRequestDto.java
┣ 📂entity
┃ ┣ 📜Order.java
┃ ┣ 📜OrderItem.java
┃ ┗ 📜ShippingInfo.java
┣ 📂facade
┃ ┗ 📜PaymentFacade.java
┣ 📂repository
┃ ┣ 📜OrderRepository.java
┃ ┣ 📜RefundRepository.java
┃ ┗ 📜ShippingInfoRepository.java
┣ 📂service
┃ ┣ 📜OrderService.java
┃ ┣ 📜RefundService.java
┃ ┗ 📜ShippingService.java
┗ 📜PaymentController.java

```


## 토스 API 결제 시스템
토스 결제 API를 사용한 결제 시스템은 다음과 같은 순서로 작동합니다:

1. 사용자는 필요한 결제 정보를 입력하고, [결제하기] 버튼을 클릭하여 백엔드에 결제 요청을 합니다.
2. 백엔드는 받은 요청 객체를 검증하고 필요한 값들을 추가한 후, 데이터베이스에 결제 정보를 저장하고 그 결과를 프론트엔드로 반환합니다.
3. 프론트엔드는 백엔드로부터 받은 결제 정보를 바탕으로 tossPayments.requestPayment('카드', {결제 정보 파라미터}) 함수를 호출하여 토스페이먼츠 결제창을 사용자에게 보여줍니다.
4. 사용자는 토스페이먼츠 결제창에서 결제 절차를 완료합니다.
5. 결제 완료 후, 토스페이먼츠는 결제 성공 여부와 관련 파라미터를 프론트엔드의 콜백 주소(successUrl)로 리다이렉트합니다.
6. 프론트엔드는 토스에게 받은 결제정보를 서버에게 전달합니다. 
7. 백엔드는 토스페이먼츠에 최종 결제 승인 요청을 보냅니다. 실패한 경우에는 실패 정보를 프론트엔드로 반환하여 사용자에게 알립니다.



