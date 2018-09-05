// Parameters from Jenkins
def test = params.TEST
def execution_node = params.EXECUTION_NODE
def string_hosts_config = params.HOSTS_CONFIG
def dry_run = params.INVOKE_PARAMETERS

// Pipeline configuration
def jenkins_repo = params.JENKINS_REPO
def jenkins_credentials = params.JENKINS_CREDENTIALS
def jenkins_branch = params.JENKINS_BRANCH
def quality_dir = params.QUALITY_DIR

// Configuration
def test_route = "${quality_dir}tests/$test/test/conf"

// Temporal output
def tmp_path = "${quality_dir}tmp"
def hosts_config = "$tmp_path/hosts_config"
def selected_deploy = "$tmp_path/selected_deploy.json"


node(execution_node){
  stage("STAGE 0 - Initializing environment"){
    if (dry_run == "yes") {
      properties([
        parameters([
          choice(choices: ['master'], description: '', name: 'EXECUTION_NODE'),
          string(defaultValue: '', description: '', name: 'HOSTS_CONFIG', trim: false),
          choice(choices: ['logcollector/01-add_log'], description: '', name: 'TEST')
        ])
      ])
      currentBuild.result = 'ABORTED'
      error('DRY RUN COMPLETED. JOB PARAMETERIZED.')
    }

    sh "mkdir -p $test"
    dir("./$test"){
      sh "rm -rf $tmp_path"
      git branch: "$jenkins_branch", credentialsId: "$jenkins_credentials", url: "$jenkins_repo"
      sh "mkdir -p $tmp_path"
      writeFile file: "$hosts_config", text: "$string_hosts_config"
    }
  }


  stage("STAGE 1 - Running test"){
    dir("./$test"){
      ansiblePlaybook disableHostKeyChecking: true, inventory: "$hosts_config", playbook: "$test_route/test_agent.yaml"
      ansiblePlaybook disableHostKeyChecking: true, inventory: "$hosts_config", playbook: "$test_route/test_manager.yaml"
    }
  }

}
