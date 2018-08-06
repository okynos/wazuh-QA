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
manager_machine = ""

# Constants
SSH_PORT_MIN = 10000
SSH_PORT_MAX = 20000
AUTH_PORT_MIN = 20001
AUTH_PORT_MAX = 30000
COMM_PORT_MIN = 30001
COMM_PORT_MAX = 40000
IP_OCTET_MIN = 2
IP_OCTET_MAX = 255

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
  obj['deploy_ip'] = infrastructure[dsystem]
  obj['docker_image'] = element['docker_image']
  

  if dtype == "agent":
    for e in range(quantity):
      if manager_machine != "" and manager_machine == dsystem:
        obj['location'] = "internal"
      else:
        obj['location'] = "external"
      ip_third_octet = random.randint(IP_OCTET_MIN, IP_OCTET_MAX)
      ip_fourth_octet = random.randint(IP_OCTET_MIN, IP_OCTET_MAX)
      ssh_port = random.randint(SSH_PORT_MIN, SSH_PORT_MAX)
      obj['sshport'] = ssh_port
      obj['container_ip'] = "172.100." + str(ip_third_octet) + "." + str(ip_fourth_octet)
      obj['id'] = agentid
      obj['name'] = "Agent" + str(agentid)
      agentid += 1
      result.append(obj.copy())
  elif dtype == "manager":
    manager_machine = dsystem
    for e in range(quantity):
      ip_third_octet = random.randint(IP_OCTET_MIN, IP_OCTET_MAX)
      ip_fourth_octet = random.randint(IP_OCTET_MIN, IP_OCTET_MAX)
      ssh_port = random.randint(SSH_PORT_MIN, SSH_PORT_MAX)
      auth_port = random.randint(AUTH_PORT_MIN, AUTH_PORT_MAX)
      comm_port = random.randint(COMM_PORT_MIN, COMM_PORT_MAX)
      obj['sshport'] = ssh_port
      obj['authport'] = auth_port
      obj['commport'] = comm_port
      obj['container_ip'] = "172.100." + str(ip_third_octet) + "." + str(ip_fourth_octet)
      obj['id'] = managerid
      obj['name'] = "Manager" + str(managerid)
      obj['location'] = "external"
      managerid += 1
      result.append(obj.copy())    

print json.dumps(result)
