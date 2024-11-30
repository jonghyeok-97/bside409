import sqlite3
import traceback

import numpy as np
import pandas as pd

from ElapsedTimer import ElapsedTimer

sqlite_db_path = './shared_data.db'
letter_core_emotions_filename = './csv/letter_core_emotions.csv'

def run_query():
    try:
        conn = sqlite3.connect(f'file:{sqlite_db_path}?mode=ro', uri=True)

        query = """
                SELECT letter_analysis_id FROM letter_analysis
                """

        result = pd.read_sql_query(query, conn)
        conn.close()
        return result
    except Exception as e:
        print(f"처리 중 오류 발생: {e}")
        traceback.print_exc()
        return pd.DataFrame()


def main():
    timer = ElapsedTimer("letter core emotions 생성 작업")
    try:
        timer.start()

        result = run_query()

        letter_analysis_ids = result['letter_analysis_id']
        core_emotions = ['기쁨', '슬픔', '분노', '두려움', '놀라움', '혐오', '열망', '수용', '중립']

        letter_core_emotions = {
            'letter_analysis_id': [],
            'core_emotion': []
        }

        for letter_analysis_id in letter_analysis_ids:
            repeat_cnt = np.random.randint(0, 4)
            chosen_emotions = np.random.choice(core_emotions, size=repeat_cnt, replace=False)

            for emotion in chosen_emotions:
                letter_core_emotions['letter_analysis_id'].append(letter_analysis_id)
                letter_core_emotions['core_emotion'].append(emotion)

        letter_core_emotions_df = pd.DataFrame(letter_core_emotions)

        # CSV 파일에 데이터 쓰기
        letter_core_emotions_df.to_csv(letter_core_emotions_filename, index=False, encoding='utf-8')
        print(f"{letter_core_emotions_filename} 파일이 생성되었습니다.")

        # letter_analysis 테이블 추가
        conn = sqlite3.connect(sqlite_db_path)  # SQLite 데이터베이스 연결
        letter_core_emotions_df.to_sql('letter_core_emotions', conn, index=False, if_exists='replace')
        conn.close()
        print("SQLite 데이터베이스에서 'letter_core_emotions' 테이블 업데이트 완료!")
    except Exception as e:
        print(f"\n에러 발생: {e}")
        traceback.print_exc()
    finally:
        timer.stop()
        print("letter analysis 생성 프로그램 종료\n")


if __name__ == "__main__":
    main()
