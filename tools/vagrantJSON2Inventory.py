import json
import sys

def printParams(pathToKey):
  print("ansible_user=vagrant")
  print("ansible_ssh_private_key_file=" + pathToKey)
  print("ansible_ssh_common_args='-o StrictHostKeyChecking=no'")
  print("ansible_become=true")

with open(sys.argv[1]) as json_data:
  data = json.load(json_data)
  agents = ""
  managers = ""

  for element in data:
    hostname = element['hostname']
    ip = element['IP']
    identifier = str(element['ID'])

    if hostname == 'TAgent':
      agents = agents + hostname + identifier + ' ansible_host=' + ip
      agents += '\n'
    else:
      managers = managers + hostname + identifier + ' ansible_host=' + ip
      managers += '\n'

  print("[Agents]")
  print(agents)
  print("[Managers]")
  print(managers)
  print("")

  print("[Agents:vars]")
  printParams(sys.argv[2])
  print("")
  print("[Managers:vars]")
  printParams(sys.argv[2])


