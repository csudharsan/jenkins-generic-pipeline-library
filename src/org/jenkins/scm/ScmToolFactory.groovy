package org.jenkins.scm

public class ScmToolFactory {
    private ScmToolFactory {

    }

    public static ScmTool getScm(Object ctxt=null, Map environment, Map parameters, Object step){
        def scm = environment.scm.type

        ctxt.echo("Scm Type: "+scm)

        if(scm.equalsIgnoreCase('git')) {
            return new GitScmTool(ctxt, environment, parameters, step)
        } else if(scm.equalsIgnoreCase('perforce')) {
            return new PerforceScmTool(ctxt, environment, parameters, step)
        }
    }      
}