node('master') {
    properties([
      [$class: "ParametersDefinitionProperty", parameterDefinitions:
        [
          // [$class: "ChoiceParameterDefinition", name: "PLATFORM_NAME", choices: "linux-x86_64\nlinux-ppc64le\nandroid-arm\nandroid-x86\nlinux-x86", description: "Build project on architecture"],
          // [$class: "LabelParameterDefinition", name: "DOCKER_NODE", defaultValue: "ubuntu4sonar", description: "Correct parameters:\nFor x86_64-jenkins-slave-cuda,amd64\nfor PowerPC - ppc,power8"],
          // [$class: "BooleanParameterDefinition", name: "SONAR", defaultValue: true, description: "Select to check code with SonarQube"],
          [$class: "StringParameterDefinition", name: "ACCOUNT", defaultValue: "smaryn", description: "Default Git account"],
          [$class: "StringParameterDefinition", name: "PROJECT", defaultValue: "pipelines", description: "Default Git project"],
          [$class: "StringParameterDefinition", name: "BRANCH_NAME", defaultValue: "master", description: "Default Git branch value"],
          [$class: "CredentialsParameterDefinition", name: "GITCREDID", required: false, defaultValue: "github-smaryn-pipelines-id-1", description: "Credentials to access needed repositories"],
          [$class: "StringParameterDefinition", name: "PDIR", defaultValue: "jobs/sonar", description: "Path to groovy scripts"],

        ]
      ]
    ])

    step([$class: 'WsCleanup'])

    checkout scm

    load "${PDIR}/vars.groovy"
    functions = load "${PDIR}/functions.groovy"

    stage('Check sources with SonarQube') {

      get_code("${PROJECT}")

      functions.sonar("${PROJECT}")

    }

    step([$class: 'WsCleanup'])

}
