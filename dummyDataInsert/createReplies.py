import sqlite3
import traceback

import numpy as np
import pandas as pd

from ElapsedTimer import ElapsedTimer
from common import generate_unique_uuids_fast_parallel

sqlite_db_path = './shared_data.db'
reply_filename = './csv/reply.csv'

def run_query():
    try:
        conn = sqlite3.connect(f'file:{sqlite_db_path}?mode=ro', uri=True)

        query = """
                SELECT letter_id, created_at FROM letter;
                """

        result = pd.read_sql_query(query, conn)
        conn.close()
        return result
    except Exception as e:
        print(f"처리 중 오류 발생: {e}")
        traceback.print_exc()
        return pd.DataFrame()


def main():
    timer = ElapsedTimer("reply 생성 작업")
    try:
        timer.start()

        result = run_query()

        letter_ids = result['letter_id']
        created_ats = result['created_at']
        letter_cnt = len(letter_ids)

        replies = {
            'reply_id': generate_unique_uuids_fast_parallel(letter_cnt),
            'letter_id': letter_ids,
            'message_t': np.repeat('t 유형 답변', letter_cnt),
            'message_f': np.repeat('f 유형 답변', letter_cnt),
            'created_at': created_ats
        }

        replies_df = pd.DataFrame(replies)

        # CSV 파일에 데이터 쓰기
        replies_df.to_csv(reply_filename, index=False, encoding='utf-8')
        print(f"{reply_filename} 파일이 생성되었습니다.")

        # letter_analysis 테이블 추가
        conn = sqlite3.connect(sqlite_db_path)  # SQLite 데이터베이스 연결
        replies_df.to_sql('reply', conn, index=False, if_exists='replace')
        conn.close()
        print("SQLite 데이터베이스에서 'reply' 테이블 업데이트 완료!")
    except Exception as e:
        print(f"\n에러 발생: {e}")
        traceback.print_exc()
    finally:
        timer.stop()
        print("reply 생성 프로그램 종료\n")


if __name__ == "__main__":
    main()
