package fi.helsinki.cs.tmc.core.old.services;

import java.util.List;

import fi.helsinki.cs.tmc.core.old.domain.FeedbackAnswer;
import fi.helsinki.cs.tmc.core.old.services.http.ServerManager;

/**
 * Class used by the feedback submission background task.
 */
public class FeedbackAnswerSubmitter {

    private final ServerManager server;

    public FeedbackAnswerSubmitter(final ServerManager server) {

        this.server = server;

    }

    public void submitFeedback(final List<FeedbackAnswer> answers, final String answerUrl) {

        if (answers == null || answers.isEmpty() || answerUrl == null || answerUrl.trim().length() == 0) {
            return;
        }

        if (answersAreEmpty(answers)) {
            return;
        }

        server.submitFeedback(answerUrl, answers);
    }

    private boolean answersAreEmpty(final List<FeedbackAnswer> answers) {

        for (final FeedbackAnswer a : answers) {
            if (a.getAnswer().trim().length() != 0) {
                return false;
            }
        }
        return true;
    }
}
