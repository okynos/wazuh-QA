# whodata.py
import os
import sys 
import time
import json

test_filepath = "/whodata/whodata.txt"
test_eventtype = "modified"

json_file = sys.argv[1]
result_file = sys.argv[2]
run_time = int(sys.argv[3])
num_agents = int(sys.argv[4])

total_ctr = 0
audit_ctr = 0

agents = [None] * (num_agents+1)

for i in range(len(agents)):
  obj = {}
  obj['generated_alerts'] = 0
  obj['whodata_alerts'] = 0
  obj['id'] = 0
  obj['name'] = ""
  agents[i] = obj.copy()

with open(json_file) as input_file:
  for line in input_file:
    alert = json.loads(line)
    total_ctr += 1

    if (
          'syscheck' in alert
          and 'audit' in alert['syscheck']
          and 'login_user' in alert['syscheck']['audit']
          and 'effective_user' in alert['syscheck']['audit']
          and 'user' in alert['syscheck']['audit']
          and 'group' in alert['syscheck']['audit']
          and alert['syscheck']['path'] == test_filepath 
          and alert['syscheck']['event'] == test_eventtype
        ):
        audit_ctr += 1
        agentid = int(alert['agent']['id'])
        agents[agentid]['name'] = alert['agent']['name']
        agents[agentid]['id'] = agentid
        agents[agentid]['generated_alerts'] += 1
        agents[agentid]['whodata_alerts'] += 1
    else:
      agentid = int(alert['agent']['id'])
      agents[agentid]['generated_alerts'] += 1


f = open(result_file, 'w')
f.write("Total alerts in file: " + str(total_ctr) + "\n")
f.write("Total audit alerts detected: " + str(audit_ctr) + "\n")
f.write("Expected audit alerts: " + str(num_agents*run_time) + "\n")
f.write("Agents registered -> " + json.dumps(agents, indent=2))
      












