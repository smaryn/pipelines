node('master') {
    properties([
      [$class: "ParametersDefinitionProperty", parameterDefinitions:
        [
          [$class: "ChoiceParameterDefinition", name: "PLATFORM_NAME", choices: "linux-x86_64\nlinux-ppc64le\nandroid-arm\nandroid-x86\nlinux-x86", description: "Build project on architecture"],
          [$class: "LabelParameterDefinition", name: "DOCKER_NODE", defaultValue: "jenkins-slave-cuda", description: "Correct parameters:\nFor x86_64-jenkins-slave-cuda,amd64\nfor PowerPC - ppc,power8"],
          [$class: "BooleanParameterDefinition", name: "SONAR", defaultValue: false, description: "Select to check code with SonarQube"],
          [$class: "StringParameterDefinition", name: "GIT_BRANCHNAME", defaultValue: "master", description: "Default Git branch value"],
          [$class: "CredentialsParameterDefinition", name: "GITCREDID", required: false, defaultValue: "github-smaryn-pipelines-id-1", description: "Credentials to access needed repositories"],
          [$class: "StringParameterDefinition", name: "PDIR", defaultValue: "jobs/service", description: "Path to groovy scripts"],

        ]
      ]
    ])

    step([$class: 'WsCleanup'])

    // checkout scm

    stage('Check sources with SonarQube') {

      get_code("${PROJECT}")

      functions.sonar("${PROJECT}")

    }

    step([$class: 'WsCleanup'])

}
