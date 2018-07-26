# whodata.py
import time
import sys

def generate_whodata_alert():
  limit = int(sys.argv[2])
  filename = sys.argv[1]
  for i in range(limit):
    whodata_file = open(filename, "w")
    whodata_file.write(str(i))
    print(i)
    whodata_file.close()
    time.sleep(1)


generate_whodata_alert()
