package hub.container

class Docker{
    def dockerURL
    def imgStable
    def imgLts

    Docker(def dockerURL, def imgStable, def imgLts){
        this.dockerURL = dockerURL
        this.imgStable = imgStable
        this.imgLts = imgLts
    }

    def tagStable(){
        sh "docker tag ${this.dockerURL}/${this.imgLts} ${this.dockerURL}/${this.imgStable}"
    }

    def pushStable(){
        sh "docker push ${this.dockerURL}/${this.imgStable}"
    }

    def removeImgLst(){
        sh "docker image rm -f ${this.dockerURL}/${this.imgLts}"
    }

    def removeImgStable(){
        sh "docker image rm -f ${this.dockerURL}/${this.imgStable}"
    }

    def stopContainer(def containerName){
        sh "docker stop ${containerName}"
    }

    def rmContainer(def containerName){
        sh "docker rm ${containerName}"
    }
}