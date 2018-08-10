#!groovy

def execution_node = params.EXECUTION_NODE
def goal_version = params.WAZUH_VERSION
def goal_branch = "master"
def goal_api_branch = "master"
def repo = "dev"

// tests to pass
def tests_to_pass = ['01-add_log','14-check_captured_command']

node(execution_node){

  stage("STAGE 1 - Launch functional test"){
    tests_to_pass.eachWithIndex { test, index ->
      stage("STAGE 1.$index - $test"){
          build job: 'Logcollector_test_automatic', parameters: [
            string(name: 'WAZUH_VERSION', value: "$goal_version"),
            string(name: 'WHERE_DEPLOY', value: "env-docker"),
            string(name: 'DEPLOY', value: "1mC-c_1aC-c"),
            string(name: 'TEST', value: "logcollector/$test"),
            string(name: 'EXECUTION_NODE', value: "$execution_node"),
            string(name: 'KEY_NAME', value: "test.pem"),
            string(name: 'MACHINE_USER', value: "ec2-user")
          ]
      }
    }
  }


  stage("STAGE X - Finish"){

  }
}
