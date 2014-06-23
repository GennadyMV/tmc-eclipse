package fi.helsinki.cs.plugin.tmc.async.tasks;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fi.helsinki.cs.plugin.tmc.async.BackgroundTask;
import fi.helsinki.cs.plugin.tmc.async.TaskFeedback;
import fi.helsinki.cs.plugin.tmc.domain.Project;
import fi.helsinki.cs.plugin.tmc.domain.TestRunResult;
import fi.helsinki.cs.plugin.tmc.ui.IdeUIInvoker;
import fi.helsinki.cs.plugin.tmc.utils.TestResultParser;

/**
 * Background task for maven test runner. For all your maven testing needs.
 * 
 * 
 */
public abstract class MavenTestrunnerTask extends TestrunnerTask {

    private Project project;
    private TestRunResult results;
    private IdeUIInvoker invoker;

    /**
     * 
     * @param project
     *            Project to be tested
     * @param invoker
     *            Object that allows core to invoke ide ui, in this case error
     *            messages
     */
    public MavenTestrunnerTask(Project project, IdeUIInvoker invoker) {
        super("Running tests");
        this.project = project;
        this.invoker = invoker;
    }

    public TestRunResult get() {
        return this.results;
    }

    public abstract int runMaven(List<String> goals, Project project);

    @Override
    public int start(TaskFeedback progress) {
        progress.startProgress(this.getDescription(), 3);

        List<String> goals = new ArrayList<String>();
        goals.add("test-compile");

        if (runMaven(goals, project) != 0) {
            invoker.raiseVisibleException("Unable to compile project.");
            return BackgroundTask.RETURN_FAILURE;
        }

        progress.incrementProgress(1);
        if (shouldStop(progress)) {
            return BackgroundTask.RETURN_INTERRUPTED;
        }

        File resultFile = new File(project.getRootPath() + "/target/test_output.txt");

        goals.clear();
        goals.add("fi.helsinki.cs.tmc:tmc-maven-plugin:1.6:test");

        if (runMaven(goals, project) != 0) {
            invoker.raiseVisibleException("Unable to run tests.");
            return BackgroundTask.RETURN_FAILURE;
        }

        progress.incrementProgress(1);
        if (shouldStop(progress)) {
            return BackgroundTask.RETURN_INTERRUPTED;
        }

        try {
            this.results = new TestResultParser().parseTestResults(resultFile);
            resultFile.delete();
            progress.incrementProgress(1);
        } catch (IOException e) {
            invoker.raiseVisibleException("Unable to parse testresults.");
            return BackgroundTask.RETURN_FAILURE;
        }

        return BackgroundTask.RETURN_SUCCESS;
    }
}
