// Parameters from Jenkins
def version = params.WAZUH_VERSION
def branch = params.BRANCH
def installation = params.INSTALLATION
def deploy_env = params.WHERE_DEPLOY
def deploy = params.DEPLOY
def deploy_prefix = params.DEPLOY_PREFIX
def execution_node = params.EXECUTION_NODE
def machine_user = params.MACHINE_USER
def credentials = params.MACHINE_CREDENTIALS
def dry_run = params.INVOKE_PARAMETERS

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
def vagrant_path = "deployments/vagrant"
def docker_container_user = "root"

// Pipeline configuration
def jenkins_repo = "https://github.com/okynos/wazuh-QA.git"
def jenkins_branch = "master"

// Temporal output
def tmp_path = "tmp"
def hosts_deploy = "$tmp_path/hosts_deploy"
def hosts_config = "$tmp_path/hosts_config"
def selected_deploy = "$tmp_path/selected_deploy.json"

node(execution_node){
  stage("STAGE 0 - Initializing environment"){
    if (dry_run == "yes") {
      properties([
        parameters([
          choice(choices: ['3.5.0', '3.4.0', '3.3.1'], description: '', name: 'WAZUH_VERSION'),
          choice(choices: ['master'], description: '', name: 'BRANCH'),
          choice(choices: ['packages'], description: '', name: 'INSTALLATION'),
          choice(choices: ['env-docker'], description: '', name: 'WHERE_DEPLOY'),
          choice(choices: ['default'], description: '', name: 'DEPLOY'),
          choice(choices: ['notDefined'], description: '', name: 'DEPLOY_PREFIX'),
          choice(choices: ['master'], description: '', name: 'EXECUTION_NODE'),
          choice(choices: ['ec2-user'], description: '', name: 'MACHINE_USER'),
          // ADD PARAMETER MANUALLY
          //credentials(credentialType: 'com.cloudbees.jenkins.plugins.sshcredentials.impl.BasicSSHUserPrivateKey', defaultValue: '', description: '', name: 'MACHINE_CREDENTIALS', required: true)
        ])
      ])
      currentBuild.result = 'ABORTED'
      error('DRY RUN COMPLETED. JOB PARAMETERIZED.')
    }
    sh "rm -rf ./*"
    git branch: "$jenkins_branch", url: "$jenkins_repo"
    sh "mkdir -p $tmp_path"
    if (deploy_env == "env-docker"){
      sh "python tools/generateJSON.py $env_file $deploy_conf > $selected_deploy"
      sh "python tools/generateInventory.py deploy $selected_deploy $deploy_prefix $machine_user > $hosts_deploy"
      sh "python tools/generateInventory.py config $selected_deploy $deploy_prefix $docker_container_user > $hosts_config"
    }
  }

  stage("STAGE 1 - Deploy"){
    if (deploy_env == "env-docker"){
      ansiblePlaybook credentialsId: "$credentials", disableHostKeyChecking: true, inventory: "$hosts_deploy", playbook: "$deploy_path/conf/deploy.yaml"

      ansiblePlaybook credentialsId: "$credentials", disableHostKeyChecking: true, extras: "-- extra-vars wazuh_version=$version", inventory: "$hosts_config", playbook: "$deploy_path/conf/wazuh_packages_install.yaml"

      ansiblePlaybook credentialsId: "$credentials", disableHostKeyChecking: true, inventory: "$hosts_config", playbook: "$deploy_path/conf/wazuh_register.yaml"

    }/*else if (deploy_env == "env-vm"){
      // NOT TESTED WITH NEW CREDENTIAL SYSTEM
      dir(vagrant_path){
        sh "deploy_file=$deploy_conf vagrant up"
      }
      sh "python tools/generateInventoryVagrant.py $vagrant_path/results.json ./NO_KEY_DEFINED > $hosts_config"
    }*/
  }

}
