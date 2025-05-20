import asyncio
import aiohttp
import time
from collections import Counter

URL_UNAUTHORIZED = "http://localhost:8080/"
URL_AUTHORIZED = "http://localhost:8080/create"
REQUESTS_PER_SECOND = 30

# Получить свой JMT можно авторизовавшись через UI и через инструмент разработчика достать его из Cookie в поле JMT
JWT_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsInVzZXJOYW1lIjoic2hldmx5YWtvdmFzb2ZpYUBpY2xvdWQuY29tIiwiaWF0IjoxNzQ3NzUzOTk2LCJleHAiOjE3NDc3NTc1OTZ9.S0yYAkA2qu6XSuY2zgTsbg2P1bjVXft6yJsEntgtTjc"

status_counter = Counter()

async def send_request(session, i, url):
    try:
        if (url == URL_AUTHORIZED):
             cookies = {"jwt": JWT_TOKEN}
             async with session.get(URL_AUTHORIZED, cookies=cookies) as response:
                 status = response.status
                 status_counter[status] += 1
                 print(f"[{i}] Authorized Status: {status}")
        else:
            async with session.get(URL_UNAUTHORIZED) as response:
                status = response.status
                status_counter[status] += 1
                print(f"[{i}] Status: {status}")
    except Exception as e:
        print(f"[{i}] Error: {e}")
        status_counter["error"] += 1

async def request_batch(url):
    tasks = []
    async with aiohttp.ClientSession() as session:
        for i in range(REQUESTS_PER_SECOND):
            tasks += [send_request(session, i, url)]
        await asyncio.gather(*tasks)
        print("Response status counts:")
        for code, count in status_counter.items():
            print(f"  {code}: {count}")

if __name__ == "__main__":
    asyncio.run(request_batch(URL_UNAUTHORIZED))
    status_counter.clear()
    time.sleep(1)

    asyncio.run(request_batch(URL_AUTHORIZED))
