node('master') {
/*
    properties([
      [$class: "ParametersDefinitionProperty", parameterDefinitions:
        [
          [buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '3')), pipelineTriggers([])]
          // [$class: "ChoiceParameterDefinition", name: "PLATFORM_NAME", choices: "linux-x86_64\nlinux-ppc64le\nandroid-arm\nandroid-x86\nlinux-x86", description: "Build project on architecture"],
          // [$class: "LabelParameterDefinition", name: "DOCKER_NODE", defaultValue: "ubuntu4sonar", description: "Correct parameters:\nFor x86_64-jenkins-slave-cuda,amd64\nfor PowerPC - ppc,power8"],
          // [$class: "BooleanParameterDefinition", name: "SONAR", defaultValue: true, description: "Select to check code with SonarQube"],
          [$class: "StringParameterDefinition", name: "VERSION", defaultValue: "0.1", description: "Version to pass to SonarQube"],
          [$class: "StringParameterDefinition", name: "ACCOUNT", defaultValue: "smaryn", description: "Default Git account"],
          [$class: "StringParameterDefinition", name: "PROJECT", defaultValue: "pipelines", description: "Default Git project"],
          [$class: "StringParameterDefinition", name: "BRANCH_NAME", defaultValue: "master", description: "Default Git branch value"],
          [$class: "CredentialsParameterDefinition", name: "GITCREDID", required: false, defaultValue: "github-smaryn-pipelines-id-1", description: "Credentials to access needed repositories"],
          [$class: "StringParameterDefinition", name: "PDIR", defaultValue: "jobs/sonar", description: "Path to groovy scripts"],

        ]
      ]
    ])
*/
    properties([buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '3')),
      parameters([string(defaultValue: '0.1', description: 'Version to pass to SonarQube', name: 'VERSION'),
                  string(defaultValue: 'smaryn', description: 'Default Git account', name: 'ACCOUNT'),
                  string(defaultValue: 'pipelines', description: 'Default Git project', name: 'PROJECT'),
                  string(defaultValue: 'master', description: 'Default Git branch value', name: 'BRANCH_NAME'),
                  string(defaultValue: 'ssh', description: 'ssh OR http', name: 'PROTOCOL'),
                  [$class: 'CredentialsParameterDefinition', credentialType: 'com.cloudbees.plugins.credentials.common.StandardCredentials', defaultValue: 'github-smaryn-pipelines-id-1', description: 'Credentials to access repositories', name: 'GITCREDID', required: false],
                  string(defaultValue: 'jobs/sonar', description: 'Path to groovy scripts', name: 'PDIR')]), pipelineTriggers([])])


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
