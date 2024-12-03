from collections import Counter

import pandas as pd

from ElapsedTimer import ElapsedTimer
from common import generate_unique_uuids_fast_parallel
from createLetters import CreateLetters


def test_generate_unique_uuids_fast_parallel():
    count = 15_000_000
    timer = ElapsedTimer('test uuid 생성')

    # 생성된 UUID 개수 확인
    try:
        timer.start()
        uuids = generate_unique_uuids_fast_parallel(count)
        print(f"\n생성된 UUID 개수: {len(uuids)}")
        print(f"중복이 없는지 확인: {len(uuids) == count}")
    finally:
        timer.stop()
        print('테스트 종료')


def test_generate_created_at():
    # 테스트용 데이터 초기화
    timer = ElapsedTimer('test createdAt 생성')
    letter_filename = 'test_letters.csv'
    user_filename = 'test_users.csv'
    letter_cnt = 15_000_000
    user_cnt = 100_000
    letters_per_date = 5
    start_date = '2024-10-01'
    end_date = '2024-10-30'
    start_time = '9H'
    time_frequency = '3H'

    try:
        timer.start()

        # CreateLetters 클래스 초기화
        generator = CreateLetters(
            letter_filename, user_filename, letter_cnt, user_cnt,
            letters_per_date, start_date, end_date, start_time, time_frequency
        )

        # generate_created_at 실행
        created_at = generator.generate_created_at()

        # 테스트 출력
        print(f"\n생성된 created_at 길이: {len(created_at)}")
        print(f"\ncreated_at 예시:\n{created_at[:10]}")  # 처음 10개 값 확인

        counter = Counter(created_at)
        print(f"\n시간별 데이터 개수 확인: {counter}")

        # 검증 조건
        expected_length = len(pd.date_range(start_date, end_date, freq='D')) * letters_per_date * user_cnt
        assert len(created_at) == expected_length, f"길이 불일치! 예상: {expected_length}, 실제: {len(created_at)}"
    finally:
        timer.stop()
        print("테스트 성공: generate_created_at 함수가 예상대로 동작합니다!")


if __name__ == "__main__":
    print("generate_unique_uuids_fast_parallel 테스트 시작")
    test_generate_unique_uuids_fast_parallel()

    print("\ngenerate_created_at 테스트 시작")
    test_generate_created_at()
