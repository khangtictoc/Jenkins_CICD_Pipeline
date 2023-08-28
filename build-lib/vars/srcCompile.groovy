def call(){
    sh 'chmod +x mvnw'
    sh './mvnw -DskipTests -Dcheckstyle.skip package'
}

