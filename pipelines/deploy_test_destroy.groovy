// Parameters from Jenkins
def version = params.WAZUH_VERSION
def branch = params.BRANCH
def installation = params.INSTALLATION
def deploy_env = params.WHERE_DEPLOY
def deploy = params.DEPLOY
def deploy_prefix = params.DEPLOY_PREFIX
def test = params.TEST
def execution_node = params.EXECUTION_NODE
def machine_user = params.MACHINE_USER
def credentials = params.MACHINE_CREDENTIALS
def destroy = params.DESTROY
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
def test_route = "tests/$test/test/conf"
def vagrant_path = "deployments/vagrant"
def docker_container_user = "root"

// Pipeline configuration
def jenkins_repo = "https://github.com/okynos/wazuh-QA.git"
def jenkins_branch = "master"
def keys_path = ""

// Temporal output
def tmp_path = "tmp"
def first_random = Math.abs(new Random().nextInt() % 999) + 1
def second_random = Math.abs(new Random().nextInt() % 999) + 1
def hosts_deploy = "$tmp_path/hosts_deploy_${first_random}_${second_random}"
def hosts_config = "$tmp_path/hosts_config_${first_random}_${second_random}"
def selected_deploy = "$tmp_path/selected_deploy_${first_random}_${second_random}.json"
def string_hosts_deploy = ""
def string_hosts_config = ""


node(execution_node){
  stage("STAGE 0 - Init environment"){
    //Pipeline parameters config
    if (dry_run == "yes") {
      properties([
        parameters([
          choice(choices: ['3.5.0', '3.4.0', '3.3.1'], description: '', name: 'WAZUH_VERSION'),
          choice(choices: ['master'], description: '', name: 'BRANCH'),
          choice(choices: ['packages'], description: '', name: 'INSTALLATION'),
          choice(choices: ['env-docker'], description: '', name: 'WHERE_DEPLOY'),
          choice(choices: ['default'], description: '', name: 'DEPLOY'),
          string(defaultValue: 'notDefined', description: '', name: 'DEPLOY_PREFIX', trim: false),
          choice(choices: ['master'], description: '', name: 'EXECUTION_NODE'),
          choice(choices: ['ec2-user'], description: '', name: 'MACHINE_USER'),
          choice(choices: ['logcollector/01-add_log'], description: '', name: 'TEST')
          // ADD PARAMETER MANUALLY
          //credentials(credentialType: 'com.cloudbees.jenkins.plugins.sshcredentials.impl.BasicSSHUserPrivateKey', defaultValue: '', description: '', name: 'MACHINE_CREDENTIALS', required: true)
        ])
      ])
      currentBuild.result = 'ABORTED'
      error('DRY RUN COMPLETED. JOB PARAMETERIZED.')
    }

    // Deploy and test configs
    sh "rm -rf ./*"
    git branch: "$jenkins_branch", url: "$jenkins_repo"
    sh "mkdir -p $tmp_path"

    if (deploy_env == "env-docker"){

      sh "python tools/generateJSON.py $env_file $deploy_conf > $selected_deploy"
      sh "python tools/generateInventory.py deploy $selected_deploy $deploy_prefix $machine_user > $hosts_deploy"
      sh "python tools/generateInventory.py config $selected_deploy $deploy_prefix $docker_container_user > $hosts_config"
      string_hosts_deploy = readFile "$hosts_deploy"
      string_hosts_config = readFile "$hosts_config"
    }
  }

  stage("STAGE 1 - Deploy wazuh instances"){
    build job: "Deploy_wazuh_instances", parameters: [
      string(name: 'WAZUH_VERSION', value: "$version"),
      string(name: 'BRANCH', value: "$branch"),
      string(name: 'INSTALLATION', value: "$installation"),
      string(name: 'EXECUTION_NODE', value: "$execution_node"),
      string(name: 'HOSTS_DEPLOY', value: "$string_hosts_deploy"),
      string(name: 'HOSTS_CONFIG', value: "$string_hosts_config"),
      string(name: 'MACHINE_CREDENTIALS', value: "$credentials")
    ]
  }

  stage("STAGE 2 - Testing"){
    build job: "Launch_wazuh_test", parameters: [
      string(name: 'TEST', value: "$test"),
      string(name: 'EXECUTION_NODE', value: "$execution_node"),
      string(name: 'HOSTS_CONFIG', value: "$string_hosts_config")
    ]
  }

  stage("STAGE 3 - Destroy evidences"){
    if(destroy){
      build job: "Destroy_wazuh_instances", parameters: [
        string(name: 'EXECUTION_NODE', value: "$execution_node"),
        string(name: 'HOSTS_DEPLOY', value: "$string_hosts_deploy"),
        string(name: 'MACHINE_CREDENTIALS', value: "$credentials")
      ]
    }
  }

}
