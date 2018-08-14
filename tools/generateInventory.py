import json
import sys

# Checkings
if len(sys.argv) < 3:
  print("Usage: " + sys.argv[0] + " (deploy/config) JSONFile [name_prefix] [ssh_user] [path_to_private_key]")
  exit()
# Loading data
if len(sys.argv) >= 3:
  json_data = json.load(open(sys.argv[2]))
  goal = sys.argv[1].lower()
  name_prefix = ""
  ssh_user = ""
  path_private_key = ""
  if len(sys.argv) >= 4:
    name_prefix = sys.argv[3]
    if len(sys.argv) >= 5:
      ssh_user = sys.argv[4]
    if len(sys.argv) >= 6:
      path_private_key = sys.argv[5]

if goal != "deploy" and goal != "config":
  print("Usage: " + sys.argv[0] + " (deploy/config) JSONFile [name_prefix] [ssh_user] [path_to_private_key]")
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
  deploy_ip = element['deploy_ip']
  container_ip = element['container_ip']
  name = element['name']
  image = element['docker_image']
  location = element['location']
  obj = {}
  obj['system'] = dsystem
  obj['type'] = dtype
  obj['sshport'] = ssh_port
  obj['deploy_ip'] = deploy_ip
  obj['container_ip'] = container_ip
  if name_prefix != "":
    obj['name'] = name_prefix + "_" + name
  else:
    obj['name'] = name
  obj['docker_image'] = image
  obj['location'] = location

  if dtype == "agent":
    agents.append(obj.copy())

  if dtype == "manager":
    obj['commport'] = element['commport']
    obj['authport'] = element['authport'] 
    managers.append(obj.copy())






inventory += "[Agents]\n"
for agent in agents:
  inventory += agent['name']
  inventory += " ansible_host=" + agent['deploy_ip']
  inventory += " container_ip=" + agent['container_ip']  
  if goal == "deploy":
    inventory += " docker_image=" + agent['docker_image']
    inventory += " ssh_port=" + str(agent['sshport'])
  elif goal == "config":
    inventory += " ansible_ssh_port=" + str(agent['sshport'])
    inventory += " location=" + agent['location']
  inventory += "\n"






inventory += "\n[Managers]\n"
for manager in managers:
  inventory += manager['name']
  inventory += " ansible_host=" + manager['deploy_ip']
  inventory += " container_ip=" + manager['container_ip'] 
  if goal == "deploy":
    inventory += " docker_image=" + manager['docker_image']
    inventory += " ssh_port=" + str(manager['sshport']) 
  elif goal == "config":
    inventory += " ansible_ssh_port=" + str(manager['sshport'])
  inventory += " comm_port=" + str(manager['commport'])
  inventory += " auth_port=" + str(manager['authport'])
  inventory += "\n"

inventory += "\n[all:vars]\n"
if ssh_user != "":
  inventory += "ansible_user=" + ssh_user + "\n"
if path_private_key != "":
  inventory += "ansible_ssh_private_key_file=" + path_private_key + "\n"

inventory += "ansible_ssh_common_args='-o StrictHostKeyChecking=no'\n"
inventory += "ansible_ssh_pass=root\n" 

print inventory
