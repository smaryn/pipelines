env.ACCOUNT = "smaryn"
env.PROJECT = "pipelines"

env.DOCKER_UBUNTU_SONAR = "zensam/ubuntu4sonar:latest"

dockerParams = "-v ${WORKSPACE}:${WORKSPACE}:rw -v ${JENKINS_M2DIR_AMD64}:/home/jenkins/.m2:rw"
dockerParams_tmpfs = "-v ${WORKSPACE}:${WORKSPACE}:rw ${JENKINS_M2DIR_AMD64}:/home/jenkins/.m2:rw --tmpfs /tmp:size=3g"
