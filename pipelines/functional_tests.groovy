#!groovy

def execution_node = params.EXECUTION_NODE
def goal_version = params.WAZUH_VERSION
// Pipeline configuration
def jenkins_repo = params.JENKINS_REPO
def jenkins_credentials = params.JENKINS_CREDENTIALS
def jenkins_branch = params.JENKINS_BRANCH
def quality_dir = params.QUALITY_DIR
def machine_user = params.MACHINE_USER
def machine_credentials = params.MACHINE_CREDENTIALS


node(execution_node){
  /*stage("STAGE 0 - Initializaing environment"){
    git branch: "$jenkins_branch", credentialsId: "$jenkins_credentials", url: "$jenkins_repo"

  }*/
  stage("STAGE 0 - Launch Logcollector tests"){
    build job: 'Tests_launcher', parameters: [
      string(name: 'WAZUH_VERSION', value: "$goal_version"),
      string(name: 'EXECUTION_NODE', value: "$execution_node"),
      string(name: 'TEST_GOAL', value: "logcollector"),
      string(name: 'DEPLOY_ENVIRONMENT', value: "env-docker"),
      string(name: 'DEPLOY_GOAL', value: "1mU-u_1aU-u"),
      string(name: 'MACHINE_CREDENTIALS', value: "$machine_credentials"),
      string(name: 'NODE_USERNAME', value: "$machine_user"),
      string(name: 'REPOSITORY', value: "dev"),
      string(name: 'JENKINS_REPO', value: "$jenkins_repo"),
      string(name: 'JENKINS_CREDENTIALS', value: "$jenkins_credentials"),
      string(name: 'JENKINS_BRANCH', value: "$jenkins_branch"),
      string(name: 'QUALITY_DIR', value: "$quality_dir"),
      string(name: 'TESTS', value: "01-add_log 14-check_captured_command")
    ]
  }

  stage("STAGE 1 - Launch Syscheck tests"){
    build job: 'Tests_launcher', parameters: [
      string(name: 'WAZUH_VERSION', value: "$goal_version"),
      string(name: 'EXECUTION_NODE', value: "$execution_node"),
      string(name: 'TEST_GOAL', value: "syscheck"),
      string(name: 'DEPLOY_ENVIRONMENT', value: "env-docker"),
      string(name: 'DEPLOY_GOAL', value: "1mC-c_1aC-c"),
      string(name: 'MACHINE_CREDENTIALS', value: "$machine_credentials"),
      string(name: 'NODE_USERNAME', value: "$machine_user"),
      string(name: 'REPOSITORY', value: "dev"),
      string(name: 'JENKINS_REPO', value: "$jenkins_repo"),
      string(name: 'JENKINS_CREDENTIALS', value: "$jenkins_credentials"),
      string(name: 'JENKINS_BRANCH', value: "$jenkins_branch"),
      string(name: 'QUALITY_DIR', value: "$quality_dir"),
      string(name: 'TESTS', value: "01-add_file_alert")
    ]
  }

}
