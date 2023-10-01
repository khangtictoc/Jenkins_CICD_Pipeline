import com.example.Constants

def call(boolean dockerBuild, Constants cons, def port, def branch){
    if (dockerBuild == true){
        deployMethod.dockerDeploy([
            credId: Constants.SSH_CRED_ID,
            sshConnection: Constants.SSH_CONNECT_CMD,
            dockerURL: Constants.DOCKER_URL,
            imgLts: Constants.IMAGE_LATEST,
            containerName: Constants.CONTAIN_NAME,
            branch: cons.getBranch(),
            port: port,
            branch: branch
        ])
    }
    else{
        deployMethod.serviceDeploy([
            relJar: cons.getRelFileJar(),
            relJarUrl: cons.getRelJarURL()
        ])
    }
}