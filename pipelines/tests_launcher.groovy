#!groovy

// Parameters from Jenkins
def execution_node = params.EXECUTION_NODE
def goal_version = params.WAZUH_VERSION
def repo = params.REPOSITORY
def branch = params.BRANCH
def test_module = params.TEST_MODULE
def deploy_env = params.DEPLOY_ENVIRONMENT
def deploy = params.DEPLOY_GOAL
def deploy_prefix = params.DEPLOY_PREFIX
def credentials = params.MACHINE_CREDENTIALS
def node_username = params.NODE_USERNAME
def tests_to_pass = params.TESTS
def dry_run = params.INVOKE_PARAMETERS
def destroy = params.DESTROY
def stages_for_parallel = tests_to_pass.split().collectEntries {
  ["$it": to_stage(it, execution_node, goal_version, repo, test_module, deploy_env, deploy, node_username, credentials)]
}


def to_stage(test, execution_node, goal_version, repo, test_module, deploy_env, deploy, node_username, credentials) {
  return {

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

    /*stage ("STAGE 1 - " + test) {
      build job: "$test_module-test_automatic", parameters: [
        string(name: 'WAZUH_VERSION', value: "$goal_version"),
        string(name: 'WHERE_DEPLOY', value: "$deploy_env"),
        string(name: 'DEPLOY', value: "$deploy"),
        string(name: 'DEPLOY_PREFIX', value: "$test_module-$test"),
        string(name: 'TEST', value: "$test_module/$test"),
        string(name: 'EXECUTION_NODE', value: "$execution_node"),
        string(name: 'KEY_NAME', value: "$private_key_name"),
        string(name: 'MACHINE_USER', value: "$node_username")
      ]
    }*/


    stage("STAGE 0 - Init environment - test: " + test){
      // Deploy and test configs
      sh "rm -rf ./*"
      git branch: "$jenkins_branch", url: "$jenkins_repo"
      sh "mkdir -p $tmp_path"

      if (deploy_env == "env-docker"){

        sh "python tools/generateJSON.py $env_file $deploy_conf > $selected_deploy"
        sh "python tools/generateInventory.py deploy $selected_deploy $deploy_prefix $node_username > $hosts_deploy"
        sh "python tools/generateInventory.py config $selected_deploy $deploy_prefix $docker_container_user > $hosts_config"
        string_hosts_deploy = readFile "$hosts_deploy"
        string_hosts_config = readFile "$hosts_config"
      }
    }

    stage("STAGE 1 - Deploy wazuh instances - test: " + test){
      build job: "Deploy_wazuh_instances", parameters: [
        string(name: 'WAZUH_VERSION', value: "$goal_version"),
        string(name: 'BRANCH', value: "$branch"),
        string(name: 'EXECUTION_NODE', value: "$execution_node"),
        string(name: 'HOSTS_DEPLOY', value: "$string_hosts_deploy"),
        string(name: 'HOSTS_CONFIG', value: "$string_hosts_config"),
        string(name: 'MACHINE_CREDENTIALS', value: "$credentials")
      ]
    }

    stage("STAGE 2 - Testing - test: " + test){
      build job: "Launch_wazuh_test", parameters: [
        string(name: 'TEST', value: "$test"),
        string(name: 'EXECUTION_NODE', value: "$execution_node"),
        string(name: 'HOSTS_CONFIG', value: "$string_hosts_config")
      ]
    }

    stage("STAGE 3 - Destroy evidences - test: " + test){
      if(destroy){
        build job: "Destroy_wazuh_instances", parameters: [
          string(name: 'EXECUTION_NODE', value: "$execution_node"),
          string(name: 'HOSTS_DEPLOY', value: "$string_hosts_deploy"),
          string(name: 'MACHINE_CREDENTIALS', value: "$credentials")
        ]
      }
    }
  }
}




node(execution_node){
  stage("STAGE 0 - Init environment"){
    //Pipeline parameters config
    if (dry_run == "yes") {
      properties([
        parameters([
          choice(choices: ['3.5.0', '3.4.0', '3.3.1'], description: '', name: 'WAZUH_VERSION'),
          choice(choices: ['dev', 'live'], description: '', name: 'REPOSITORY'),
          choice(choices: ['master'], description: '', name: 'BRANCH'),
          choice(choices: ['packages'], description: '', name: 'INSTALLATION'),
          choice(choices: ['env-docker'], description: '', name: 'DEPLOY_ENVIRONMENT'),
          choice(choices: ['default'], description: '', name: 'DEPLOY_GOAL'),
          string(defaultValue: 'notDefined', description: '', name: 'DEPLOY_PREFIX', trim: false),
          choice(choices: ['master'], description: '', name: 'EXECUTION_NODE'),
          choice(choices: ['ec2-user'], description: '', name: 'NODE_USERNAME'),
          choice(choices: ['logcollector', 'syscheck'], description: '', name: 'TEST_MODULE'),
          string(defaultValue: '01-add_log 14-check_captured_command', description: '', name: 'TESTS', trim: false),
          booleanParam(defaultValue: true, description: '', name: 'DESTROY')
          // ADD PARAMETER MANUALLY
          //credentials(credentialType: 'com.cloudbees.jenkins.plugins.sshcredentials.impl.BasicSSHUserPrivateKey', defaultValue: '', description: '', name: 'MACHINE_CREDENTIALS', required: true)
        ])
      ])
      currentBuild.result = 'ABORTED'
      error('DRY RUN COMPLETED. JOB PARAMETERIZED.')
    }
  }

  stage("STAGE 1 - Launch functional test"){ 
    parallel stages_for_parallel 
  }

}
