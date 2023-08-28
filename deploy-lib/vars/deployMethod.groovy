def dockerDeploy(Map config){
    sshagent(credentials: [config.credId]) {
    sh "scp -o StrictHostKeyChecking=no DOCKER_COMPOSE_PATH} SSH_USER}@REMOTE_IPADDR}:~"
    }
    sshagent(credentials: [config.credId]) {
        sh "${config.sshConnection} docker pull DOCKER_URL}/"
        sh "${config.sshConnection} IMAGE_NAME=${config.dockerURL}/${config.imgLts} CONTAIN_NAME=${config.containerName} docker compose up --force-recreate -d"
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
