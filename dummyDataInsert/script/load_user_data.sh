#!/bin/bash

# MySQL 접속 정보
DB_USER=$1
DB_PASSWORD=$2
DB_NAME=bside

# 유저 데이터가 있는 파일
FILE="/var/lib/mysql-files/csv/user.csv"

echo "Processing $FILE..."

mysql -u "$DB_USER" -p"$DB_PASSWORD" "$DB_NAME" -e "
  LOAD DATA INFILE '$FILE'
  INTO TABLE user
  CHARACTER SET utf8mb4
  FIELDS TERMINATED BY ','
  ENCLOSED BY '\"'
  LINES TERMINATED BY '\r\n'
  IGNORE 1 LINES
  (@user_id, created_at, @agree_to_privacy_policy, @agree_to_terms, email, @is_email_ads_consented, @is_synced, nickname, preference, oauth2_provider, role, username, @isDormant)
  SET user_id = UUID_TO_BIN(@user_id),
      agree_to_privacy_policy = IF(@agree_to_privacy_policy = '1', b'1', b'0'),
      agree_to_terms = IF(@agree_to_terms = '1', b'1', b'0'),
      is_email_ads_consented = IF(@is_email_ads_consented = '1', b'1', b'0'),
      is_synced = IF(@is_synced = '1', b'1', b'0'),
      isDormant = IF(@isDormant = '1', b'1', b'0');
"

echo "✅ 사용자 데이터 로드 완료!"