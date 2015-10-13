import multiprocessing


def loop():
    while True:
        pass


[multiprocessing.Process(target=loop).start() for i in range(multiprocessing.cpu_count())]
