def call(Map config){
    return checkout([
        $class: 'GitSCM',
        branches: [[name: config.branch]],
        userRemoteConfigs: [
            [ url: config.repoURL ]
        ]
    ])
}