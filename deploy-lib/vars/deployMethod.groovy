import com.example.Constants

def dockerDeploy(Map config){
    sshagent(credentials: [config.credId]) {
        sh "scp -o StrictHostKeyChecking=no ${Constants.DOCKER_COMPOSE_PATH} ${Constants.SSH_USER}@${Constants.REMOTE_IPADDR}:~"
        sh "${config.sshConnection} docker pull ${Constants.PULL_URL}"
        sh "${config.sshConnection} PORT=${config.port} BRANCH=${config.branch} IMAGE_NAME=${config.dockerURL}/${config.imgLts} CONTAIN_NAME=${config.containerName}-${config.branch} docker compose up ${config.branch} -d"
    }

    ansiColor('xterm') {
        ansiblePlaybook(
            playbook: 'ansible/web-docker.yml',
            inventory: 'ansible/inventory.ini',
            colorized: true
        )
    }
}

def serviceDeploy(Map config){
    ansiColor('xterm') {
        ansiblePlaybook(
            playbook: 'ansible/web-artifact.yml',
            inventory: 'ansible/inventory.ini',
            extraVars: [
                jar_file_release: config.relJar,
                jar_file_url: config.relJarUrl
            ],
            colorized: true
        )
    }
}
