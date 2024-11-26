import uuid
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
        uuids.add(uuid.uuid4())
    return uuids


def generate_uuids_with_uuid4(chunk_size):
    """
    uuid.uuid4()를 활용해 UUID 생성
    """
    return [str(uuid.uuid4()) for _ in range(chunk_size)]


def generate_unique_uuids_fast_parallel(count):
    """
    병렬 + uuid.uuid4()를 활용한 빠른 UUID 생성
    """
    cpu_cores = cpu_count()
    chunk_size = count // cpu_cores

    with Pool(processes=cpu_cores) as pool:
        results = pool.map(generate_uuids_with_uuid4, [chunk_size] * cpu_cores)

    # 병렬 처리 결과를 평탄화
    uuids = {u for sublist in results for u in sublist}

    while len(uuids) < count:
        # 부족한 개수만큼 추가로 생성
        uuids.update(str(uuid.uuid4()) for _ in range(count - len(uuids)))

    return list(uuids)