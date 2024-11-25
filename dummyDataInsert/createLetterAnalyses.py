import sqlite3
import traceback

import numpy as np
import pandas as pd

from ElapsedTimer import ElapsedTimer
from common import generate_unique_uuids_fast_parallel

sqlite_db_path = './shared_data.db'
letter_analysis_filename = './csv/letter_analysis.csv'

def run_query():
    try:
        conn = sqlite3.connect(f'file:{sqlite_db_path}?mode=ro', uri=True)

        query = """
                SELECT l.letter_id as letter_id,
                dr.created_at as created_at
                FROM letter l JOIN daily_report dr
                ON l.daily_report_id = dr.daily_report_id
                """

        result = pd.read_sql_query(query, conn)
        conn.close()
        return result
    except Exception as e:
        print(f"처리 중 오류 발생: {e}")
        traceback.print_exc()
        return pd.DataFrame()


def main():
    timer = ElapsedTimer("letter analysis 생성 작업")
    try:
        timer.start()

        result = run_query()

        letter_ids = result['letter_id']
        created_ats = result['created_at']

        letter_analysis_cnt = len(letter_ids)
        letter_analyses = {
            'letter_analysis_id': generate_unique_uuids_fast_parallel(letter_analysis_cnt),
            'created_at': created_ats,
            'sensitive_emotions': [
                np.random.choice(['기쁨', '슬픔', '분노', '두려움', '놀라움', '혐오', '열망', '수용', '중립'],
                                 size=np.random.randint(0, 4),
                                 replace=False) for _ in range(letter_analysis_cnt)],
            'topic': [f'주제{i}' for i in range(letter_analysis_cnt)],
            'letter_id': letter_ids
        }

        letter_analysis_df = pd.DataFrame(letter_analyses)

        # CSV 파일에 데이터 쓰기
        letter_analysis_df.to_csv(letter_analysis_filename, index=False, encoding='utf-8')
        print(f"{letter_analysis_filename} 파일이 생성되었습니다.")

        # letter_analysis 테이블 추가
        conn = sqlite3.connect(sqlite_db_path)  # SQLite 데이터베이스 연결
        letter_analysis_df.to_sql('letter_analysis', conn, index=False, if_exists='replace')
        conn.close()
        print("SQLite 데이터베이스에서 'letter_analysis' 테이블 업데이트 완료!")
    except Exception as e:
        print(f"\n에러 발생: {e}")
        traceback.print_exc()
    finally:
        timer.stop()
        print("letter analysis 생성 프로그램 종료\n")


if __name__ == "__main__":
    main()
