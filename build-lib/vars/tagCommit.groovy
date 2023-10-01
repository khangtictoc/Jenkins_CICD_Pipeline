def call(Map config){
    def tagName = config.buildNum + '-' +
                  config.timestamp + '-' +
                  config.branch + '-' +
                  config.gitHash
    sh "git checkout ${config.branch}"
    sh "git pull"
    sh "git tag ${tagName} ${config.branch}"
    sh "git push origin ${tagName}"
    return tagName
}