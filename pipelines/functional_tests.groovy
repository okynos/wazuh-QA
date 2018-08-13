#!groovy

def execution_node = params.EXECUTION_NODE
def goal_version = params.WAZUH_VERSION


node(execution_node){
  stage("STAGE 0 - Initializaing environment"){
    git url: 'https://github.com/okynos/wazuh-QA.git'

  }
  stage("STAGE 1 - Launch Logcollector tests"){
    build job: 'Tests_launcher', parameters: [
      string(name: 'WAZUH_VERSION', value: "$goal_version"),
      string(name: 'EXECUTION_NODE', value: "$execution_node"),
      string(name: 'TEST_GOAL', value: "logcollector"),
      string(name: 'DEPLOY_ENVIRONMENT', value: "env-docker"),
      string(name: 'DEPLOY_GOAL', value: "1mC-c_1aC-c"),
      string(name: 'PRIVATE_KEY', value: "test.pem"),
      string(name: 'NODE_USERNAME', value: "ec2-user"),
      string(name: 'REPOSITORY', value: "dev"),
      string(name: 'TESTS', value: "01-add_log 14-check_captured_command")
    ]
  }

  stage("STAGE 2 - Launch Syscheck tests"){
    build job: 'Tests_launcher', parameters: [
      string(name: 'WAZUH_VERSION', value: "$goal_version"),
      string(name: 'EXECUTION_NODE', value: "$execution_node"),
      string(name: 'TEST_GOAL', value: "syscheck"),
      string(name: 'DEPLOY_ENVIRONMENT', value: "env-docker"),
      string(name: 'DEPLOY_GOAL', value: "1mC-c_1aC-c"),
      string(name: 'PRIVATE_KEY', value: "test.pem"),
      string(name: 'NODE_USERNAME', value: "ec2-user"),
      string(name: 'REPOSITORY', value: "dev"),
      string(name: 'TESTS', value: "01-add_file_alert")
    ]
  }

}
