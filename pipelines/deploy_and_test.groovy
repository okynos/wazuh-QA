// Parameters from Jenkins
def version = params.WAZUH_VERSION
def branch = params.BRANCH
def installation = params.INSTALLATION
def deploy_env = params.WHERE_DEPLOY
def deploy = params.DEPLOY
def deploy_prefix = params.DEPLOY_PREFIX
def test = params.TEST
def execution_node = params.EXECUTION_NODE
def key_name = params.KEY_NAME
def machine_user = params.MACHINE_USER

// Configuration
def deploy_path = ""
if (deploy_env == "env-docker"){
  deploy_path = "deployments/ansible"
}else if (deploy_env == "env-vm"){
  deploy_path = "deployments/vagrant"
}
def environment = "environments/$deploy_env"
def env_file = "$environment/environment.json"
def deploy_conf = "$deploy_path/$deploy"+".json"
def test_route = "tests/$test/test/conf"
def vagrant_path = "deployments/vagrant"
def docker_container_user = "root"

// Pipeline configuration
def jenkins_repo = "https://github.com/okynos/wazuh-QA.git"
def jenkins_branch = "master"
def keys_path = ""

// Temporal output
def tmp_path = "tmp"
def hosts_deploy = "$tmp_path/hosts_deploy"
def hosts_config = "$tmp_path/hosts_config"
def selected_deploy = "$tmp_path/selected_deploy.json"


node(execution_node){
  stage("STAGE 0 - Initializing environment"){
    sh "rm -rf ./*"
    git branch: "$jenkins_branch", url: "$jenkins_repo"
    sh "mkdir -p $tmp_path/keys"
    keys_path = "${env.WORKSPACE}/../../keys"
    sh "cp '$keys_path'/$key_name $tmp_path/keys/"
    sh "chmod 0400 $tmp_path/keys/$key_name"
    if (deploy_env == "env-docker"){
      sh "python tools/generateJSON.py $env_file $deploy_conf > $selected_deploy"
      sh "python tools/generateInventory.py deploy $selected_deploy $machine_user $deploy_prefix $tmp_path/keys/$key_name > $hosts_deploy"
      sh "python tools/generateInventory.py config $selected_deploy $docker_container_user $deploy_prefix $tmp_path/keys/$key_name > $hosts_config"
    }
  }

  stage("STAGE 1 - Deploy"){
    if (deploy_env == "env-docker"){
      sh "ansible-playbook -i $hosts_deploy $deploy_path/conf/deploy.yaml"
      sh "ansible-playbook -i $hosts_config $deploy_path/conf/wazuh_packages_install.yaml --extra-vars 'wazuh_version=$version'"
      sh "ansible-playbook -i $hosts_config $deploy_path/conf/wazuh_register.yaml"
    }else if (deploy_env == "env-vm"){
      dir(vagrant_path){
        sh "deploy_file=$deploy_conf vagrant up"
      }
      sh "python tools/generateInventoryVagrant.py $vagrant_path/results.json $tmp_path/keys/$key_name > $hosts_config"
    }
  }

  stage("STAGE 2 - Running test"){ 
    sh "ansible-playbook -i $hosts_config $test_route/test_agent.yaml"
    sh "ansible-playbook -i $hosts_config $test_route/test_manager.yaml"
  }

  stage("STAGE X - Destroy"){
    sh "ansible-playbook -i $hosts_deploy $deploy_path/conf/destroy.yaml"
  }

}
