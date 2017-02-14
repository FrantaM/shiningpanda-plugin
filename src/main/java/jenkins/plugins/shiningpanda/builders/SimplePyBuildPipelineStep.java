package jenkins.plugins.shiningpanda.builders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.jenkinsci.plugins.workflow.steps.SynchronousNonBlockingStepExecution;
import org.kohsuke.stapler.DataBoundConstructor;

import com.google.common.collect.ImmutableSet;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Descriptor;
import hudson.model.Node;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.Builder;

import jenkins.model.Jenkins;
import jenkins.plugins.shiningpanda.workspace.Workspace;

/**
 * Python meta-step.
 * 
 * @author Franta Mejta
 */
public class SimplePyBuildPipelineStep extends Step {

    public final SimplePyBuildStep delegate;

    @DataBoundConstructor
    public SimplePyBuildPipelineStep(SimplePyBuildStep delegate) {
        this.delegate = delegate;
    }

    @Override
    public StepExecution start(StepContext sc) throws Exception {
        return new Execution(delegate, sc);
    }

    private static final class Execution extends SynchronousNonBlockingStepExecution<Void> {

        private transient final SimplePyBuildStep delegate;

        Execution(SimplePyBuildStep delegate, StepContext context) {
            super(context);
            this.delegate = delegate;
        }

        @Override protected Void run() throws Exception {
            FilePath workspace = getContext().get(FilePath.class);
            workspace.mkdirs();
            Run<?, ?> run = getContext().get(Run.class);
            delegate.perform(run,
                             Workspace.fromNode(getContext().get(Node.class), run.getParent(), null),
                             workspace,
                             getContext().get(Launcher.class),
                             getContext().get(TaskListener.class));
            return null;
        }

        @Override public String getStatus() {
            String supe = super.getStatus();
            return delegate != null ? delegate.getClass().getName() + ": " + supe : supe;
        }

        private static final long serialVersionUID = 1L;

    }

    @Extension
    public static final class DescriptorImpl extends StepDescriptor {

        @Override public String getFunctionName() {
            return "py";
        }

        @Override public String getDisplayName() {
            return "General Python Build Step";
        }

        @Override
        public boolean isMetaStep() {
            return true;
        }

        @Override
        public boolean isAdvanced() {
            return true;
        }

        public Collection<? extends Descriptor<?>> getApplicableDescriptors() {
            List<Descriptor<?>> r = new ArrayList<>();
            for (Descriptor<?> d : Jenkins.getInstance().getDescriptorList(Builder.class)) {
                if (SimplePyBuildStep.class.isAssignableFrom(d.clazz)) {
                    r.add(d);
                }
            }
            return r;
        }

        @Override
        public Set<Class<?>> getRequiredContext() {
            return ImmutableSet.of(Run.class, FilePath.class, Launcher.class, TaskListener.class, Node.class);
        }

    }

}
