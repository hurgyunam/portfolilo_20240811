# 허규남 포트폴리오(2018.03 ~ 2024.09)



## Flutter 폴더

- provider 폴더
인앱 결제, 구독 결제, 구독 전환(upgrade/downgrade) 관련 기능이 클래스와 그에 따른 riverpod purchase provider입니다. 

- page 폴더
purchase_provider의 state에 따라 Flutter UI 페이지가 그려집니다.

## Nuxt 폴더

- composables 폴더
TradingView 라이브러리를 다루기 위한 일종의 Controller와 Model. callback 요소를 Promise로 치환함으로써 코드의 가독성을 높이는 작업을 했습니다.

- pages/coin/[marketCode] 폴더
TradingView 라이브러리가 표출되는 페이지

- Dockerfile
React에 Next.js가 있다면 Vue에는 Nuxt.js가 있습니다. Next.js와 같이 서버에서 관리해줄 필요가 있습니다. 환경변수와 build 그리고 실행까지의 과정이 담겨 있습니다.


## Spring API 폴더

- 프로젝트에서 유저 회원가입/로그인 기능이 담겨있습니다. 로그인 방식은 세션 방식입니다.

## Spring Scheduler 폴더

- DB에 들어있는 거래데이터를 기반으로 예측 Signal을 채점하는 스케쥴러입니다.
- 채점 기준이 될만한 거래데이터를 쿼리를 통해 1차적으로 걸러낸 후 시간 비교를 통해 채점합니다.