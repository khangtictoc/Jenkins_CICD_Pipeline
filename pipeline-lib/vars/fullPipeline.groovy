import com.example.Constants
import hub.container.Docker

def getdeployPort(branch){
    switch(branch){
        case ~/uat(\/\w+)*/:
            return Constants.UAT_PORT
        case ~/dev(\/\w+)*/:
            return Constants.DEV_PORT
        case 'main':
            return Constants.MAIN_PORT
        default:
            return Constants.MAIN_PORT
    }
}

def call(){
    def cons = new Constants(
        env.BUILD_NUMBER,
        env.BUILD_URL,
        env.BUILD_TIMESTAMP,
        env.BRANCH_NAME
    )

    def container = new Docker(
        Constants.DOCKER_URL,
        Constants.IMAGE_STABLE,
        Constants.IMAGE_LATEST,
        Constants.DOCKERFILE_PATH
    )

    def DOCKER_BUILD = true
    def port = getdeployPort(env.BRANCH_NAME)

    try{
        node{
            stage('Gitlab webhook notification') {
                gitNotify()
            }
            stage('Get GitHub Repository') {
                def scmVars = gitCheckout([
                    branch: env.BRANCH_NAME =~ /^MR/ ? env.CHANGE_BRANCH : cons.getBranch(),
                    repoURL: Constants.GITLAB_REPO_URL
                ])
                cons.setGitHash(scmVars.GIT_COMMIT)

            }
            stage('Compile Code') {
                srcCompile()
            }
            stage('Unit Test') {
                srcTest()
            }
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
            if (DOCKER_BUILD){
                stage('Build Docker image') {
                    // Add user 'jenkins' to 'docker' group
                    // sudo usermod -aG docker 'jenkins'
                    echo "${env.BRANCH_NAME}"
                    container.buildImg()
                }
            }
            if (env.BRANCH_NAME =~ /uat(\/\w+)*/ || env.BRANCH_NAME =~ /dev(\/\w+)*/  || env.BRANCH_NAME == 'main'){
                stage('Tag The Commit To Remote') {
                    def tagName = tagCommit([
                        buildNum: cons.getBuildNum(),
                        timestamp: cons.getTimestamp(),
                        branch: cons.getBranch(),
                        gitHash: cons.getGitHash()
                    ])
                }

                if (!DOCKER_BUILD){
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
                }
                if (DOCKER_BUILD){
                    stage('Push Docker images to Nexus') {
                        pushLtsToNexus(container)
                    }
                }
                stage('Deploy Web App') {
                    deployWeb(DOCKER_BUILD, cons, port, "${env.BRANCH_NAME}")
                }
                stage('Website Health Check + Slack notification'){
                    def isFailed = healthCheck([
                        dockerBuild: DOCKER_BUILD,
                        rmtIP: Constants.REMOTE_IPADDR,
                        rmtProxPort: Constants.REV_PROXY_PORT
                    ],
                        container
                    )
                    // slackWorkspace.sendMessage(isFailed)
                }
            }
        }
    }
    catch(error){
        echo "Failed to execute the pipeline"
        // slackWorkspace.sendMessage(true)
        echo "${error.toString()}"
        echo "${error.getMessage()}"
    }
}