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

agents = []

for i in range(num_agents+4):
  obj = {}
  obj['alerts_generated'] = 0
  obj['alerts_whodata'] = 0
  obj['id'] = 0
  obj['name'] = ""
  agents.append(obj.copy())

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
        ):
      if alert['syscheck']['path'] == test_filepath and alert['syscheck']['event'] == test_eventtype:
        audit_ctr += 1
        agentid = int(alert['agent']['id'])
        agents[agentid]['name'] = alert['agent']['name']
        agents[agentid]['id'] = agentid
        agents[agentid]['alerts_generated'] += 1
        agents[agentid]['alerts_whodata'] += 1
    else:
      agentid = int(alert['agent']['id'])
      agents[agentid]['alerts_generated'] += 1

for agent in agents:
  if agent['id'] == 0:
    agents.remove(agent)

f = open(result_file, 'w')
f.write("Total alerts in file: " + str(total_ctr) + "\n")
f.write("Total audit alerts detected: " + str(audit_ctr) + "\n")
f.write("Expected audit alerts: " + str(num_agents*run_time) + "\n")
f.write("Agents registered -> " + json.dumps(agents, indent=2))
      












