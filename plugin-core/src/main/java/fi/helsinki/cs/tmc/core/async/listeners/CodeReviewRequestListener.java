package fi.helsinki.cs.tmc.core.async.listeners;

import fi.helsinki.cs.tmc.core.async.BackgroundTaskListener;
import fi.helsinki.cs.tmc.core.async.tasks.CodeReviewRequestTask;
import fi.helsinki.cs.tmc.core.ui.IdeUIInvoker;

public class CodeReviewRequestListener implements BackgroundTaskListener {

    private final CodeReviewRequestTask task;
    private final IdeUIInvoker uiInvoker;

    public CodeReviewRequestListener(final CodeReviewRequestTask task, final IdeUIInvoker uiInvoker) {

        this.task = task;
        this.uiInvoker = uiInvoker;
    }

    @Override
    public void onBegin() {

    }

    @Override
    public void onSuccess() {

        uiInvoker.invokeCodeReviewRequestSuccefullySentWindow();
    }

    @Override
    public void onFailure() {

        uiInvoker.raiseVisibleException("Failed to create the code review request.");
    }

    @Override
    public void onInterruption() {

    }
}
