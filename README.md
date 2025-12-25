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

##  

### Job  
- 배치 계층 구조에서 가장 상위에 있는 개념, 하나의 배치작업 자체를 의미함.
- Job Configuration을 통해 생성되는 객체 단위로서 배치작업을 어떻게 구성하고 실행할 것인지 전체적으로 설정하고 명세해 놓은 객체.
- 여러 Step을 포함하고 있는 컨테이너로서 반드시 한개 이상의 Step으로 구성해야함.

#### 기본 구현체 
- SimpleJob
  - 순차적으로 Step을 실행시키는 Job
  - 모든 Job에서 유용하게 사용할 수 있는 표준 기능을 가지고 있음
- FlowJob
  - 특정한 조건과 흐름에 따라 Step을 구성하여 실행시키는 Job
  - Flow 객체를 실행시켜서 작업을 진행함
 <img width="1253" height="573" alt="스크린샷 2025-12-21 오전 9 20 55" src="https://github.com/user-attachments/assets/606301f8-7da1-46ca-9410-d78eddfceae7" />


### JobInstance  
- Job 이 실행될 때 생성되는 Job 의 논리적 실행 단위 객체로서 고유하게 식별 가능한 작업 실행을 나타냄  
- Job 의 설정과 구성은 동일하지만 Job 이 실행되는 시점에 처리하는 내용은 다르기 때문에 Job 의 실행을 구분해야 함  
  - 예를 들어 하루에 한 번 씩 배치 Job이 실행된다면 매일 실행되는 각각의 Job 을 JobInstance 로 표현합니다.  
- JobInstance 생성 및 실행  
  - 처음 시작하는 Job + JobParameter 일 경우 새로운 JobInstance 생성  
  - 이전과 동일한 Job + JobParameter 으로 실행 할 경우 이미 존재하는 JobInstance 리턴  
    - 내부적으로 JobName + jobKey (jobParametes 의 해시값) 를 가지고 JobInstance 객체를 얻음  
- Job 과는 1:M 관계  
- JOB_NAME (Job) 과 JOB_KEY (JobParameter 해시값) 가 동일한 데이터는 중복해서 저장할 수 없음

<img width="843" height="597" alt="스크린샷 2025-12-21 오전 9 48 58" src="https://github.com/user-attachments/assets/64bb75f5-1c60-4767-9e8c-cb5dbf293a90" />  

##   
  
<img width="1097" height="581" alt="스크린샷 2025-12-21 오전 9 49 07" src="https://github.com/user-attachments/assets/896fdde9-2d40-43b8-9762-3ff598ea32f4" />

##  

### JobParameter  
- Job을 실행할 때 함께 포함되어 사용되는 파라미터를 가진 도메인 객체  
- 하나의 Job에 존재할 수 있는 여러개의 JobInstance를 구분하기 위한 용도  
- JobParameters와 JobInstance는 1:1 관계  

(생성 및 바인딩)  
- 어플리케이션 실행 시 주입
  - Java -jar LogBatch.jar requestDate=20210101
- 코드로 생성
  - JobParameterBuilder, DefaultJobParametersConverter
- SpEL 이용  
  - @Value(“#{jobParameter[requestDate]}”), @JobScope, @StepScope 선언 필수


(BATCH_JOB_EXECUTION_PARAM 테이블과 매핑)  
- JOB_EXECUTION 과 1:M 의 관계

##  

<img width="1082" height="575" alt="스크린샷 2025-12-22 오후 9 54 00" src="https://github.com/user-attachments/assets/d2f4c20f-4e71-4cf4-aeeb-9ce21a50d424" />


##  

### JobExecution  

- JobIstance 에 대한 한번의 시도를 의미하는 객체로서 Job 실행 중에 발생한 정보들을 저장하고 있는 객체
  - 시작시간, 종료시간 ,상태(시작됨,완료,실패),종료상태의 속성을 가짐
- JobIstance 과의 관계
  - JobExecution은 'FAILED' 또는 'COMPLETED‘ 등의 Job의 실행 결과 상태를 가지고 있음
  - JobExecution 의 실행 상태 결과가 'COMPLETED’ 면 JobInstance 실행이 완료된 것으로 간주해서 재 실행이 불가함
  - JobExecution 의 실행 상태 결과가 'FAILED’ 면 JobInstance 실행이 완료되지 않은 것으로 간주해서 재실행이 가능함
    - JobParameter 가 동일한 값으로 Job 을 실행할지라도 JobInstance 를 계속 실행할 수 있음
  - JobExecution 의 실행 상태 결과가 'COMPLETED’ 될 때까지 하나의 JobInstance 내에서 여러 번의 시도가 생길 수 있음

(BATCH_JOB_EXECUTION 테이블과 매핑)  
-  JobInstance 와 JobExecution 는 1:M 의 관계로서 JobInstance 에 대한 성공/실패의 내역을 가지고 있음

<img width="1175" height="613" alt="스크린샷 2025-12-23 오후 12 37 23" src="https://github.com/user-attachments/assets/330b352c-1845-4e71-891c-c7af158fbb66" />  

##  

<img width="1251" height="625" alt="스크린샷 2025-12-23 오후 12 37 36" src="https://github.com/user-attachments/assets/421b1c3b-f0e9-4415-b44b-c6b9b77ba901" />  

##  

<img width="1244" height="654" alt="스크린샷 2025-12-23 오후 12 37 46" src="https://github.com/user-attachments/assets/68944c79-41cb-4b60-bc4c-5e93028ee907" />






