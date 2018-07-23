import json
import sys

# Checkings
if len(sys.argv) != 3:
  print("Usage: " + sys.argv[0] + " [deploy/config] JSONFile")
  exit()

# Loading data
json_data = json.load(open(sys.argv[2]))
goal = sys.argv[1].lower()


if goal != "deploy" and goal != "config":
  print("Usage: " + sys.argv[0] + " [deploy/config] JSONFile")
  exit()

# Private vars
inventory = ""
agents = []
managers = []

# Generating Deployment JSON
for element in json_data:
  dtype = element['type']
  dsystem = element['system']
  ssh_port = element['sshport']
  ip = element['ip']
  name = element['name']
  obj = {}
  obj['system'] = dsystem
  obj['type'] = dtype
  obj['sshport'] = ssh_port
  obj['ip'] = ip
  obj['name'] = name

  if dtype == "agent":
    agents.append(obj.copy())

  if dtype == "manager":
    obj['commport'] = element['commport']
    obj['authport'] = element['authport'] 
    managers.append(obj.copy())


inventory += "[Agents]\n"
for agent in agents:
  inventory += agent['name'] + " ansible_host=" + agent['ip']
  if goal == "deploy":
    inventory += " ssh_port=" + str(agent['sshport'])  + "\n"
  elif goal == "config":
    inventory += " ansible_ssh_port=" + str(agent['sshport'])  + "\n"


inventory += "\n[Managers]\n"
for manager in managers:
  inventory += manager['name'] + " ansible_host=" + manager['ip']
  inventory += " comm_port=" + str(manager['commport'])
  inventory += " auth_port=" + str(manager['authport'])
  if goal == "deploy":
    inventory += " ssh_port=" + str(manager['sshport']) + "\n"
  elif goal == "config":
    inventory += " ansible_ssh_port=" + str(manager['sshport']) + "\n"

inventory += "\n[all:vars]\n"
inventory += "ansible_user=root\n"
inventory += "ansible_ssh_private_key_file=./id_rsa_tmp\n"
inventory += "ansible_ssh_common_args='-o StrictHostKeyChecking=no'\n"
inventory += "ansible_ssh_pass=root\n"

print inventory
