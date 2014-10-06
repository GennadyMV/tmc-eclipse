package fi.helsinki.cs.tmc.core.async.tasks;

import fi.helsinki.cs.tmc.core.async.BackgroundTask;
import fi.helsinki.cs.tmc.core.async.TaskStatusMonitor;
import fi.helsinki.cs.tmc.core.domain.FeedbackAnswer;
import fi.helsinki.cs.tmc.core.services.FeedbackAnswerSubmitter;
import fi.helsinki.cs.tmc.core.ui.IdeUIInvoker;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class FeedbackSubmissionTaskTest {

    private FeedbackAnswerSubmitter submitter;
    private FeedbackSubmissionTask task;
    private List<FeedbackAnswer> answers;
    private String url;
    private IdeUIInvoker invoker;

    @Before
    public void setUp() {

        submitter = mock(FeedbackAnswerSubmitter.class);
        url = "mockUrl";
        answers = new ArrayList<FeedbackAnswer>();

        invoker = mock(IdeUIInvoker.class);
        task = new FeedbackSubmissionTask(submitter, answers, url, invoker);
    }

    @Test
    public void feedbackAnswerSubmitterIsCalledWhenTaskIsRun() {

        task.start(mock(TaskStatusMonitor.class));
        verify(submitter, times(1)).submitFeedback(answers, url);
    }

    @Test
    public void feedbackAnswerSubmitterReturnsSuccess() {

        assertEquals(BackgroundTask.RETURN_SUCCESS, task.start(mock(TaskStatusMonitor.class)));

    }

    @Test
    public void feedbackAnswerSubmitterCallsErrorHandlerAndReturnsFalseOnException() {

        Mockito.doThrow(new RuntimeException("Error message here")).when(submitter).submitFeedback(answers, url);

        assertEquals(BackgroundTask.RETURN_FAILURE, task.start(mock(TaskStatusMonitor.class)));
        verify(invoker, times(1)).raiseVisibleException("An error occured while submitting feedback:\nError message here");
    }

}
