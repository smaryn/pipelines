def get_code_ssh(proj) {
  checkout([$class: 'GitSCM',
            branches: [[name: "*/${BRANCH_NAME}"]],
            doGenerateSubmoduleConfigurations: false,
            // extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: "${proj}"], [$class: 'CloneOption', honorRefspec: true, noTags: true, reference: '', shallow: true]],
            extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: "${proj}"], [$class: 'CloneOption', honorRefspec: true, noTags: false, reference: '', shallow: false]],
            submoduleCfg: [],
            userRemoteConfigs: [[url: "git@github.com:${ACCOUNT}/${proj}.git", credentialsId: "${GITCREDID}"]]])
}

def get_code_http(proj) {
  checkout([$class: 'GitSCM',
            branches: [[name: "*/${BRANCH_NAME}"]],
            doGenerateSubmoduleConfigurations: false,
            // extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: "${proj}"], [$class: 'CloneOption', honorRefspec: true, noTags: true, reference: '', shallow: true]],
            extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: "${proj}"], [$class: 'CloneOption', honorRefspec: true, noTags: false, reference: '', shallow: false]],
            submoduleCfg: [],
            userRemoteConfigs: [[url: "https://github.com/${ACCOUNT}/${proj}.git", credentialsId: "${GITCREDID}"]]])
}

// Remove .git folder and other unneeded files from workspace
def rm() {
  echo "Remove .git folder from workspace - ${WORKSPACE}"
  dir("${WORKSPACE}") {
    sh("rm -rf {.git,.gitignore,docs,imgs,ansible,README.md}")
  }
}

def dirm2() {
  sh ("mkdir ${WORKSPACE}/.m2 || true")
}

def def_docker() {
  echo "Setting docker parameters and image for ${PLATFORM_NAME}"
  switch("${PLATFORM_NAME}") {
    case "linux-ppc64le":
      dockerImage = "${DOCKER_CUDA_PPC}"
      dockerParams = dockerParams_ppc

    break

    case "linux-x86_64":
      dockerImage = "${DOCKER_CENTOS6_CUDA80_AMD64}"
      // def dockerParams = dockerParams

    break

    default:
      error("Platform name is not defined or unsupported")

    break
  }
}

def sonar(proj) {
  echo "Check ${ACCOUNT}/${proj} code with SonarQube Scanner"
  // requires SonarQube Scanner 2.8+
  def scannerHome = tool 'SS28';
  dir("${proj}") {
    // withSonarQubeEnv("${SQS}") {
    withSonarQubeEnv('SQS') {
      sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=${ACCOUNT}:${proj} \
          -Dsonar.projectName=${proj} -Dsonar.projectVersion=${VERSION} \
          -Dsonar.sources=."
          // -Dsonar.sources=. -Dsonar.exclusions=**/*reduce*.h"
    }
  }
}

// mvn versions:set -DallowSnapshots=true -DgenerateBackupPoms=false -DnewVersion=$VERSION
def verset(ver, allowss) {
  def mvnHome = tool 'M339'
  sh ("'${mvnHome}/bin/mvn' -q versions:set -DallowSnapshots=${allowss} -DgenerateBackupPoms=false -DnewVersion=${ver}")
}

def release(proj) {
  // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  // Here you need to put stuff for atrifacts releasing

  // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  // Tag builded branch with new version
  if (CREATE_TAG) {
    echo ("Parameter CREATE_TAG is defined and it is: ${CREATE_TAG}")
    echo ("Adding tag ${proj}-${VERSION} to github.com/${ACCOUNT}/${proj}")
    dir("${proj}") {
      sshagent(credentials: ["${GITCREDID}"]) {
        sh ('git config user.email "${USER_MAIL}"')
        sh ('git config user.name \"${USER_NAME}\"')
        sh 'git status'
        // DO NOT ENABLE COMMIT AND TAGGING UNTIL IT IS NEEDED FOR REAL RELEASE
        sh('git commit -a -m \"Update to version ${VERSION}\"')
        sh("git tag -a ${proj}-${VERSION} -m ${proj}-${VERSION}")
        // sh("git push origin test-${proj}-${VERSION}")
      }
    }
  }
  else {
      echo ("Parameter CREATE_TAG is undefined so tagging has been skipped")
  }
}

return this;
