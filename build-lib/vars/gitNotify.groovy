def call(){
    updateGitlabCommitStatus name: 'build', state: 'pending'
    updateGitlabCommitStatus name: 'build', state: 'success'
}