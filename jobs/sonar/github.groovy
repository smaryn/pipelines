node('master') {
properties([parameters([string(defaultValue: '0.1', description: 'Version to pass to SonarQube', name: 'VERSION'), string(defaultValue: 'smaryn', description: 'Default Git account', name: 'ACCOUNT'), string(defaultValue: 'pipelines', description: 'Default Git project', name: 'PROJECT'), string(defaultValue: 'master', description: 'Default Git branch value', name: 'BRANCH_NAME'), choice(choices: ['ssh', 'http'], description: 'Protocol to be used for access to github', name: 'PROTOCOL'), [$class: 'CredentialsParameterDefinition', credentialType: 'com.cloudbees.plugins.credentials.common.StandardCredentials', defaultValue: 'github-smaryn-pipelines-id-1', description: 'Credentials to access repositories', name: 'GITCREDID', required: false], string(defaultValue: 'jobs/sonar', description: 'Path to groovy scripts', name: 'PDIR')]), pipelineTriggers([])])

    stage('Prepare workspace') {
          step([$class: 'WsCleanup'])

          checkout scm

          // Remove .git folder from workspace
          // functions.rm()

          load "${PDIR}/vars.groovy"
          functions = load "${PDIR}/functions.groovy"
    }

    stage('Check sources with SonarQube') {
      switch(PROTOCOL) {
        case "ssh":
          functions.get_code_ssh("${PROJECT}")
        break

        case "http":
          functions.get_code_http("${PROJECT}")
        break

        default:
          error("Protocol is not defined or unsupported")
        break
      }

        functions.sonar("${PROJECT}")

    }

    step([$class: 'WsCleanup'])

}
