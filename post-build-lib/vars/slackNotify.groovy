def onSuccess(Map config, boolean buildStatus){
    def BOT_ICON = libraryResource "img/avatars-000706520767-yiqbpu-t500x500.jpg"
    slackSend(
        teamDomain: config.teamDomain,
        channel: buildStatus == true ? config.successChannel : config.failedChannel,
        color: config.color,
        message: buildStatus == true ? config.msgSuccess : config.msgFailed,
        tokenCredentialId : config.slackToken,
        iconEmoji: "resources/img/avatars-000706520767-yiqbpu-t500x500.jpg",
        username: config.username
    )
}