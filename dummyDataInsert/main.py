from createLetters import CreateLetters
from createUsers import CreateUsers


def __main__():
    user_filename = './csv/user.csv'
    user_cnt = 100_000
    user_created_at_start_date = '2024-09-01'
    user_created_at_end_date = '2024-09-30'

    letter_filename = './csv/letter.csv'
    letter_cnt = 15_000_000
    letters_per_date = 5
    letter_created_at_start_date = '2024-10-01'
    letter_created_at_end_date = '2024-10-30'
    letter_created_at_start_time = '9h'
    letter_created_at_time_frequency = '3h'

    daily_report_cnt = 1_500_000

    # user 100k 생성
    userGenerator = CreateUsers(
        user_filename=user_filename,
        user_cnt=user_cnt,
        start_date=user_created_at_start_date,
        end_date=user_created_at_end_date,
        time_frequency='D',
    )
    userGenerator.create_users()

    # letter 1500k 생성
    letterGenerator = CreateLetters(
        letter_filename=letter_filename,
        user_filename=user_filename,
        letter_cnt=letter_cnt,
        user_cnt=user_cnt,

        letters_per_date=letters_per_date,
        start_date=letter_created_at_start_date,
        end_date=letter_created_at_end_date,

        start_time=letter_created_at_start_time,
        time_frequency=letter_created_at_time_frequency,
    )
    letterGenerator.create_letters()


if __name__ == "__main__":
    __main__()