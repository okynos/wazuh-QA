import sys
import os
import time

def GenerateAlerts(log_src, log_dst, target, sleep_sec):
    ctr = 0
    while ctr < target:   
        f = open(log_dst,'a')
        output_file = open('output.txt', 'a')
        line = open(log_src).readline()
        f.write(line)
        output_file.write("Generated %d alerts\n" % ctr)

        f.close()
        output_file.close()
        time.sleep(sleep_sec)
        ctr += 1

           
if __name__ == "__main__":
    GenerateAlerts(sys.argv[1], "/var/ossec/logs/active-responses.log", sys.argv[2], sys.argv[3])
