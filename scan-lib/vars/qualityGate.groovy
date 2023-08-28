def call(){
    timeout(time: 2, unit: 'MINUTES') {
        def result = waitForQualityGate abortPipeline: true
        if (result.status != "OK"){
            error "Your scanner encounters troubles !!!"
        }
        else{
            echo "Your scanner is run successfully !!!"
        }
    }
}