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

##  

### Step  
- Batch job을 구성하는 독립적인 하나의 단계로서 실제 배치 처리를 정의하고 컨트롤하는 데 필요한 모든 정보를 가지고 있는 도메인 객체
- 단순한 단일 태스크 뿐 아니라 입력과 처리 그리고 출력과 관련된 복잡한 비즈니스 로직을 포함하는 모든 설정들을 담고 있다.
- 배치작업을 어떻게 구성하고 실행할 것인지 Job 의 세부 작업을 Task 기반으로 설정하고 명세해 놓은 객체
- 모든 Job은 하나 이상의 step으로 구성됨

TaskletStep
- 가장 기본이 되는 클래스로서 Tasklet 타입의 구현체들을 제어한다
PartitionStep
- 멀티 스레드 방식으로 Step 을 여러 개로 분리해서 실행한다
JobStep
- Step 내에서 Job 을 실행하도록 한다
FlowStep
- Step 내에서 Flow 를 실행하도록 한다
<img width="1124" height="597" alt="스크린샷 2025-12-23 오후 12 43 02" src="https://github.com/user-attachments/assets/a52d9276-0a4a-44ab-95cd-3786d32a0457" />

##  

<img width="1180" height="451" alt="스크린샷 2025-12-23 오후 12 43 20" src="https://github.com/user-attachments/assets/681306ea-3d01-47bc-a0b7-651dabe986e5" />  


##  

### StepExecution  
- Step 에 대한 한번의 시도를 의미하는 객체로서 Step 실행 중에 발생한 정보들을 저장하고 있는 객체
  - 시작시간, 종료시간 ,상태(시작됨,완료,실패), commit count, rollback count 등의 속성을 가짐
- Step 이 매번 시도될 때마다 생성되며 각 Step 별로 생성된다
- Job 이 재시작 하더라도 이미 성공적으로 완료된 Step 은 재 실행되지 않고 실패한 Step 만 실행된다
- 이전 단계 Step이 실패해서 현재 Step을 실행하지 않았다면 StepExecution을 생성하지 않는다. Step이 실제로 시작됐을 때만 StepExecution을 생성한다
- JobExecution 과의 관계
  - Step의 StepExecution 이 모두 정상적으로 완료 되어야 JobExecution이 정상적으로 완료된다.
  - Step의 StepExecution 중 하나라도 실패하면 JobExecution 은 실패한다

(BATCH_STEP_EXECUTION 테이블과 매핑)  
- JobExecution 와 StepExecution 는 1:M 의 관계
- 하나의 Job 에 여러 개의 Step 으로 구성했을 경우 각 StepExecution 은 하나의 JobExecution 을 부모로 가진다

<img width="1247" height="637" alt="스크린샷 2025-12. 23 오후 5 49 56" src="https://github.com/user-attachments/assets/bc2aa541-045b-4668-9397-02f8dfab096c" /> 

## 

<img width="1198" height="648" alt="스크린샷 2025-12-23 오후 5 50 11" src="https://github.com/user-attachments/assets/c39f56cf-710c-4ccd-9002-02d14284bcf8" />  

##  

<img width="1244" height="631" alt="스크린샷 2025-12-23 오후 5 50 32" src="https://github.com/user-attachments/assets/04234c52-0c83-47e5-a33b-ac72ab26aab5" />



##  

### StepContribution  
- 청크 프로세스의 변경 사항을 버퍼링 한 후 StepExecution 상태를 업데이트하는 도메인 객체
- 청크 커밋 직전에 StepExecution 의 apply 메서드를 호출하여 상태를 업데이트 함
- ExitStatus 의 기본 종료코드 외 사용자 정의 종료코드를 생성해서 적용 할 수 있음

<img width="1091" height="337" alt="스크린샷 2025-12-23 오후 6 13 57" src="https://github.com/user-attachments/assets/2e43ebdd-1678-4f3d-b0ee-3174f6f39fd6" />  

##  

<img width="1183" height="617" alt="스크린샷 2025-12-23 오후 6 14 10" src="https://github.com/user-attachments/assets/eb1e379d-450e-4fee-ba91-1ead827c1773" />


##  

### ExecutionContext  
- 프레임워크에서 유지 및 관리하는 키/값으로 된 컬렉션으로 StepExecution 또는 JobExecution 객체의 상태(state)를 저장하는 공유 객체
- DB 에 직렬화 한 값으로 저장됨 - { “key” : “value”}
- 공유 범위
  - Step 범위 – 각 Step 의 StepExecution 에 저장되며 Step 간 서로 공유 안됨
  - Job 범위 – 각 Job의 JobExecution 에 저장되며 Job 간 서로 공유 안되며 해당 Job의 Step 간 서로 공유됨
  - Job 재 시작시 이미 처리한 Row 데이터는 건너뛰고 이후로 수행하도록 할 때 상태 정보를 활용한다

<img width="713" height="156" alt="스크린샷 2025-12-23 오후 6 30 01" src="https://github.com/user-attachments/assets/d8e98aea-4d6b-4234-a354-84d2c52acaa6" />  

##  

<img width="1186" height="591" alt="스크린샷 2025-12-23 오후 6 30 13" src="https://github.com/user-attachments/assets/ed7f5a9e-c614-463a-84af-3b504c03851c" />  

##  

<img width="1047" height="630" alt="스크린샷 2025-12-23 오후 6 30 28" src="https://github.com/user-attachments/assets/d9164d84-aa21-4193-81f1-2e02901648d2" />  



##  

### JobRepository  
- 배치 작업 중의 정보를 저장하는 저장소 역할
- Job이 언제 수행되었고, 언제 끝났으며, 몇 번이 실행되었고 실행에 대한 결과 등의 배치 작업의 수행과 관련된 모든 meta data 를 저장함
  - JobLauncher, Job, Step 구현체 내부에서 CRUD 기능을 처리함

<img width="962" height="493" alt="스크린샷 2025-12-23 오후 6 48 26" src="https://github.com/user-attachments/assets/b511bcf4-2369-451d-926c-d31e8a51b335" />

##  
(JobRepository 설정)  
- @EnableBatchProcessing 어노테이션만 선언하면 JobRepository 가 자동으로 빈으로 생성됨
- BatchConfigurer 인터페이스를 구현하거나 BasicBatchConfigurer 를 상속해서 JobRepository 설정을 커스터마이징 할 수 있다


##  

### JobLauncher  
- 배치 Job 을 실행시키는 역할을 한다
- Job과 Job Parameters를 인자로 받으며 요청된 배치 작업을 수행한 후 최종 client 에게 JobExecution을 반환함
- 스프링 부트 배치가 구동이 되면 JobLauncher 빈이 자동 생성 된다

(Job 실행)  
- JobLanucher.run(Job, JobParameters)
- 스프링 부트 배치에서는 JobLauncherApplicationRunner 가 자동적으로 JobLauncher 을 실행시킨다
- 동기적 실행
  - taskExecutor 를 SyncTaskExecutor 로 설정할 경우 (기본값은 SyncTaskExecutor)
  - JobExecution 을 획득하고 배치 처리를 최종 완료한 이후 Client 에게 JobExecution 을 반환
  - 스케줄러에 의한 배치처리에 적합 함 – 배치처리시간이 길어도 상관없는 경우
- 비동기적 실행
  - taskExecutor 가 SimpleAsyncTaskExecutor 로 설정할 경우
  - JobExecution 을 획득한 후 Client 에게 바로 JobExecution 을 반환하고 배치처리를 완료한다
  - HTTP 요청에 의한 배치처리에 적합함 – 배치처리 시간이 길 경우 응답이 늦어지지 않도록 함 
<img width="963" height="625" alt="스크린샷 2025-12-23 오후 6 52 40" src="https://github.com/user-attachments/assets/d4476728-4b56-40fe-ad1a-c0e696232376" />







