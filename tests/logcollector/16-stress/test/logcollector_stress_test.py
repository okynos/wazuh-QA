import sys
import os
import time
import subprocess

def test_alerts_agent(output, target):
    ctr = 0
    generated_alerts = subprocess.check_output(['wc', '-l', output]).split(' ')[0]

    print("Generated alerts: " + generated_alerts)
    print("Target alerts: " + target)
    assert int(generated_alerts) == int(target)
           
if __name__ == "__main__":
    test_alerts_agent(sys.argv[1], sys.argv[2])
