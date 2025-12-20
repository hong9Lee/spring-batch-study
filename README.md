# SPRING-BATCH-STUDY
- 목적: 실무에서 스프링 배치를 사용해왔지만 제대로 알고 사용했는지 재점검

배치 핵심 패턴  
- READ: 데이터베이스, 파일, 큐에서 다량의 데이터를 조회한다.
- PROCESS: 특정 방법으로 데이터를 가공한다.
- WRITE: 데이터를 수정된 양식으로 다시 저장한다.

##   

배치 시나리오  
- 배치 프로세스를 주기적으로 커밋
- 동시 다발적인 Job의 배치 처리, 대용량 병렬 처리
- 실패 후 수동 또는 스케줄링에 의한 재시작
- 의존관계가 있는 step 여러개를 순차적으로 처리
- 조건적 Flow 구성을 통한 체계적이고 유연한 배치 모델 구성
- 반복, 재시도, Skip 처리

##  

배치 아키텍처  
<img width="1139" height="386" alt="스크린샷 2025-12-20 오후 2 10 10" src="https://github.com/user-attachments/assets/ad8bbd39-171d-4cb7-862b-e0b1794eceaf" />


##  

### @EnableBatchProcessing  
총 4개의 설정 클래스를 실행시키며 스프링 배치의 모든 초기화 및 실행 구성이 이루어진다.  
스프링 부트 배치의 자동 설정 클래스가 실행됨으로 빈으로 등록된 모든 Job을 검색해서 초기화와 동시에 Job을 수행하도록 구성됨.  
<img width="290" height="567" alt="스크린샷 2025-12-20 오후 2 13 43" src="https://github.com/user-attachments/assets/75576cac-06fc-47db-877f-3cdd13301af1" />

1. BatchAutoConfiguration
- 스프링 배치가 초기화될때 자동으로 실행되는 설정 클래스.  
- Job을 수행하는 JobLauncherApplicationRunner 빈을 생성.  
  
2. SimpleBatchConfiguration  
- JobBuilderFactory와 StepBuilderFactory 생성.  
- 스프링 배치의 주요 구성 요소 생성 - 프록시 객체로 생성됨.

3. BatchConfigurerConfiguration
- BasicBatchConfigurer
  - SimpleBatchConfiguration에서 생성한 프록시 객체의 실제 대상 객체를 생성하는 설정 클래스
  - 빈으로 의존성 주입 받아서 주요 객체들을 참조해서 사용할 수 있다.
- JpaBatchConfigurer
  - JPA 관련 객체를 생성하는 설정 클래스
- 사용자 정의 BatchConfigurer 인터페이스를 구현하여 사용할 수 있음.


## 

Job이 구동되면 Step을 실행하고 Step이 구동되면 Tasklet을 실행하도록 설정함.  
<img width="984" height="286" alt="스크린샷 2025-12-20 오후 2 31 59" src="https://github.com/user-attachments/assets/8fdad271-fdf4-4aa4-ad6e-9339a57fd36d" />

















