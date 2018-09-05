// Parameters from Jenkins
def string_hosts_deploy = params.HOSTS_DEPLOY
def execution_node = params.EXECUTION_NODE
def dry_run = params.INVOKE_PARAMETERS
def credentials = params.MACHINE_CREDENTIALS

// Pipeline configuration
def jenkins_repo = params.JENKINS_REPO
def jenkins_credentials = params.JENKINS_CREDENTIALS
def jenkins_branch = params.JENKINS_BRANCH
def quality_dir = params.QUALITY_DIR
def test = params.TEST

// Configuration
def deploy_path = "${quality_dir}deployments/ansible"

// Temporal output
def tmp_path = "${quality_dir}tmp"
def hosts_deploy = "$tmp_path/hosts_deploy"


node(execution_node){
  stage("STAGE 0 - Initializing environment"){
    if (dry_run == "yes") {
      properties([
        parameters([
          choice(choices: ['master'], description: '', name: 'EXECUTION_NODE'),
          string(defaultValue: '', description: '', name: 'HOSTS_DEPLOY', trim: false)
          // ADD PARAMETER MANUALLY
          //credentials(credentialType: 'com.cloudbees.jenkins.plugins.sshcredentials.impl.BasicSSHUserPrivateKey', defaultValue: '', description: '', name: 'MACHINE_CREDENTIALS', required: true)
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
      writeFile file: "$hosts_deploy", text: "$string_hosts_deploy"
    }
  }

  stage("STAGE X - Destroy"){
    dir("./$test"){
      ansiblePlaybook credentialsId: "$credentials", disableHostKeyChecking: true, inventory: "$hosts_deploy", playbook: "$deploy_path/conf/destroy.yaml"
    }
  }

}
