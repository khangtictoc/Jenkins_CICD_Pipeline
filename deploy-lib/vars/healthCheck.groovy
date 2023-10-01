import hub.container.Docker
import com.example.Constants
//import com.example.SlackNotify


def call(Map config, Docker container){
    sleep(time: 30, unit: 'SECONDS')

    def CHECK_HEATH = sh(
        returnStdout: true,
        script: "curl -I http://${config.rmtIP}:${config.rmtProxPort} | head -n 1 | cut -d ' ' -f2"
    ).trim()
    sh "echo 'value of checking health: ${CHECK_HEATH}'"

    if (CHECK_HEATH =~ /^20[0-9]$/){
        if (config.dockerBuild){
            echo "${CHECK_HEATH}"
            container.tagStable()
            container.pushStable()
            container.rmImgLts()
            container.rmImgStable()
        }
        return false
    }
    else{
        if (config.dockerBuild){
            container.stopContainer("${Constants.CONTAIN_NAME}")
            container.rmContainer("${Constants.CONTAIN_NAME}")
            container.rmImgLts()
        }
        return true
    }
}