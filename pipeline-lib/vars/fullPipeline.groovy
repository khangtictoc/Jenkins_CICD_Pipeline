import com.example.Constants
import hub.container.Docker

def call(){

    def cons = new Constants(
        env.BUILD_NUMBER,
        env.BUILD_TIMESTAMP,
        env.BRANCH_NAME
    )

    def container = new Docker(
        Constants.DOCKER_URL,
        Constants.IMAGE_STABLE,
        Constants.IMAGE_LATEST
    )

    def DOCKER_BUILD = false
    node{
        stage('Gitlab webhook notification') {
            gitNotify()
        }
        stage('Get GitHub Repository') {
            def scmVars = gitCheckout([
                branch: cons.getBranch(),
                repoURL: Constants.GITLAB_REPO_URL
            ])
            cons.setGitHash(scmVars.GIT_COMMIT)

        }
        stage('Compile Code') {
            srcCompile()
        }
        // stage('Unit Test') {
        //     srcTest()
        // }
        stage('Sonarqube Scan') {
            scannerHome = tool Constants.SONARSCANNER
            sonarScan([
                scanHome: scannerHome,
                sonarServer: Constants.SONARSERVER,
                projectKey: Constants.SONAR_PROJECT_KEY,
                projectName: Constants.SONAR_PROJECT_NAME,
                version: Constants.SONAR_PROJECT_VER,
                targetSrc: Constants.TARGET_SOURCE,
                targetBin: Constants.TARGET_COMPILED_JAVA,
                rpJunit: Constants.TARGET_JUNIT_REPORT,
                rpJacoco: Constants.TARGET_JACOCO_REPORT,
                rpCheckstyle: Constants.TARGET_CHECKSTYLE_REPORT
            ])
        }
        stage("Quality Gate") {
            qualityGate()
        }
        // stage('Build Docker image') {
        //     steps {
        //         // Add user 'jenkins' to 'docker' group
        //         // sudo usermod -aG docker 'jenkins'
        //         sh 'echo "[1/6] BUILD DOCKER IMAGE <<<<<<<<<<<<"'
        //         sh "docker build -f DOCKERFILE_PATH} -t IMAGE_LATEST} ."
        //     }
        // }
        if (env.BRANCH_NAME =~ /uat(\/\w+)*/ || env.BRANCH_NAME == 'main'){
            stage('Tag The Commit To Remote') {
                def tagName = tagCommit([
                    buildNum: cons.getBuildNum(),
                    timestamp: cons.getTimestamp(),
                    branch: cons.getBranch(),
                    gitHash: cons.getGitHash()
                ])
            }
            stage('Push artifacts to Nexus') {
                pushArtifact([
                    nexusVer: Constants.NEXUS_VER,
                    protocol: Constants.PROTOCOL,
                    url: Constants.NEXUSIP + ':' + Constants.NEXUSPORT,
                    group: Constants.TARGET_GROUP,
                    version: cons.getVersion(),
                    repo: Constants.RELEASE_REPO_ARTIFACT,
                    credId: Constants.NEXUS_LOGIN,
                    targetRel: Constants.TARGET_RELEASE,
                    file: "target/${Constants.RELEASE_ARTIFACT_NAME}.${Constants.FILE_TYPE}",
                    type: Constants.FILE_TYPE
                ])
            }
            if (DOCKER_BUILD){
                stage('Push Docker images to Nexus') {
                    sh "docker tag ${Constants.IMAGE_LATEST} ${Constants.DOCKER_URL}/${Constants.IMAGE_LATEST}"
                    sh "echo ${Constants.NEXUS_PASS} | docker login ${Constants.DOCKER_URL} -u ${Constants.NEXUS_USER} --password-stdin"
                    sh "docker push ${Constants.DOCKER_URL}/${Constants.IMAGE_LATEST}"
                    sh "docker image rm ${Constants.IMAGE_LATEST}"
                }
            }
            stage('Deploy Web App') {
                deployWeb(DOCKER_BUILD, cons)
            }
            stage('Website Health Check'){
                healthCheck([
                    dockerBuild: DOCKER_BUILD,
                    rmtIP: Constants.REMOTE_IPADDR,
                    rmtProxPort: Constants.REV_PROXY_PORT
                ],
                    container
                )
            }
        }
    }
}

