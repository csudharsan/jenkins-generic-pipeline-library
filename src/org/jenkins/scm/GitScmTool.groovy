package org.jenkins.scm

public class GitScmTool implements ScmTool {
    private Map environment
    private Map parameters
    private Map config
    private step
    private ctxt

    GitScmTool(ctxt, environment, parameters, step) {
        this.environment = environment
        this.parameters = parameters
        this.ctxt = ctxt
        this.step = step
    }

    @Override

    public void run() {
        ctxt.echo("START ====> GitScmTool::run")
        def prodBranch = 'master'
        def gitCreds = parameters.get('gitCreds', 'CICD-BB-READ-ONLY')
        ctxt.echo("END ====> GitScmTool::run")
    }
}