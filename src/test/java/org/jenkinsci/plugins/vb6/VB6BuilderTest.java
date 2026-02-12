package org.jenkinsci.plugins.vb6;

import hudson.Functions;
import hudson.model.Cause;
import hudson.model.CauseAction;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import hudson.model.queue.QueueTaskFuture;
import org.htmlunit.html.HtmlForm;
import org.htmlunit.html.HtmlPage;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;
import org.jvnet.hudson.test.recipes.LocalData;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@WithJenkins
public class VB6BuilderTest {

    @Test
    void configRoundTrip(JenkinsRule j) throws Exception {
        FreeStyleProject p = j.createFreeStyleProject();
        VB6Builder orig = new VB6Builder("projectFile1.vbp");
        p.getBuildersList().add(orig);

        try (JenkinsRule.WebClient webClient = j.createWebClient()) {
            HtmlPage page = webClient.getPage(p, "configure");
            HtmlForm form = page.getFormByName("config");
            j.submit(form);
        }

        j.assertEqualBeans(orig, p.getBuildersList().get(VB6Builder.class), "projectFile");

    }

    @Test
    void testUnix(JenkinsRule j) throws Exception {
        Assumptions.assumeFalse(Functions.isWindows());

        FreeStyleProject project1 = j.createFreeStyleProject("project1");
        project1.getBuildersList().add(new VB6Builder("test.vbp"));
        QueueTaskFuture<FreeStyleBuild> freeStyleBuildQueueTaskFuture = project1.scheduleBuild2(0, new Cause.UserIdCause());

        FreeStyleBuild freeStyleBuild = freeStyleBuildQueueTaskFuture.get();

        j.assertBuildStatus(Result.FAILURE, freeStyleBuild);
    }

    @Test
    void testWindowsWithoutBuildToolPathDefined(JenkinsRule j) throws Exception {
        Assumptions.assumeTrue(Functions.isWindows());

        FreeStyleProject project1 = j.createFreeStyleProject("project1");
        project1.getBuildersList().add(new VB6Builder("Project1.vbp"));
        QueueTaskFuture<FreeStyleBuild> freeStyleBuildQueueTaskFuture = project1.scheduleBuild2(0, new Cause.UserIdCause());

        FreeStyleBuild freeStyleBuild = freeStyleBuildQueueTaskFuture.get();

        j.assertBuildStatus(Result.FAILURE, freeStyleBuild);
    }

    @LocalData
    @Test
    void testWindowsSuccess(JenkinsRule j) throws Exception {
        Assumptions.assumeTrue(Functions.isWindows());
        Assumptions.assumeTrue(Files.exists(Paths.get("C:\\Program Files (x86)\\Microsoft Visual Studio\\VB98\\VB6.EXE")));

        VB6Builder.DescriptorImpl vb6 = (VB6Builder.DescriptorImpl) j.jenkins.getBuilder("VB6Builder");
        vb6.setBuilderPath("C:\\Program Files (x86)\\Microsoft Visual Studio\\VB98\\VB6.EXE");
        vb6.save();

        FreeStyleProject project1 = j.createFreeStyleProject("project1");
        project1.getBuildersList().add(new VB6Builder("Project1.vbp"));
        QueueTaskFuture<FreeStyleBuild> freeStyleBuildQueueTaskFuture = project1.scheduleBuild2(0, new Cause.UserIdCause());

        FreeStyleBuild freeStyleBuild = freeStyleBuildQueueTaskFuture.get();

        j.assertBuildStatus(Result.SUCCESS, freeStyleBuild);
    }

    @LocalData
    @Test
    void testWindowsFails(JenkinsRule j) throws Exception {
        Assumptions.assumeTrue(Functions.isWindows());
        Assumptions.assumeTrue(Files.exists(Paths.get("C:\\Program Files (x86)\\Microsoft Visual Studio\\VB98\\VB6.EXE")));

        VB6Builder.DescriptorImpl vb6 = (VB6Builder.DescriptorImpl) j.jenkins.getBuilder("VB6Builder");
        vb6.setBuilderPath("C:\\Program Files (x86)\\Microsoft Visual Studio\\VB98\\VB6.EXE");
        vb6.save();

        FreeStyleProject project1 = j.createFreeStyleProject("project1");
        project1.getBuildersList().add(new VB6Builder("Project1.vbp"));
        QueueTaskFuture<FreeStyleBuild> freeStyleBuildQueueTaskFuture = project1.scheduleBuild2(0, new Cause.UserIdCause());

        FreeStyleBuild freeStyleBuild = freeStyleBuildQueueTaskFuture.get();

        j.assertBuildStatus(Result.FAILURE, freeStyleBuild);
        j.assertLogContains("Line 3 : Variable not defined", freeStyleBuild);
        j.assertLogContains("Build of 'Project1.exe' failed.", freeStyleBuild);
        j.assertLogContains("return code is 1", freeStyleBuild);
    }

    @LocalData
    @Test
    void testWindowsSuccessPipeline(JenkinsRule j) throws Exception {
        Assumptions.assumeTrue(Functions.isWindows());
        Assumptions.assumeTrue(Files.exists(Paths.get("C:\\Program Files (x86)\\Microsoft Visual Studio\\VB98\\VB6.EXE")));

        VB6Builder.DescriptorImpl vb6 = (VB6Builder.DescriptorImpl) j.jenkins.getBuilder("VB6Builder");
        vb6.setBuilderPath("C:\\Program Files (x86)\\Microsoft Visual Studio\\VB98\\VB6.EXE");
        vb6.save();

        WorkflowJob project = j.createProject(WorkflowJob.class, "project1");
        project.setDefinition(new CpsFlowDefinition("pipeline { agent any \n stages { stage('build') { steps { vb6 'Project1.vbp' } } } }", true));

        QueueTaskFuture<WorkflowRun> workflowRunQueueTaskFuture = project.scheduleBuild2(0, new CauseAction(new Cause.UserIdCause()));

        WorkflowRun workflowRun = workflowRunQueueTaskFuture.get();

        j.assertBuildStatus(Result.SUCCESS, workflowRun);
    }
}
