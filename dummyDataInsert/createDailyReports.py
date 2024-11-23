import traceback

import numpy as np
import pandas as pd

from ElapsedTimer import ElapsedTimer
from common import generate_unique_uuids_fast_parallel


class CreateDailyReports:
    def __init__(self, daily_report_filename, user_filename, letter_filename,
                 user_cnt, letter_cnt, daily_report_cnt,
                 start_date, end_date, generation_time):
        self.daily_report_filename = daily_report_filename
        self.user_filename = user_filename
        self.letter_filename = letter_filename
        self.user_cnt = user_cnt
        self.letter_cnt = letter_cnt
        self.daily_report_cnt = daily_report_cnt
        self.start_date = start_date
        self.end_date = end_date
        self.generation_time = generation_time
        self.timer = ElapsedTimer('daily reports')


    def generate_created_at(self):
        """
        매일 전체 유저의 절반이 일일 리포트를 생성한다고 가정하고 해당 일일 리포트 생성 시간을 생성.
        """
        # 날짜 범위 생성
        days = pd.date_range(self.start_date, self.end_date, freq='D')
        generate_time = pd.Timedelta(self.generation_time)

        days_repeated = np.repeat(days, self.user_cnt // 2)
        times_repeated = pd.to_timedelta(np.tile(generate_time, len(days) * self.user_cnt // 2))

        created_at = days_repeated + times_repeated

        return pd.Series(created_at)


    def create_daily_reports(self):
        try:
            self.timer.start()

            created_at = self.generate_created_at()
            core_emotion = np.random.choice(['기쁨', '슬픔', '분노', '두려움', '놀라움', '혐오', '열망', '수용', '중립'],
                                      size=self.daily_report_cnt)

            # weekly_report_id는 일단 생략
            daily_reports = {
                'daily_report_id': generate_unique_uuids_fast_parallel(self.daily_report_cnt),
                'created_at': created_at,
                'core_emotion': core_emotion,
                'description': [f"{t.strftime('%y년 %m일 %d일 %H시')} 해석 - 주요 감정 : {emo}" for emo, t in zip(core_emotion, created_at)],
                'target_date': np.repeat(pd.date_range(self.start_date, self.end_date, freq='D'), self.user_cnt // 2),
            }

            daily_reports_df = pd.DataFrame(daily_reports)

            # CSV 파일에 데이터 쓰기
            daily_reports_df.to_csv(self.daily_report_filename, index=False, encoding='utf-8')
            print(f"{self.daily_report_filename} 파일이 생성되었습니다.")
        except Exception as e:
            print(f"\n에러 발생: {e}")
            traceback.print_exc()
        finally:
            self.timer.stop()
            print("\ndaily report 생성 프로그램 종료")