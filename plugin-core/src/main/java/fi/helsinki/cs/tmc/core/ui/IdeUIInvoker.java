package fi.helsinki.cs.tmc.core.ui;

import fi.helsinki.cs.tmc.core.domain.Review;
import fi.helsinki.cs.tmc.core.domain.SubmissionResult;
import fi.helsinki.cs.tmc.core.domain.TestCaseResult;

import java.util.List;

/**
 * Interface for invoking IDE UI functionality from the IDE-independent core.
 * IDE specific portion must implement this interface
 *
 */
public interface IdeUIInvoker {

    void invokeTestResultWindow(List<TestCaseResult> results);

    void invokeAllTestsPassedWindow(SubmissionResult result, String exerciseName);

    void invokeSomeTestsFailedWindow(SubmissionResult result, String exerciseName);

    void invokeAllTestsFailedWindow(SubmissionResult result, String exerciseName);

    void invokeSubmitToServerWindow();

    void invokeSendToPastebinWindow(String exerciseName);

    void invokePastebinResultDialog(String pasteUrl);

    void invokeRequestCodeReviewWindow(final String exerciseName);

    void invokeCodeReviewRequestSuccefullySentWindow();

    void raiseVisibleException(String message);

    void invokeCodeReviewDialog(Review review);

    void invokeMessageBox(String string);

    void invokeCodeReviewPopupNotification(List<Review> unseen);
}
