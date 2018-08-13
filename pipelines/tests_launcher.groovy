#!groovy

def execution_node = params.EXECUTION_NODE
def goal_version = params.WAZUH_VERSION
def repo = params.REPOSITORY
def test_goal = params.TEST_GOAL
def deploy_env = params.DEPLOY_ENVIRONMENT
def deploy = params.DEPLOY_GOAL
def private_key_name = params.PRIVATE_KEY
def node_username = params.NODE_USERNAME
def tests_to_pass = params.TESTS
def stages_for_parallel = tests_to_pass.split().collectEntries {
  ["$it": to_stage(it, execution_node, goal_version, repo, test_goal, deploy_env, deploy, private_key_name, node_username)]
}

def to_stage(test, execution_node, goal_version, repo, test_goal, deploy_env, deploy, private_key_name, node_username) {
  return {
    stage ("STAGE 1 - " + test) {
      build job: "$test_goal-test_automatic", parameters: [
        string(name: 'WAZUH_VERSION', value: "$goal_version"),
        string(name: 'WHERE_DEPLOY', value: "$deploy_env"),
        string(name: 'DEPLOY', value: "$deploy"),
        string(name: 'DEPLOY_PREFIX', value: "$test_goal-$test"),
        string(name: 'TEST', value: "$test_goal/$test"),
        string(name: 'EXECUTION_NODE', value: "$execution_node"),
        string(name: 'KEY_NAME', value: "$private_key_name"),
        string(name: 'MACHINE_USER', value: "$node_username")
      ]
    }
  }
}

node(execution_node){
  stage("STAGE 1 - Launch functional test"){ 
    parallel stages_for_parallel 
  }
}
