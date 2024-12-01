import os

import pandas as pd


def split_csv_file(filename) :
    # 원본 CSV 파일 경로
    input_file = filename

    # 출력 디렉토리
    output_dir = os.path.basename(filename).split('.')[0]
    os.makedirs(f"./csv/{output_dir}", exist_ok=True)  # 디렉토리 생성

    # 한 파일당 최대 행 수
    rows_per_file = 100000

    # CSV 파일 읽기
    df = pd.read_csv(input_file, low_memory=False)

    # 행 기준으로 분할
    for i in range(0, len(df), rows_per_file):
        split_df = df[i:i+rows_per_file]  # 일정한 행만 선택
        output_file = f"./csv/{output_dir}/{output_dir}_{i // rows_per_file + 1}.csv"
        split_df.to_csv(output_file, index=False, encoding='utf-8')
        print(f"파일 생성: {output_file}")


if __name__ == "__main__":
    split_csv_file("./csv/letter.csv")
    split_csv_file("./csv/daily_report.csv")
    split_csv_file("./csv/letter_analysis.csv")
    split_csv_file("./csv/letter_core_emotions.csv")
    split_csv_file("./csv/reply.csv")
