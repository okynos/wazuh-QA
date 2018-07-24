import json
import sys
import random
import time

# Checkings
if len(sys.argv) != 3:
  print("Usage: " + sys.argv[0] + " JSONStructure JSONDeploy")
  exit()

# System configs
random.seed(time.time())

# Loading data
struct_data = json.load(open(sys.argv[1]))
deploy_data = json.load(open(sys.argv[2]))

# Private vars
agentid = 0
managerid = 0
inventory = ""
infrastructure = {}
result = []

# Constants
SSH_PORT_MIN = 10000
SSH_PORT_MAX = 20000
AUTH_PORT_MIN = 20001
AUTH_PORT_MAX = 30000
COMM_PORT_MIN = 30001
COMM_PORT_MAX = 40000

# Loading infrastructure
for element in struct_data:
  system = element['system']
  ip = element['ip']
  infrastructure[system] = ip

# Generating Deployment JSON
for element in deploy_data:
  dtype = element['type']
  dsystem = element['system']
  quantity = int(element['quantity'])
  obj = {}
  obj['type'] = dtype
  obj['system'] = dsystem
  obj['ip'] = infrastructure[dsystem]
  obj['docker_image'] = element['docker_image']

  if dtype == "agent":
    for e in range(quantity):
      ssh_port = random.randint(SSH_PORT_MIN, SSH_PORT_MAX)
      obj['sshport'] = ssh_port
      obj['id'] = agentid
      obj['name'] = "Agent" + str(agentid)
      agentid += 1
      result.append(obj.copy())
  elif dtype == "manager":
    for e in range(quantity):
      ssh_port = random.randint(SSH_PORT_MIN, SSH_PORT_MAX)
      auth_port = random.randint(AUTH_PORT_MIN, AUTH_PORT_MAX)
      comm_port = random.randint(COMM_PORT_MIN, COMM_PORT_MAX)
      obj['sshport'] = ssh_port
      obj['authport'] = auth_port
      obj['commport'] = comm_port
      obj['id'] = managerid
      obj['name'] = "Manager" + str(managerid)
      managerid += 1
      result.append(obj.copy())    

print json.dumps(result)
