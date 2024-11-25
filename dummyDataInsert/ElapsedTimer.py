import time
import threading
import warnings
warnings.filterwarnings('ignore', category=FutureWarning)


class ElapsedTimer:
    def __init__(self, name):
        self.name = name
        self.stop_event = threading.Event()
        self.thread = None

    def _show_elapsed_time(self):
        start_time = time.time()
        while not self.stop_event.is_set():  # 종료 신호가 없으면 계속 실행
            elapsed_time = time.time() - start_time
            print(f"\r{self.name} 생성 경과 시간: {elapsed_time:.2f}초", end="", flush=True)
            time.sleep(1)  # 1초마다 업데이트

    def start(self):
        # `stop_event`를 초기화
        self.stop_event.clear()
        if self.thread is None or not self.thread.is_alive():
            self.thread = threading.Thread(target=self._show_elapsed_time, daemon=True)
            self.thread.start()

    def stop(self):
        if self.thread and self.thread.is_alive():
            self.stop_event.set()  # 종료 신호 설정
            self.thread.join()  # 스레드 종료 대기
            self.thread = None  # 스레드 재사용 가능하도록 초기화
