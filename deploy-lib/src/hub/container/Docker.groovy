package hub.container
import com.example.Constants

class Docker{
    def dockerURL
    def imgStable
    def imgLts
    def dockerfile

    Docker(def dockerURL, def imgStable, def imgLts, def dockerfile){
        this.dockerURL = dockerURL
        this.imgStable = imgStable
        this.imgLts = imgLts
        this.dockerfile = dockerfile
    }

    def buildImg(){
        "docker build  -f ${this.imgStable} -t ${this.imgLts} .".execute()
    }

    def tagLts(){
        "docker tag ${this.imgLts} ${this.dockerURL}/${this.imgLts}".execute()
    }
    def tagStable(){
        "docker tag ${this.dockerURL}/${this.imgLts} ${this.dockerURL}/${this.imgStable}".execute()
    }

    def pushStable(){
        "docker push ${this.dockerURL}/${this.imgStable}".execute()
    }

    def pushLts(){
        "docker push ${this.dockerURL}/${this.imgLts}".execute()
    }

    def loginRepo(){
        "echo ${Constants.NEXUS_PASS} | docker login ${Constants.DOCKER_URL} -u ${Constants.NEXUS_USER} --password-stdin".execute()
    }

    def rmImgLtsLocal(){
        "docker image rm -f ${this.imgLts}".execute()
    }

    def rmImgLts(){
        "docker image rm -f ${this.dockerURL}/${this.imgLts}".execute()
    }

    def rmImgStable(){
        "docker image rm -f ${this.dockerURL}/${this.imgStable}".execute()
    }

    def stopContainer(def containerName){
        "docker stop ${containerName}".execute()
    }

    def rmContainer(def containerName){
        "docker rm ${containerName}".execute()
    }
}