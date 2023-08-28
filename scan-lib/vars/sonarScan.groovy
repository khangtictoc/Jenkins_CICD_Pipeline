def call(Map config){
    withSonarQubeEnv("${config.sonarServer}") {
        sh """${config.scanHome}/bin/sonar-scanner -Dsonar.projectKey=${config.projectKey} \
            -Dsonar.projectName=${config.projectName} \
            -Dsonar.projectVersion=${config.version} \
            -Dsonar.sources=${config.targetSrc} \
            -Dsonar.java.binaries=${config.targetBin} \
            -Dsonar.junit.reportsPath=${config.rpJunit} \
            -Dsonar.jacoco.reportsPath=${config.rpJacoco} \
            -Dsonar.java.checkstyle.reportPaths=${config.rpCheckstyle}"""
    }
}