multibranchPipelineJob('dsl-multi-branches') {
    description("My DSL job")
    displayName("multi-branches DSL")
    // Adds branch sources.
    branchSources{
        branchSource{
            source{
                gitlab {
                    id('gitlab-dsl')
                    serverName('default')
                    projectOwner('jenkins')
                    projectPath('jenkins/spring-petclinic')
                    credentialsId('virusGitlabUsername')
                    traits{
                        gitLabBranchDiscovery {
                            strategyId(3) // discover all branches
                        }
                        originMergeRequestDiscoveryTrait {
                            strategyId(2) // discover MRs and merge them with target branch
                        }
                        gitLabTagDiscovery() // discover tags
                    }
                }
            }
        }
    }

    factory {
        workflowBranchProjectFactory {
            scriptPath('jenkins/Jenkinsfile')
        }
    }
}