package fi.helsinki.cs.tmc.core.async.tasks;

import fi.helsinki.cs.tmc.core.async.BackgroundTask;
import fi.helsinki.cs.tmc.core.async.StopStatus;
import fi.helsinki.cs.tmc.core.async.TaskStatusMonitor;
import fi.helsinki.cs.tmc.core.domain.Project;
import fi.helsinki.cs.tmc.core.domain.SubmissionResult;
import fi.helsinki.cs.tmc.core.domain.exception.InvalidProjectException;
import fi.helsinki.cs.tmc.core.services.ProjectDAO;
import fi.helsinki.cs.tmc.core.services.ProjectUploader;
import fi.helsinki.cs.tmc.core.ui.IdeUIInvoker;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UploaderTaskTest {

    private UploaderTask task;
    private ProjectUploader uploader;
    private String path;
    private TaskStatusMonitor progress;
    private Project project;
    private ProjectDAO dao;
    private IdeUIInvoker invoker;

    @Before
    public void setUp() {

        path = "mock_path";
        uploader = mock(ProjectUploader.class);
        progress = mock(TaskStatusMonitor.class);

        dao = mock(ProjectDAO.class);
        invoker = mock(IdeUIInvoker.class);
        project = mock(Project.class);
        when(dao.getProjectByFile(path)).thenReturn(project);
        task = new UploaderTask(uploader, path, dao, invoker);
    }

    @Test
    public void allMethodsAreCalledWithCorrectParametersAndSuccessReturnedOnCorrectCall() throws IOException, InvalidProjectException {

        when(progress.isCancelRequested()).thenReturn(false);
        when(uploader.getResult()).thenReturn(new SubmissionResult());

        assertEquals(BackgroundTask.RETURN_SUCCESS, task.start(progress));

        verify(progress, times(3)).incrementProgress(1);
        verify(uploader, times(1)).zipProjects();
        verify(uploader, times(1)).handleSubmissionResponse();
        verify(uploader, times(1)).handleSubmissionResult(Matchers.any(StopStatus.class));
    }

    @Test
    public void retunsFailureAndCallsMethodsCorrectlyWhenCancelRequestedAfterZipping() throws IOException, InvalidProjectException {

        when(progress.isCancelRequested()).thenReturn(false);

        Mockito.doAnswer(new Answer() {

            @Override
            public Object answer(final InvocationOnMock invocation) {

                when(progress.isCancelRequested()).thenReturn(true);
                return null;
            }
        }).when(uploader).zipProjects();

        assertEquals(BackgroundTask.RETURN_INTERRUPTED, task.start(progress));

        verify(progress, times(0)).incrementProgress(1);
        verify(uploader, times(1)).zipProjects();
        verify(uploader, times(0)).handleSubmissionResponse();
        verify(uploader, times(0)).handleSubmissionResult(Matchers.any(StopStatus.class));
    }

    @Test
    public void progressBarIsSetOnStart() {

        task.start(progress);
        verify(progress, times(1)).startProgress(anyString(), anyInt());
    }

    @Test
    public void projectIsSetToUploader() {

        task.start(progress);
        verify(uploader, times(1)).setProject(project);
    }

    @Test
    public void retunsFailureAndCallsMethodsCorrectlyWhenCancelRequestedAfterHandlingResponse() throws IOException, InvalidProjectException {

        when(progress.isCancelRequested()).thenReturn(false);

        Mockito.doAnswer(new Answer() {

            @Override
            public Object answer(final InvocationOnMock invocation) {

                when(progress.isCancelRequested()).thenReturn(true);
                return null;
            }
        }).when(uploader).handleSubmissionResponse();

        assertEquals(BackgroundTask.RETURN_INTERRUPTED, task.start(progress));

        verify(progress, times(1)).incrementProgress(1);
        verify(uploader, times(1)).zipProjects();
        verify(uploader, times(1)).handleSubmissionResponse();
        verify(uploader, times(0)).handleSubmissionResult(Matchers.any(StopStatus.class));
    }

    @Test
    public void retunsFailureAndCallsMethodsCorrectlyWhenCancelRequestedDuringWaitingResult() throws IOException, InvalidProjectException {

        when(progress.isCancelRequested()).thenReturn(false);

        Mockito.doAnswer(new Answer() {

            @Override
            public Object answer(final InvocationOnMock invocation) {

                when(progress.isCancelRequested()).thenReturn(true);
                return null;
            }
        }).when(uploader).handleSubmissionResult(Matchers.any(StopStatus.class));

        assertEquals(BackgroundTask.RETURN_FAILURE, task.start(progress));

        verify(progress, times(2)).incrementProgress(1);
        verify(uploader, times(1)).zipProjects();
        verify(uploader, times(1)).handleSubmissionResponse();
        verify(uploader, times(1)).handleSubmissionResult(Matchers.any(StopStatus.class));
    }

    @Test
    public void projectUploaderCallsErrorHandlerAndReturnsFalseOnException() throws IOException, InvalidProjectException {

        Mockito.doThrow(new RuntimeException("Error message here")).when(uploader).zipProjects();

        assertEquals(BackgroundTask.RETURN_FAILURE, task.start(progress));
        verify(invoker, times(1)).raiseVisibleException("An error occurred while uploading exercises:\nError message here");
    }

}
