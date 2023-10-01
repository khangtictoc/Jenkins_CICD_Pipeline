import com.example.Constants

def sendMessage(boolean isFailed, Constants cons){
    def channelName
    def msgText
    def finalResult

    if (isFailed){
        channelName = Constants.FAIL_CHANNEL
        msgText = cons.slackFailMsg()
        finalResult = 'FAILURE'
    }
    else{
        channelName = Constants.SUCESS_CHANNEL
        msgText = cons.slackSuccessMsg()
        finalResult = 'SUCCESS'
    }
    slackSend(
        teamDomain: Constants.TEAM_DOMAIN,
        channel: channelName,
        message: msgText,
        color: Constants.MESS_COLOR,
        tokenCredentialId: Constants.SLACK_TOKEN_ID,
        iconEmoji: Constants.BOT_ICON,
        botUser: Constants.BOT_NAME
    )

    currentBuild.result = finalResult
}