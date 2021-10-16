import org.jenkins.scm.ScmTool
import org.jenkins.scm.ScmToolFactory


import org.jenkins.scm.BuildTool
import org.jenkins.scm.BuildToolFactory

import org.jenkins.scm.PackageTool
import org.jenkins.scm.PackageToolFactory

import org.jenkins.scm.DeployTool
import org.jenkins.scm.DeployToolFactory


def call(Map parameters = [:]) {

    def STAGE

    def skipStages = parameters.get('skipStages')

    // Production Branch
    def prodBranch = 'master'

    parameters['prodBranch'] = prodBranch

    // Set default tgt params pipeline to prod branch
    parameters['defaultConfig'] = prodBranch

    def pEnv 
    timeout(time: 480, unit: 'MINUTES') {
        timestamps {
            node(parameters.get('buildInfra')['jenkinsNodeLabel']) {
                try {
                    pEnv = readYaml(text: libraryResource('Environment.yaml'))
                    // Set pipelineenvironment based on project defined in jenkinsfile
                    def project = parameters.get('project')

                    pEnv = pEnv['project'][project]
                    println("\t\t ===== Set Pipeline Environment ======")
                    pEnv.each{k, v -> println "${k}: ${v}"}

                    // Read & Print Input params in Jenkinsfile
                    println("\t\t ===== Set Pipeline Params ======")
                    parameters.each{k, v -> println "${k}: ${v}"}

                    // Clean up the existing directory
                    deleteDir()

                    // Cleanup jenkins job artifacts
                    def noofBuildsToKeep = parameters.get('noOfBuildsToKeep', '10')
                    properties(
                        [
                            [$class: 'JiraProjectProperty'],
                            disableConcurrentBuilds(),
                            [$class: 'BuildConfigProjectProperty', name='', namespace='', resourceVersion: '',
                                uid: ''
                            ],
                            buildDiscarder(logRotator(artifacDaysToKeepStr: '', artifactNumToKeepStr: 5))

                        ]
                    )

                    STAGE = 'scm'

                    if(! skipStages.contains(STAGE)) {
                        stageList = parameters.get(STAGE)
                        for(step in stageList) {
                            stageName = step['stageName']
                            stage(stageName) {
                                ScmTool scmTool = ScmToolFactory.getScm(this, pEnv, parameters, step)
                                scmTool.run()
                            }
                        }
                    }

                    STAGE = 'build'
                    if(! skipStages.contains(STAGE)) {
                        stageList = parameters.get(STAGE)
                        for(step in stageList) {
                            stageName = step['stagename']
                            stage(stageName) {
                                if(step['bldContainer']) {
                                    def bldContainerName = pEnv.docker.infra.registry
                                    blrContainerName += '/'+(step['bldContainer'])

                                    docker.image(bldContainerName).inside("-w /root -v /root/.m2:/root/.m2 -v /root/.docker:/root/.docker -v /var/run/docker.sock:/var/run/docker.sock") {
                                        BuildTool buildTool = BuildToolFactory.getBuild(this, pEnv, parameters, step)
                                        buildTool.run()
                                    }
                                } else {
                                    // Run on host
                                    BuildTool buildTool = BuildToolFactory.getBuild(this, pEnv, parameters, step)
                                    buildTool.run()
                                }
                            }
                        }
                    }
                } catch( Exception e) {
                    currentBuild.result = 'FAILURE'
                    throw e
                } finally {
                    if (parameters.get('archiveArtifacts')) {
                        artifacts.addAll(parameters.get('archiveArtifacts'))
                    }
                }
            }
        }
    }
}