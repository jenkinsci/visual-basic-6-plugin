package org.jenkinsci.plugins.vb6;

import hudson.Functions;
import hudson.model.Cause;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import hudson.model.queue.QueueTaskFuture;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.recipes.LocalData;

import java.nio.file.Files;
import java.nio.file.Paths;

public class VB6BuilderTest {

    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    public void testUnix() throws Exception {
        Assume.assumeFalse(Functions.isWindows());

        FreeStyleProject project1 = j.createFreeStyleProject("project1");
        project1.getBuildersList().add(new VB6Builder("test.vbp"));
        QueueTaskFuture<FreeStyleBuild> freeStyleBuildQueueTaskFuture = project1.scheduleBuild2(0, new Cause.UserIdCause());

        FreeStyleBuild freeStyleBuild = freeStyleBuildQueueTaskFuture.get();

        j.assertBuildStatus(Result.FAILURE, freeStyleBuild);
    }

    @Test
    public void testWindowsWithoutBuildToolPathDefined() throws Exception {
        Assume.assumeTrue(Functions.isWindows());

        FreeStyleProject project1 = j.createFreeStyleProject("project1");
        project1.getBuildersList().add(new VB6Builder("Project1.vbp"));
        QueueTaskFuture<FreeStyleBuild> freeStyleBuildQueueTaskFuture = project1.scheduleBuild2(0, new Cause.UserIdCause());

        FreeStyleBuild freeStyleBuild = freeStyleBuildQueueTaskFuture.get();

        j.assertBuildStatus(Result.FAILURE, freeStyleBuild);
    }

    @LocalData
    @Test
    public void testWindowsSuccess() throws Exception {
        Assume.assumeTrue(Functions.isWindows());
        Assume.assumeTrue(Files.exists(Paths.get("C:\\Program Files (x86)\\Microsoft Visual Studio\\VB98\\VB6.EXE")));

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
    public void testWindowsFails() throws Exception {
        Assume.assumeTrue(Functions.isWindows());
        Assume.assumeTrue(Files.exists(Paths.get("C:\\Program Files (x86)\\Microsoft Visual Studio\\VB98\\VB6.EXE")));

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
}
