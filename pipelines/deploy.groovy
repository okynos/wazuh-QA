// Parameters from Jenkins
def version = params.WAZUH_VERSION
def branch = params.BRANCH
def execution_node = params.EXECUTION_NODE
def credentials = params.MACHINE_CREDENTIALS
def string_hosts_deploy = params.HOSTS_DEPLOY
def string_hosts_config = params.HOSTS_CONFIG
def dry_run = params.INVOKE_PARAMETERS
def test = params.TEST

// Pipeline configuration
def jenkins_repo = params.JENKINS_REPO
def jenkins_credentials = params.JENKINS_CREDENTIALS
def jenkins_branch = params.JENKINS_BRANCH
def quality_dir = params.QUALITY_DIR

// Configuration
def deploy_path = "${quality_dir}deployments/ansible"

// Temporal output
def tmp_path = "${quality_dir}tmp"
def hosts_deploy = "$tmp_path/hosts_deploy"
def hosts_config = "$tmp_path/hosts_config"

node(execution_node){
  stage("STAGE 0 - Initializing environment"){
    if (dry_run == "yes") {
      properties([
        parameters([
          choice(choices: ['3.5.0', '3.4.0', '3.3.1'], description: '', name: 'WAZUH_VERSION'),
          choice(choices: ['master'], description: '', name: 'BRANCH'),
          choice(choices: ['env-docker'], description: '', name: 'WHERE_DEPLOY'),
          choice(choices: ['default'], description: '', name: 'DEPLOY'),
          string(defaultValue: '', description: '', name: 'HOSTS_DEPLOY', trim: false),
          string(defaultValue: '', description: '', name: 'HOSTS_CONFIG', trim: false),
          choice(choices: ['master'], description: '', name: 'EXECUTION_NODE')
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
      writeFile file: "$hosts_config", text: "$string_hosts_config"
    }
  }

  stage("STAGE 1 - Deploy instances"){
    dir("./$test"){
      ansiblePlaybook credentialsId: "$credentials", disableHostKeyChecking: true, inventory: "$hosts_deploy", playbook: "$deploy_path/conf/deploy.yaml"

      ansiblePlaybook credentialsId: "$credentials", disableHostKeyChecking: true, extras: "--extra-vars wazuh_version=$version", inventory: "$hosts_config", playbook: "$deploy_path/conf/wazuh_packages_install.yaml"

      ansiblePlaybook credentialsId: "$credentials", disableHostKeyChecking: true, inventory: "$hosts_config", playbook: "$deploy_path/conf/wazuh_register.yaml"
    }
  }

}
