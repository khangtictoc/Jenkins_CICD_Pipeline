multibranchPipelineJob('dsl-multi-branches') {
    // Adds branch sources.
    branchSources {
    // Adds a Git branch source.
        // git {
        //     // Sets credentials for authentication with the remote repository.
        //     credentialsId('gitlab')
        //     // Sets a pattern for
        //     remote('http://192.168.56.10/jenkins/spring-petclinic.git')
        //     id('gitId')
        // }
        // Adds a GitHub branch source.
        branchSource {
            source{
                gitlab {
                    // Select the GitLab Server where you want the projects to be discovered from.
                    serverName('default')
                    // Specify the namespace which owns your projects.
                    projectOwner('jenkins')
                    // Select the project on which you want to perform the Multibranch Pipeline Job.
                    projectPath('jenkins/spring-petclinic')
                    credentialsId('virusGitlabUsername')
                    id('gitlab-dsl')
                }
            }
        }
    }

    factory {
        workflowBranchProjectFactory {
        // Relative location within the checkout of your Pipeline script.
        scriptPath('jenkins/Jenkinsfile')
        }
    }
    // Sets a description for the item.
    description("My DSL job")
    // Sets the name to display instead of the actual name.
    displayName("multi-branches DSL")
}