package org.jenkinsci.plugins.vb6;
import com.google.common.base.Strings;
import hudson.AbortException;
import hudson.Launcher;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.AbstractProject;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.Builder;
import hudson.tasks.BuildStepDescriptor;
import hudson.util.ArgumentListBuilder;
import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static java.io.File.createTempFile;

/**
 * Sample {@link Builder}.
 *
 * <p>
 * When the user configures the project and enables this builder,
 * {@link DescriptorImpl#newInstance(StaplerRequest)} is invoked
 * and a new {@link VB6Builder} is created. The created
 * instance is persisted to the project configuration XML by using
 * XStream, so this allows you to use instance fields
 * to remember the configuration.
 *
 * <p>
 * When a build is performed, the {@link #perform} method will be invoked. 
 *
 * @author Kohsuke Kawaguchi
 */
public class VB6Builder extends Builder implements SimpleBuildStep {

    public enum MAKE_TYPE {EXE, DLL}

    private final String projectFile;
    private final String outDir;
    private final MAKE_TYPE buildType;

    // Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
    @DataBoundConstructor
    public VB6Builder(String projectFile, String outDir, String buildType) {
        this.projectFile = projectFile;
        this.outDir = outDir;
        this.buildType = MAKE_TYPE.valueOf(buildType);
    }

    public String getProjectFile() {
        return projectFile;
    }

    public String getOutDir() {
        return outDir;
    }

    public MAKE_TYPE getBuildType() {
        return buildType;
    }

    @Override
    public void perform(Run<?,?> build, FilePath workspace, Launcher launcher, TaskListener listener) throws InterruptedException, IOException {

        if (launcher.isUnix()) {
            throw new AbortException("nice try, but come back with a Windows machine");
        }

        ArgumentListBuilder args = new ArgumentListBuilder();
        args.add(getDescriptor().getBuilderPath());

        switch (getBuildType()){
            case EXE: args.add("/make"); break;
            case DLL: args.add("/makedll"); break;
            default:
                throw new AbortException("build type not valid");
        }

        if(!Strings.isNullOrEmpty(getOutDir())){
            args.add("/outdir").add(getOutDir());
        }

        FilePath tempPath = workspace.createTempFile("vb6build", "log");
        args.add("/out").add(tempPath.getRemote());

        args.add(getProjectFile());

        args.prepend("cmd.exe", "/C", "\"");
        args.add("\"", "&&", "exit", "%%ERRORLEVEL%%");


        //perform build
        int r = launcher.launch().cmds(args).pwd(workspace).join();
        if(r != 0) {
            build.setResult(Result.FAILURE);
        }
        listener.getLogger().println(tempPath.readToString());

        tempPath.delete();
    }

    // Overridden for better buildType safety.
    // If your plugin doesn't really define any property on Descriptor,
    // you don't have to do this.
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }

    /**
     * Descriptor for {@link VB6Builder}. Used as a singleton.
     * The class is marked as public so that it can be accessed from views.
     *
     * <p>
     * See <tt>src/main/resources/hudson/plugins/hello_world/VB6Builder/*.jelly</tt>
     * for the actual HTML fragment for the configuration screen.
     */
    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {


        /**
         * To persist global configuration information,
         * simply store it in a field and call save().
         *
         * <p>
         * If you don't want fields to be persisted, use <tt>transient</tt>.
         */
        private String builderPath;

        /**
         * In order to load the persisted global configuration, you have to 
         * call load() in the constructor.
         */
        public DescriptorImpl() {
            load();
        }


        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project types 
            return true;
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return "VB6";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            builderPath = formData.getString("builderPath");
            save();
            return super.configure(req,formData);
        }

        public String getBuilderPath() {
            return builderPath;
        }

        public void setBuilderPath(String builderPath) {
            this.builderPath = builderPath;
        }
    }
}

