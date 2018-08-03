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
  if goal == "deploy":
    inventory += " ansible_host=" + agent['deploy_ip']
    inventory += " docker_image=" + agent['docker_image']
    inventory += " ssh_port=" + str(agent['sshport'])
    inventory += " container_ip=" + agent['container_ip']  
  elif goal == "config" and agent['location'] == "external":
      inventory += " ansible_host=" + agent['deploy_ip']
      inventory += " ansible_ssh_port=" + str(agent['sshport'])
  else:
    inventory += " ansible_host=" + agent['container_ip']
  inventory += " location=" + agent['location']
  inventory += "\n"






inventory += "\n[Managers]\n"
for manager in managers:
  inventory += manager['name']
  if goal == "deploy":
    inventory += " ansible_host=" + manager['deploy_ip']
    inventory += " docker_image=" + manager['docker_image']
    inventory += " ssh_port=" + str(manager['sshport']) 
    inventory += " container_ip=" + manager['container_ip'] 
  elif goal == "config":
    inventory += " ansible_host=" + manager['container_ip']
    if manager['location'] == "external":
      inventory += " ansible_ssh_port=" + str(manager['sshport']) + "\n"
  inventory += " comm_port=" + str(manager['commport'])
  inventory += " auth_port=" + str(manager['authport'])
  inventory += "\n"

inventory += "\n[all:vars]\n"
inventory += "ansible_user=root\n"
inventory += "ansible_ssh_private_key_file=tmp/id_rsa_tmp\n"
inventory += "ansible_ssh_common_args='-o StrictHostKeyChecking=no'\n"
inventory += "ansible_ssh_pass=root\n"

print inventory
