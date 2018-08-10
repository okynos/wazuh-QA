# whodata.py
import time
import sys

def generate_whodata_alert():
  limit = int(sys.argv[2])
  filename = sys.argv[1]
  interval = int(sys.argv[3])
  for i in range(limit):
    whodata_file = open(filename, "a")
    whodata_file.write(str(i)+"\n")
    print(i)
    whodata_file.close()
    time.sleep(interval)


generate_whodata_alert()
