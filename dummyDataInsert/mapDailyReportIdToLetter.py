from concurrent.futures import ProcessPoolExecutor
import sqlite3
import pandas as pd
import traceback
from ElapsedTimer import ElapsedTimer

# CSV 파일 경로
user_csv = './csv/user.csv'
letter_csv = './csv/letter.csv'
daily_report_csv = './csv/daily_report.csv'

sqlite_db_path = './shared_data.db'  # SQLite 파일 경로
output_csv = './csv/updated_letter.csv'


def initialize_database():
    print("SQLite 데이터베이스 초기화 중...")
    conn = sqlite3.connect(sqlite_db_path)  # 파일 기반 데이터베이스 생성

    # 데이터 로드
    print('user 데이터 로드 중...')
    pd.read_csv(user_csv).to_sql('user', conn, index=False, if_exists='replace')
    print('letter 데이터 로드 중...')
    pd.read_csv(letter_csv).to_sql('letter', conn, index=False, if_exists='replace')
    print('daily_report 데이터 로드 중...')
    pd.read_csv(daily_report_csv).to_sql('daily_report', conn, index=False, if_exists='replace')

    # 인덱스 생성
    print("인덱스 생성 중...")
    conn.execute("CREATE INDEX idx_user_id ON user(user_id);")
    conn.execute("CREATE INDEX idx_letter_user_id ON letter(user_id);")
    conn.execute("CREATE INDEX idx_letter_created_at ON letter(created_at);")
    conn.execute("CREATE INDEX idx_daily_report_created_at ON daily_report(created_at);")
    conn.execute("CREATE INDEX idx_letter_user_created_at ON letter(user_id, created_at);")
    conn.commit()
    conn.close()
    print("SQLite 데이터베이스 초기화 완료!")


def run_query_for_date(date_str):
    try:
        print(f"쿼리 실행 중: {date_str}")
        conn = sqlite3.connect(f'file:{sqlite_db_path}?mode=ro', uri=True)  # read-only 모드

        start_time = f"{date_str} 00:00:00"
        end_time = f"{date_str} 23:59:59"

        # 쿼리 작성
        query = f"""
        WITH sampled_users AS (
            SELECT user_id, ROW_NUMBER() OVER (ORDER BY RANDOM()) AS user_rank
            FROM user
            LIMIT 50000
        ),
        ordered_daily_reports AS (
            SELECT 
                dr.daily_report_id, 
                dr.created_at,
                ROW_NUMBER() OVER (ORDER BY dr.created_at ASC) AS report_rank
            FROM daily_report dr
            WHERE dr.created_at BETWEEN '{start_time}' AND '{end_time}'
        ),
        mapped_users_to_reports AS (
            SELECT 
                su.user_id,
                dr.daily_report_id,
                dr.created_at AS report_created_at
            FROM sampled_users su
            JOIN ordered_daily_reports dr
              ON su.user_rank = dr.report_rank
        ),
        letters_with_reports AS (
            SELECT 
                l.letter_id,
                l.user_id,
                l.created_at AS letter_created_at,
                dr.daily_report_id,
                dr.report_created_at,
                ROW_NUMBER() OVER (
                    PARTITION BY l.user_id, dr.daily_report_id
                    ORDER BY l.created_at ASC
                ) AS letter_rank
            FROM letter l
            JOIN mapped_users_to_reports dr
              ON l.user_id = dr.user_id
             AND DATE(l.created_at) = DATE(dr.report_created_at)
             AND l.created_at < dr.report_created_at
        )
        SELECT 
            letter_id, 
            daily_report_id
        FROM letters_with_reports
        WHERE letter_rank <= 3;
        """
        result = pd.read_sql_query(query, conn)
        conn.close()
        print(f"{date_str} 처리 완료")
        return result
    except Exception as e:
        print(f"{date_str} 처리 중 오류 발생: {e}")
        traceback.print_exc()
        return pd.DataFrame()


def main():
    timer = ElapsedTimer("daily report id mapping")
    timer.start()

    try:
        print("날짜별 SQL 쿼리 병렬 처리 시작...")
        start_date = '2024-10-01'
        end_date = '2024-10-30'
        date_list = pd.date_range(start=start_date, end=end_date).strftime('%Y-%m-%d')

        # 데이터베이스 초기화 (한 번만 실행)
        # initialize_database()

        with ProcessPoolExecutor(max_workers=8) as executor:
            all_results = list(executor.map(run_query_for_date, date_list))

        print("모든 날짜 쿼리 완료. 결과 병합 중...")
        final_result = pd.concat(all_results, ignore_index=True)

        # 원본 letter.csv 데이터와 병합
        letters = pd.read_csv(letter_csv)
        updated_letters = pd.merge(letters, final_result, on='letter_id', how='left')

        # 결과 저장
        updated_letters.to_csv(output_csv, index=False)
        print("최종 결과 저장 완료!")
    except Exception as e:
        print(f"\n에러 발생: {e}")
        traceback.print_exc()
    finally:
        timer.stop()
        print("프로그램 완료")


if __name__ == "__main__":
    main()
