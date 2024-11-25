import traceback

from joblib import Parallel, delayed
import numpy as np
import pandas as pd
from common import generate_unique_uuids_fast_parallel
from ElapsedTimer import ElapsedTimer


class CreateDailyReports:
    def __init__(self, daily_report_filename, letter_filename,
                 user_cnt, letter_cnt, daily_report_cnt,
                 start_date, end_date, generation_time):
        self.daily_report_filename = daily_report_filename
        self.letter_filename = letter_filename
        self.user_cnt = user_cnt
        self.letter_cnt = letter_cnt
        self.daily_report_cnt = daily_report_cnt
        self.start_date = start_date
        self.end_date = end_date
        self.generation_time = generation_time
        self.timer = ElapsedTimer('daily reports')

    def generate_created_at(self):
        days = pd.date_range(self.start_date, self.end_date, freq='D')
        times = [pd.Timedelta(t) for t in self.generation_time]
        created_at = [
            d + np.random.choice(times) for d in np.repeat(days, self.user_cnt // 2)
        ]
        return pd.Series(created_at)

    @staticmethod
    def process_chunk(daily_group, daily_report_ids, created_at):
        """
        병렬 처리 함수
        """
        user_groups = {user_id: group for user_id, group in daily_group.groupby('user_id')}
        updated_ids = [None] * len(daily_group)

        selected_users = np.random.choice(list(user_groups.keys()), size=len(user_groups) // 2, replace=False)
        id_pointer = 0

        for user_id in selected_users:
            user_letters = user_groups[user_id]
            valid_letters = user_letters[user_letters['created_at'].dt.date < created_at]

            if valid_letters.empty:
                continue

            num_ids = min(len(valid_letters), len(daily_report_ids) - id_pointer)
            if num_ids <= 0:
                break

            selected_indices = valid_letters.index[:num_ids]
            updated_ids[selected_indices] = daily_report_ids[id_pointer:id_pointer + num_ids]
            id_pointer += num_ids

        return updated_ids, daily_group.index

    def create_daily_reports(self):
        try:
            self.timer.start()

            # 1. 데이터 준비
            created_at = self.generate_created_at()
            core_emotion = np.random.choice(
                ['기쁨', '슬픔', '분노', '두려움', '놀라움', '혐오', '열망', '수용', '중립'],
                size=self.daily_report_cnt
            )

            daily_reports = {
                'daily_report_id': generate_unique_uuids_fast_parallel(self.daily_report_cnt),
                'created_at': created_at,
                'core_emotion': core_emotion,
                'description': [f"{t.strftime('%y년 %m일 %d일 %H시')} 주요 감정: {emo}" for emo, t in
                                zip(core_emotion, created_at)],
                'target_date': created_at.dt.date  # target_date를 date 형식으로 변환
            }
            daily_reports_df = pd.DataFrame(daily_reports)

            # 2. 데이터 로드 및 전처리
            letters_data = pd.read_csv(self.letter_filename)
            letters_data['created_at'] = pd.to_datetime(letters_data['created_at'])
            letters_data['created_date'] = letters_data['created_at'].dt.date

            grouped_letters = letters_data.groupby('created_date')

            # 3. 병렬 처리 (Joblib 사용)
            results = Parallel(n_jobs=6)(
                delayed(self.process_chunk)(group, daily_reports_df[daily_reports_df['target_date'] == date]['daily_report_id'].values, date)
                for date, group in grouped_letters
            )

            # 4. 병렬 처리 결과 병합
            updated_daily_report_ids = [None] * len(letters_data)
            for updated_ids, indices in results:
                for idx, val in zip(indices, updated_ids):
                    updated_daily_report_ids[idx] = val

            # 5. 결과 저장
            letters_data['daily_report_id'] = updated_daily_report_ids
            letters_data.drop(columns=['created_date'], inplace=True)
            letters_data.to_csv(self.letter_filename, index=False, encoding='utf-8')

        except Exception as e:
            print(f"Error occurred: {e}")
            traceback.print_exc()
        finally:
            self.timer.stop()
            print(f"\n{self.daily_report_filename}파일이 생성되었습니다.")
