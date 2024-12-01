#!/bin/bash

# Docker 컨테이너 이름
MYSQL_CONTAINER="bside-rdb"

# 명령행 인수로 전달받은 소스 디렉토리
CSV_SOURCE_DIR=$1 # csv
SCRIPT_SOURCE_DIR=$2 # script
DB_NAME="bside"
DB_USER=$3
DB_PASSWORD=$4
MAX_JOBS=$5
BUFFER_SIZE=$6
LOG_BUFFER_SIZE=$7

# 타겟 디렉토리
TARGET_DIR="/var/lib/mysql-files"

# 1. CSV 파일 및 스크립트 컨테이너로 복사
echo "📁 CSV 파일 및 Bash 스크립트를 Docker 컨테이너로 복사 중..."
docker cp "$CSV_SOURCE_DIR" "$MYSQL_CONTAINER:$TARGET_DIR"
docker cp "$SCRIPT_SOURCE_DIR" "$MYSQL_CONTAINER:$TARGET_DIR"

# 2. 컨테이너 내에서 데이터 로드 속도 조정 스크립트 실행
echo "🔧 데이터 로드를 위한 세팅 중..."

docker exec -it "$MYSQL_CONTAINER" bash -c "
  mysql -u \"$DB_USER\" -p\"$DB_PASSWORD\" \"$DB_NAME\" -e \"
  -- 성능 최적화 설정
  SET UNIQUE_CHECKS = 0;
  SET FOREIGN_KEY_CHECKS = 0;
  SET autocommit = 0;
  SET GLOBAL innodb_buffer_pool_size = $BUFFER_SIZE * 1024 * 1024;
  SET GLOBAL innodb_log_buffer_size = $LOG_BUFFER_SIZE * 1024 * 1024;
  SET GLOBAL innodb_io_capacity=2000;
  SET GLOBAL innodb_flush_log_at_trx_commit = 2;
  ALTER TABLE daily_report DISABLE KEYS;
  ALTER TABLE letter DISABLE KEYS;
  ALTER TABLE letter_analysis DISABLE KEYS;
  ALTER TABLE letter_core_emotions DISABLE KEYS;
  \"
"

# 3. 데이터 로드 스크립트 실행
echo "🚀 데이터 로드 작업 실행 중..."

# 사용자 정보 로드
echo "👤 user table에 load data infile 실행 중..."
docker exec -it "$MYSQL_CONTAINER" bash -c "
  cd $TARGET_DIR/script
  chmod +x load_user_data.sh
  ./load_user_data.sh $DB_USER $DB_PASSWORD
"

# 일일 리포트 정보 로드
echo "📝 daily_report table에 load data infile 실행 중..."
docker exec -it "$MYSQL_CONTAINER" bash -c "
  cd $TARGET_DIR/script
  chmod +x load_daily_report_data.sh
  ./load_daily_report_data.sh $DB_USER $DB_PASSWORD $MAX_JOBS
"

# 편지 정보 로드
echo "✉️ letter table에 load data infile 실행 중..."
docker exec -it "$MYSQL_CONTAINER" bash -c "
  cd $TARGET_DIR/script
  chmod +x load_letter_data.sh
  ./load_letter_data.sh $DB_USER $DB_PASSWORD $MAX_JOBS
"

# 편지 분석 정보 로드
echo "📄 letter_analysis table에 load data infile 실행 중..."
docker exec -it "$MYSQL_CONTAINER" bash -c "
  cd $TARGET_DIR/script
  chmod +x load_letter_analysis_data.sh
  ./load_letter_analysis_data.sh $DB_USER $DB_PASSWORD $MAX_JOBS
"

# 편지별 분석된 대표 감정 정보 로드
echo "📄 letter_core_emotions table에 load data infile 실행 중..."
docker exec -it "$MYSQL_CONTAINER" bash -c "
  cd $TARGET_DIR/script
  chmod +x load_letter_core_emotions_data.sh
  ./load_letter_core_emotions_data.sh $DB_USER $DB_PASSWORD $MAX_JOBS
"

# 편지별 답변 정보 로드
echo "🗨️ reply table에 load data infile 실행 중..."
docker exec -it "$MYSQL_CONTAINER" bash -c "
  cd $TARGET_DIR/script
  chmod +x load_reply_data.sh
  ./load_reply_data.sh $DB_USER $DB_PASSWORD $MAX_JOBS
"

# 4. 변경했던 설정 복구 스크립트 실행
echo "🔧 설정 복구 작업 시작..."

docker exec -it "$MYSQL_CONTAINER" bash -c "
  mysql -u \"$DB_USER\" -p\"$DB_PASSWORD\" \"$DB_NAME\" -e \"
  -- 성능 복구 설정
  SET UNIQUE_CHECKS = 1;
  SET FOREIGN_KEY_CHECKS = 1;
  SET autocommit = 1;
  SET GLOBAL innodb_buffer_pool_size = 134217728;  -- 기본값 초기화
  SET GLOBAL innodb_log_buffer_size = 16777216;
  SET GLOBAL innodb_io_capacity=200;
  SET GLOBAL innodb_flush_log_at_trx_commit = 1;
  ALTER TABLE daily_report ENABLE KEYS;
  ALTER TABLE letter ENABLE KEYS;
  ALTER TABLE letter_analysis ENABLE KEYS;
  ALTER TABLE letter_core_emotions ENABLE KEYS;
  \"
"

# 5. 실행 완료 메시지 출력
echo "✅ 모든 데이터 로드 작업 및 설정 초기화가 완료되었습니다!"
