import hub.container.Docker

def call(Map config, Docker container){
    sleep(time: 30, unit: 'SECONDS')
    try {
        def CHECK_HEATH = sh(
            returnStdout: true,
            script: "curl -I http://${config.rmtIP}:${config.rmtProxPort} | head -n 1 | cut -d ' ' -f2"
        ).trim()
        sh "echo 'value of checking health: ${CHECK_HEATH}'"

        if (CHECK_HEATH =~ /^20[0-9]$/){
            if (config.dockerBuild){
                container.tagStable()
                container.pushStable()
                container.removeImgLst()
                container.removeImgStable()
            }
            currentBuild.result = 'SUCCESS'
        }
        else{
            if (config.dockerBuild){
                container.stopContainer("${Constants.CONTAIN_NAME}")
                container.rmContainer("${Constants.CONTAIN_NAME}")
                container.removeImgLst()
            }
            currentBuild.result = 'FAILURE'
        }
    }
    catch(error){
        echo "Failed to access Remote Web Application at port ${Constants.REV_PROXY_PORT}"
        currentBuild.result = 'FAILURE'
    }
}