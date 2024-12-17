## ✅프로젝트 소개
- `네이버 생성형 AI인 클로바`를 활용한 고민 상담 플랫폼으로 사용자에게 감정형(F)과 사고형(T)의 두 가지 시각에서의 위로, 조언을 동시에 제공하는 것을 목표로 합니다.

<img src="https://github.com/user-attachments/assets/80170bc5-3242-4a9d-acae-81d902fe6e10" alt="image" width="600" />

## ✅팀원 구성
- 디자이너: 1명  
- 기획자: 1명  
- 프론트엔드 엔지니어: 1명  
- 백엔드 엔지니어: 3명  

## ✅맡은 역할
- 고민 상담 플랫폼 고도화  
    - 본 프로젝트는 `비사이드 포텐데이`에서 이미 1개월 동안 진행되었던 프로젝트입니다. 
    - `기존 백엔드 엔지니어의 권유로 11월 4일부터 프로젝트에 합류`하여 플랫폼 고도화 작업에 참여하게 되었습니다. 

## ✅ERD

<img src="https://github.com/user-attachments/assets/89cec162-af05-4306-9db0-5b30609f38a4" alt="image" width="1000" />

## ✅아키텍처

<img src="https://github.com/user-attachments/assets/25f436f3-6314-4a26-80c1-97267327d2c4" alt="image" width="600" />

## ✅기능
### 1. 주간 통계 서비스
#### 통계 서비스 사진
<img src="https://github.com/user-attachments/assets/07e481a7-000a-4dd9-877e-aabd012cf357" alt="image" width="300" />

#### ISSUE 해결 과정
- [ISO 8601 기준으로 특정 날짜의 N월 N주차를 Java 8 날짜 API로 구하기](https://dkswhdgur246.tistory.com/71)


### 2. Prometheus와 Grafana를 활용해 ERROR 레벨 로그 발생 시 Slack 알림 연동
- Prometheus와 Grafana를 활용하여 **ERROR 레벨 로그 발생 시 Slack 알림**을 연동, 실시간 모니터링 체계를 구축하여 운영 중 발생한 문제에 즉각 대응할 수 있도록 설정했습니다.  
- 이후 **CPU 사용률**과 **메모리 사용률**의 상한치 알림도 추가로 연동하여, 성능 문제가 발생했을 때 신속히 대응할 수 있도록 개선 중입니다.  
- 이를 위해 **실무로 배우는 시스템 성능 최적화**와 **반효경님의 운영체제 강의**를 통해 관련 지식을 학습하고 있습니다.  

## ✅회고: 서비스 오너십을 가지고 기획자와 협력해 비용 절감 방안을 기획한 경험
#### 문제: 비용 절감의 필요성
운영 중인 고민 상담 서비스는 비용 절감이 가장 큰 과제였습니다. 네이버 클로바 API와 네이버 클라우드 플랫폼은 AWS와 달리 무료 프리티어가 없을뿐더러, 사용자가 늘어날수록 클로바 API 호출이 증가하여 비용이 발생하는 구조였습니다. 이러한 구조 상 사용자의 7일치 데이터를 분석해 통계 내주는 주간 통계 기능은 평소보다 7배의 비용을 발생시킬 수 있었기 때문에 효율적인 비용 절감 방안을 모색해야 했습니다.

#### 해결 과정: 협업을 통해 문제 분석과 대안 마련
백엔드 엔지니어 2명과 함께 예상 사용자 수, 서버 비용, 클로바 API 비용을 예상하여 총 비용을 계산했습니다. 수차례 회의를 통해 사용자 경험을 유지하면서도 비용을 줄일 방법을 모색했으며, 기획자와 긴밀히 협력해 수정안을 기획하고 마련했습니다.

<img src="https://github.com/user-attachments/assets/819b9714-a04f-4eb1-8425-cdef046f023a" alt="image" width="300" />

#### 결과: 협업 역량 향상 및 최선의 선택 
처음에는 최소한의 기능을 출시하고, 사용자의 동향을 파악해 점차 기능을 확대하는 구조로 결정하게 되었습니다. 그리고, 사용자의 동향을 파악하기 위해 모니터링 서버를 구축하게 되었습니다. 이렇게 사용자 경험 개선과 비용 절감간의 트레이드 오프를 고민하며, 다음과 같은 것들을 깨달으며 성장할 수 있었습니다.
- 비용 절감과 사용자 경험 개선 중 어떤 것을 우선순위로 두어야 하는지
- 팀의 공동 목표를 정하는 것은 중요한 것
- 예상 수치가 아닌 데이터 기반의 의사결정이 중요하다는 것
- 개발자 및 기획자와 여러 차례의 회의를 한 경험을 통해 협업 역량 성장