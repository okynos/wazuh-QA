#!groovy

def execution_node = params.EXECUTION_NODE
def tests_to_pass = ['01-add_file_alert']
def stages_for_parallel = tests_to_pass.collectEntries {
  ["$it": to_stage(it)]
}

node(execution_node){
  stage("STAGE 1 - Launch functional test"){ 
    parallel stages_for_parallel 
  }

  stage("STAGE X - Finish"){

  }
}

def to_stage(test) {
  return {
    stage ("STAGE 1 - " + test) {
      def goal_version = params.WAZUH_VERSION
      def goal_branch = "master"
      def goal_api_branch = "master"
      def repo = "dev"
      def test_goal = "syscheck"
      build job: "$test_goal-test_automatic", parameters: [
        string(name: 'WAZUH_VERSION', value: "$goal_version"),
        string(name: 'WHERE_DEPLOY', value: "env-docker"),
        string(name: 'DEPLOY', value: "1mC-c_1aC-c"),
        string(name: 'DEPLOY_PREFIX', value: "$test_goal-$test"),
        string(name: 'TEST', value: "$test_goal/$test"),
        string(name: 'EXECUTION_NODE', value: "$execution_node"),
        string(name: 'KEY_NAME', value: "test.pem"),
        string(name: 'MACHINE_USER', value: "ec2-user")
      ]
    }
  }
}
