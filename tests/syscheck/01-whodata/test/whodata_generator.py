# whodata.py
import time
import sys

def generate_whodata_alert():
  whodata_file = open("whodata.txt", "w")
  limit = int(sys.argv[1])
  for i in range(limit):
    whodata_file.write(str(i))
    print(i)
    time.sleep(1)


generate_whodata_alert()
