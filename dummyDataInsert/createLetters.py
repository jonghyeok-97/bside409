import traceback
from multiprocessing import Pool, cpu_count

import numpy as np
import pandas as pd

from ElapsedTimer import ElapsedTimer
from common import generate_unique_uuids_fast_parallel


class CreateLetters:
    def __init__(self, letter_filename, user_filename, letter_cnt, user_cnt,
                 letters_per_date, start_date, end_date, start_time, time_frequency):
        self.letter_filename = letter_filename
        self.user_filename = user_filename
        self.letter_cnt = letter_cnt
        self.user_cnt = user_cnt
        self.letters_per_date = letters_per_date
        self.start_date = start_date
        self.end_date = end_date
        self.start_time = start_time
        self.time_frequency = time_frequency
        self.timer = ElapsedTimer('letters')

    def generate_created_at(self):
        """
        각 유저가 하루에 여러 개의 편지를 특정 기간에 작성하는 시간을 생성.
        """
        # 날짜 범위 생성
        days = pd.date_range(self.start_date, self.end_date, freq='D')
        # 시간 범위 생성
        times = pd.timedelta_range(start=self.start_time, periods=self.letters_per_date, freq=self.time_frequency)

        days_repeated = np.repeat(np.repeat(days, len(times)), self.user_cnt)
        times_tiled = np.tile(np.repeat(times, self.user_cnt), len(days))
        created_at = days_repeated + times_tiled

        return pd.Series(created_at)

    # 메시지 생성 함수 (병렬 처리용)
    @staticmethod
    def generate_message(args):
        created_at_chunk, username_chunk = args
        return [
            f"{user}이 작성한 편지 - {time.strftime('%y년 %m월 %d일 %H시')} 작성"
            for user, time in zip(username_chunk, created_at_chunk)
        ]

    # 병렬 처리 실행 함수
    def parallel_generate_messages(self, username_list, created_at_list, chunk_size):
        """
        메시지 생성 작업을 병렬로 처리
        """
        # 데이터를 chunk로 나눔
        num_chunks = len(created_at_list) // chunk_size
        created_at_chunks = np.array_split(created_at_list, num_chunks)
        username_chunks = np.array_split(username_list, num_chunks)

        # 병렬 처리 설정
        with Pool(processes=cpu_count()) as pool:
            results = pool.map(
                self.generate_message,
                [(created_at_chunks[i], username_chunks[i]) for i in range(num_chunks)]
            )

        # 결과를 평탄화
        return [msg for sublist in results for msg in sublist]

    def create_letters(self):
        try:
            self.timer.start()

            user_data = pd.read_csv(self.user_filename)
            username = np.tile(user_data['username'], int(self.letter_cnt / self.user_cnt))
            letter_user_ids = np.tile(user_data['user_id'], int(self.letter_cnt / self.user_cnt))

            created_at = self.generate_created_at()

            # like_f, like_t는 사용되지 않는 필드이므로 제외
            # 'daily_report_id'는 추후 채울 것이므로 일단 제외
            letters = {
                'letter_id': generate_unique_uuids_fast_parallel(self.letter_cnt),
                'created_at': created_at,
                'message': self.parallel_generate_messages(username, created_at, max(1, len(created_at) // (cpu_count() * 4))),
                'preference': np.random.choice(['T', 'F'], size=self.letter_cnt),
                'published': np.random.choice([0, 1], size=self.letter_cnt),
                'user_id': letter_user_ids
            }

            letters_df = pd.DataFrame(letters)

            # CSV 파일에 데이터 쓰기
            letters_df.to_csv(self.letter_filename, index=False, encoding='utf-8')
            print(f"\n{self.letter_filename} 파일이 생성되었습니다.")
        except Exception as e:
            print(f"\n에러 발생: {e}")
            traceback.print_exc()
        finally:
            self.timer.stop()
            print('\nletter 생성 프로그램 종료')
