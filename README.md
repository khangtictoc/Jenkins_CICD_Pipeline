# Jenkins_pipeline-Maven-Nexus-Sonarqube-Docker
Using Jenkins pipeline to deploy Maven Web Application (written in Scripted format). Integrated with Nexus, Sonarqube and Docker

## Main Pipeline

<p align="center">
    <img src="resources/img/Jenkins_Pipeine.gif>
</p>

## Description
### Convention
Image:
- `stable`: Stable version of well-health app
- `latest`: Build with newest code, may not work correctly
- `$hash ($githash + 'DDMMYYYY')`: Build with version 'X' pushed by `$githash`, may not work correctly

Environment
- Production: Full pipeline
- Uat: Full pipeline
- Dev: First 4 stages

Deploy method
- As service
- As container