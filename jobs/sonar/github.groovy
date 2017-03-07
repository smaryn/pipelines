node('master') {

    properties([buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '3')),
      parameters([string(defaultValue: '0.1', description: 'Version to pass to SonarQube', name: 'VERSION'),
                  string(defaultValue: 'smaryn', description: 'Default Git account', name: 'ACCOUNT'),
                  string(defaultValue: 'pipelines', description: 'Default Git project', name: 'PROJECT'),
                  string(defaultValue: 'master', description: 'Default Git branch value', name: 'BRANCH_NAME'),
                  [$class: 'CredentialsParameterDefinition', credentialType: 'com.cloudbees.plugins.credentials.common.StandardCredentials', defaultValue: 'github-smaryn-pipelines-id-1', description: 'Credentials to access repositories', name: 'GITCREDID', required: true],
                  string(defaultValue: 'jobs/sonar', description: 'Path to groovy scripts', name: 'PDIR')]), pipelineTriggers([])])

    stage('Prepare workspace') {
          step([$class: 'WsCleanup'])

          checkout scm

          load "${PDIR}/vars.groovy"
          functions = load "${PDIR}/functions.groovy"
    }

    stage('Check sources with SonarQube') {

        functions.get_code("${PROJECT}")

        functions.sonar("${PROJECT}")

    }

    step([$class: 'WsCleanup'])

}
