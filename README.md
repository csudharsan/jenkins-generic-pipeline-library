# Motivation
Jenkins pipeline is a widely used way to define jobs in Jenkins. There are two different syntaxes: Declarative and Scripted Pipelines. Both of them are based on Groovy DSL, though declarative is quite popular using it for advanced customization for project needs its cumbersome. The motivation behind this repo is to provide a template format for jenkins users who are interested to explore scripted pipelines in jenkins.

## Folder Structure
The directory structure consists of
* `vars` - where the primary pipline caller function resides. It contains the all the steps that your org needs for CI. The steps can be enabled/disabled based on the local variables in the pipeline.
* `src` - the class implementation of functions called by pipeline code is handled in this directory. 
* `resources` - this directory holds common utilities tha are required for any of the step in the pipeline. One such standard file is the `environment.yaml` that holds the env variables for a pipeline.
  
## Usage
Users can either call this code directly from their org jenkins instance or download and host a copy of it in their org repo to call it through jenkins.
A sample Jenkinsfile to use in service repo is provided in `test/Jenkinfile`

## Contributions
This code is open and users are free to edit and raise a pull request. We love to have maintainers for this repo. 

