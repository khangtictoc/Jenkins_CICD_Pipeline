import hub.container.Docker

def call(Docker container){
    container.tagLts()
    container.loginRepo()
    container.pushLts()
    container.rmImgLtsLocal()
}