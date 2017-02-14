package jenkins.plugins.shiningpanda.builders;

import java.io.IOException;

import javax.annotation.Nonnull;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStep;

import jenkins.plugins.shiningpanda.workspace.Workspace;

/**
 * Extended BuildStep interface to launch current python
 * builders from pipeline.
 *
 * @author Franta Mejta
 */
public interface SimplePyBuildStep extends BuildStep {

    void perform(@Nonnull Run<?, ?> run,
                 @Nonnull Workspace installation,
                 @Nonnull FilePath workspace,
                 @Nonnull Launcher launcher,
                 @Nonnull TaskListener listener) throws InterruptedException, IOException;

}
