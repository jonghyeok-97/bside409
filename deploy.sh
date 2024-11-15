#!/bin/bash

# 포트 설정
BLUE_PORT=8081
BLUE_METRIC_PORT=9291
GREEN_PORT=8082
GREEN_METRIC_PORT=9292

# 현재 활성화된 환경 확인 함수
get_active_env() {
    # nginx.conf 파일에서 현재 active 상태인 환경 확인
    if grep -q "server blue:$BLUE_PORT weight=1;" /etc/nginx/conf.d/deploy.conf; then
        echo "blue"
    else
        echo "green"
    fi
}

# 컨테이너 헬스체크 함수
check_health() {
    local port=$1
    local max_attempts=30
    local attempt=1

    echo "🚧 $port 포트 번호로 헬스 체크 시도 중..."

    while [ $attempt -le $max_attempts ]; do
        if curl -s "http://localhost:$port/health" | grep -q "OK"; then
            echo "👍 헬스 체크에 성공했습니다!"
            return 0
        fi

        echo "🚧 최대 $max_attempts 시도 횟수 중 $attempt 번째 시도 중 ..."
        sleep 2
        attempt=$((attempt + 1))
    done

    echo "⚠️ $max_attempts 시도를 했음에도 헬스 체크에 실패했습니다."
    return 1
}

# 메인 배포 프로세스
deploy() {
    # 현재 활성화된 환경 확인
    ACTIVE_ENV=$(get_active_env)
    echo "⚡️ 현재 Active Environment: $ACTIVE_ENV"

    # 새로운 환경 결정
    if [ "$ACTIVE_ENV" = "blue" ]; then
        NEW_ENV="green"
        NEW_PORT=$GREEN_PORT
        NEW_METRIC_PORT=$GREEN_METRIC_PORT
        OLD_ENV="blue"
        OLD_PORT=$BLUE_PORT
        OLD_METRIC_PORT=$BLUE_METRIC_PORT
    else
        NEW_ENV="blue"
        NEW_PORT=$BLUE_PORT
        NEW_METRIC_PORT=$BLUE_METRIC_PORT
        OLD_ENV="green"
        OLD_PORT=$GREEN_PORT
        OLD_METRIC_PORT=$GREEN_METRIC_PORT
    fi

    echo "🚧 $NEW_ENV 버전의 포트 번호: $NEW_PORT 로 배포하는 중..."

    # 새 버전 배포
    docker compose up -d $NEW_ENV

    # 헬스체크
    if ! check_health $NEW_PORT; then
        echo "⚠️ 배포에 실패했습니다! 롤백을 시도합니다..."
        docker compose stop $NEW_ENV
        exit 1
    fi

    # deploy.conf 업데이트
    sudo sed -i.bak "s/server ${OLD_ENV}:${OLD_PORT} weight=1;/server ${OLD_ENV}:${OLD_PORT} backup;/" /etc/nginx/conf.d/deploy.conf
    sudo sed -i.bak "s/server ${NEW_ENV}:${NEW_PORT} backup;/server ${NEW_ENV}:${NEW_PORT} weight=1;/" /etc/nginx/conf.d/deploy.conf

    # Nginx 설정 리로드
    docker compose exec -T nginx nginx -s reload

    echo "🚧 구 버전 인스턴스에서 새 버전 인스턴스로 트래픽을 이동시키기 위해 기다리는 중..."
    sleep 30  # 이전 요청이 완료될 때까지 대기

    # prometheus 포트 변경
    sudo sed -i.bak "s/host.docker.internal:$OLD_METRIC_PORT/host.docker.internal:$NEW_METRIC_PORT weight=1;/" ./monitoring/prometheus.yaml
    docker compose restart prometheus

    echo "🚧 프로메테우스 포트를 새 인스턴스의 포트로 변경하고 있습니다..."

    sleep 30  # 이전 요청이 완료될 때까지 대기

    # 이전 환경 정리
    docker compose stop $OLD_ENV

    echo "🚀 배포가 성공적으로 완료 되었습니다!"
}

# 롤백 함수
rollback() {
    ACTIVE_ENV=$(get_active_env)

    if [ "$ACTIVE_ENV" = "blue" ]; then
        ROLLBACK_ENV="green"
        ROLLBACK_PORT=$GREEN_PORT
        ROLLBACK_METRIC_PORT=$GREEN_METRIC_PORT
        ACTIVE_PORT=$BLUE_PORT
        ACTIVE_METRIC_PORT=$BLUE_METRIC_PORT
    else
        ROLLBACK_ENV="blue"
        ROLLBACK_PORT=$BLUE_PORT
        ROLLBACK_METRIC_PORT=$BLUE_METRIC_PORT
        ACTIVE_PORT=$GREEN_PORT
        ACTIVE_METRIC_PORT=$GREEN_METRIC_PORT
    fi

    echo "🚧 $ROLLBACK_ENV 버전으로 롤백하는 중..."

    # 롤백 환경 시작
    docker compose up -d $ROLLBACK_ENV

    if ! check_health $ROLLBACK_PORT; then
        echo "⚠️ 롤백에 실패했습니다! 수동 조치가 필요합니다."
        exit 1
    fi

    # deploy.conf 업데이트
    sudo sed -i.bak "s/server ${ACTIVE_ENV}:${ACTIVE_PORT} weight=1;/server ${ACTIVE_ENV}:${ACTIVE_PORT} backup;/" /etc/nginx/conf.d/deploy.conf
    sudo sed -i.bak "s/server ${ROLLBACK_ENV}:${ROLLBACK_PORT} backup;/server ${ROLLBACK_ENV}:${ROLLBACK_PORT} weight=1;/" /etc/nginx/conf.d/deploy.conf

    # Nginx 설정 리로드
    docker compose exec -T nginx nginx -s reload

    echo "🚧 트래픽을 이동시키기 위해 기다리는 중..."
    sleep 30

    # prometheus 포트 변경
    sudo sed -i.bak "s/host.docker.internal:$ACTIVE_METRIC_PORT/host.docker.internal:$ROLLBACK_METRIC_PORT weight=1;/" ./monitoring/prometheus.yaml
    docker compose restart prometheus

    echo "🚧 프로메테우스 포트를 새 인스턴스의 포트로 변경하고 있습니다..."

    sleep 30  # 이전 요청이 완료될 때까지 대기

    # 이전 환경 정리
    docker compose stop $ACTIVE_ENV

    echo "🚀 롤백이 성공적으로 완료 되었습니다!"
}

# 스크립트 실행
case "$1" in
    "deploy")
        deploy
        ;;
    "rollback")
        rollback
        ;;
    *)
        echo "⚡️ 사용한 명령: $0 {deploy|rollback}"
        exit 1
        ;;
esac