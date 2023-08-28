import com.example.Constants

def call(boolean dockerBuild, Constants cons){
    if (dockerBuild == true){
        deployMethod.dockerDeploy([
            credId: Constants.SSH_CRED_ID,
            sshConnection: Constants.SSH_CONNECT_CMD,
            dockerURL: Constants.DOCKER_URL,
            imgLts: Constants.IMAGE_LATEST,
            containerName: Constants.CONTAIN_NAME
        ])
    }
    else{
        deployMethod.serviceDeploy([
            relJar: cons.getRelFileJar(),
            relJarUrl: cons.getRelJarURL()
        ])
    }
}