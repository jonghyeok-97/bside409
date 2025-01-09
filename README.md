<div align="center">
  <h1>UpUp Radio (올려올려 라디오)</h1>
</div>

## Project Link: [올려 올려 라디오 (https://upup-radio.site)](https://upup-radio.site)

<img src="https://github.com/user-attachments/assets/6604ceb1-8c4a-47e2-a43b-25e7b4d27633" alt="cover img" />
<br>

> ## ❤️ 위로의 사전적 정의
> ### ***따뜻한 말이나 행동으로 괴로움을 덜어주거나 슬픔을 달래주는 것.***

### '올려올려 라디오' 서비스는 다양한 상황에서 감정형(F)과 사고형(T)인 사람이 각각 다르게 반응하고 말하는 방식에서 영감을 얻어 감정형과 사고형의 두 가지 시각에서의 위로, 조언을 동시에 제공하는 서비스입니다.
<br>

## 💌 Core Feature

1. 💌 작성한 사연에 따라 감정형(F) & 사고형(T) 맞춤 답변 제공.
2. 📮 사용자가 작성한 사연과 답변을 볼 수 있는 편지함.
3. 📑 사용자가 작성한 사연을 분석해 8가지 대표 감정과 따뜻한 메시지를 건네주는 일일 리포트와 주간 리포트 제공.
<br>

## 💜 Code Contributors

이 프로젝트에 기여하신 고마운 분들입니다.  
<a href="https://github.com/jellyyelly/bside409/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=jellyyelly/bside409&max=3" />
</a>

  
## ⚙️ Tech Stack Used

### Container Orchestration

![Kubernetes](https://img.shields.io/badge/kubernetes-%23326ce5.svg?style=for-the-badge&logo=kubernetes&logoColor=white)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)

### Server

![Nginx](https://img.shields.io/badge/nginx-%23009639.svg?style=for-the-badge&logo=nginx&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)

### Database

![MySQL](https://img.shields.io/badge/mysql-4479A1.svg?style=for-the-badge&logo=mysql&logoColor=white)
![Redis](https://img.shields.io/badge/redis-%23DD0031.svg?style=for-the-badge&logo=redis&logoColor=white)
![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=Hibernate&logoColor=white)

### Monitoring

![Prometheus](https://img.shields.io/badge/Prometheus-E6522C?style=for-the-badge&logo=Prometheus&logoColor=white)
![Grafana](https://img.shields.io/badge/grafana-%23F46800.svg?style=for-the-badge&logo=grafana&logoColor=white)

### ETC

![Git](https://img.shields.io/badge/git-%23F05033.svg?style=for-the-badge&logo=git&logoColor=white)
![GitHub](https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white)

## Getting Started (Local)

### Prerequisites

- JDK >= 17
- Docker >= 27.0.0
- Docker Compose >= 2.31.0

### Ready `.env` at project root directory

```dotenv
DB_DATABASE="MySQL 데이터베이스 이름"
DB_URL="MySQL jdbc 주소"
DB_USERNAME="MySQL 로그인 아이디"
DB_PASSWD="MySQL 비밀번호"
KAKAO_CLIENT_ID="카카오 로그인 연동 시 CLIENT ID"
KAKAO_APP_ADMIN_KEY="카카오 로그인 연동 시 APP ADMIN KEY"
KAKAO_CLIENT_SECRET="카카오 로그인 연동 시 CLIENT_SECRET" 
CLOVASTUDIO_API_KEY="클로바 스튜디오 API KEY"
CLOVASTUDIO_APIGW_API_KEY="클로바 스튜디오 APIGW API KEY"
CLOVASTUDIO_REQUEST_ID="클로바 스튜디오 REQUEST ID"
JWT_ACCESS_SECRET="JWT 리프레시 토큰 액세스 키"
JWT_REFRESH_SECRET="JWT 리프레시 토큰 시크릿 키"
BASE_URL="CORS 적용할 호스트 주소"
REDIS_HOST="레디스 호스트 이름"
REDIS_PORT="레디스 포트 번호"
REDIS_EXPIRE="만료 시간 (초)"
REDIS_LIMIT="시간당 편지 작성 제한 횟수"
SPRING_ACTIVE_PROFILE="활성화할 프로필 이름"
JAR_FILENAME="JAR 파일 이름"
```

### Install

```shell
./gradlew clean build -x test 
```

### Usage

```shell
docker compose --profile dev up -d
```

## Usage (Kubernetes with ArgoCD)

### Prerequisites

- JDK >= 17
- Docker >= 27.0.0
- Kubernetes >= 1.29
- ArgoCD >= 2.13.3

### Install

ref: https://dev-gallery.tistory.com/69

### CI/CD PIPELINE

![image](https://github.com/user-attachments/assets/7f33a7c4-072b-4b8d-802e-3cac99c2b0dc)

## Preview

| <img src="https://github.com/user-attachments/assets/8ba88dac-3544-43dc-8764-13466db803dc" alt="img1" /> | <img src="https://github.com/user-attachments/assets/0a23ead6-1e0c-46a1-b4f6-5e7b8d67ca8c" alt="img2" />  |
|:--------------------------------------------------------------------------------------------------------:|:---------------------------------------------------------------------------------------------------------:|
|                                                 메인 화면 1                                                  |                                                  메인 화면 2                                                  |
| <img src="https://github.com/user-attachments/assets/25f0c727-0b16-43e1-b239-726c0cf07633" alt="img3" /> | <img src="https://github.com/user-attachments/assets/1991c107-b673-4b69-b3d9-61975a928d2d" alt="img4" />  |
|                                                  편지 쓰기                                                   |                                                사연 채택 중...                                                 |
| <img src="https://github.com/user-attachments/assets/15841170-5e8a-4a6f-bea6-96d33bffbf2c" alt="img6" /> | <img src="https://github.com/user-attachments/assets/2d09d2c8-d097-4e69-a564-71863fc2f9b5" alt="img7" />  |
|                                               F 유형을 위한 답변                                                |                                                T 유형을 위한 답변                                                |
| <img src="https://github.com/user-attachments/assets/62bfe9be-ddf5-41c8-ab55-60f8b38e354b" alt="img9" /> | <img src="https://github.com/user-attachments/assets/12ca7649-201e-4664-9fb2-c25766494693" alt="img10" /> |
|                                                 일일 리포트                                                 |                                                 주간 리포트                                                  |

## Contact
#### jellyyelly

- GitHub: [@jellyyelly](https://github.com/jellyyelly)
- email: [email](mailto:seojs0511@naver.com)  
