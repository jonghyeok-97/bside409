import uuid
import numpy as np
from multiprocessing import Pool, cpu_count


def generate_unique_uuids(count):
    """  
    겹치지 않는 UUID를 지정된 개수만큼 생성합니다.  

    Args:
        count (int): 생성하고 싶은 UUID 개수

    Returns:
        set: 생성된 고유 uuid 집합 (이때 uuid는 bytes)
    """
    uuids = set()
    while len(uuids) < count:
        uuids.add(uuid.uuid4().bytes)
    return uuids


def generate_random_bytes(chunk_size):
    """
    NumPy를 활용해 무작위 바이트 생성
    """
    random_bytes = np.random.bytes(16 * chunk_size)
    return [random_bytes[i:i+16] for i in range(0, len(random_bytes), 16)]


def generate_unique_uuids_fast_parallel(count):
    """
    병렬 + NumPy를 활용한 빠른 UUID 생성
    """
    cpu_cores = cpu_count()
    chunk_size = count // cpu_cores

    with Pool(processes=cpu_cores) as pool:
        results = pool.map(generate_random_bytes, [chunk_size] * cpu_cores)

    # 병렬 처리 결과를 평탄화
    uuids = {uuid.UUID(bytes=b).bytes for sublist in results for b in sublist}

    while len(uuids) < count:
        # 부족한 개수만큼 추가로 생성
        additional_bytes = np.random.bytes(16 * (count - len(uuids)))
        uuids.update(
            uuid.UUID(bytes=additional_bytes[i:i + 16]).bytes
            for i in range(0, len(additional_bytes), 16)
        )

    return list(uuids)