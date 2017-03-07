env.DOCKER_UBUNTU_SONAR = "zensam/ubuntu4sonar:latest"

env.JENKINS_M2DIR = "/var/lib/jenkins/storage/docker_m2"

dockerParams = "-v ${WORKSPACE}:${WORKSPACE}:rw -v ${JENKINS_M2DIR}:/home/jenkins/.m2:rw"
dockerParams_tmpfs = "-v ${WORKSPACE}:${WORKSPACE}:rw ${JENKINS_M2DIR}:/home/jenkins/.m2:rw --tmpfs /tmp:size=3g"
