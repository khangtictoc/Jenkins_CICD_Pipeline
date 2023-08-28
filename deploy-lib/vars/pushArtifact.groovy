def call(Map config){
    nexusArtifactUploader(
        nexusVersion: config.nexusVer,
        protocol: config.protocol,
        nexusUrl: config.url,
        groupId: config.group,
        version: config.version,
        repository: config.repo,
        credentialsId: config.credId,
        artifacts: [
            [
                artifactId: config.targetRel,
                classifier: '',
                file: config.file,
                type: config.type
            ]
        ]
    )
}