package net.nevercloud.lib.scheduler;


public class SchedulerTask implements Runnable {

    /**
     * The Runnable of this task which will be executed
     */

    private Runnable runner;

    /**
     * The delay in ticks before the task starts
     */

    private long startDelay;

    /**
     * The delay in ticks between repetitions
     */

    private long repeatDelay;

    /**
     * If the runner should be executed on another thread
     */

    private boolean async;

    /**
     * Number which counts the ticks of the scheduler to validate when the task should be repeated or started.
     * Resets to zero when the task is repeating and it reached the number of the {@link SchedulerTask#repeatDelay}
     */

    private long runCounter = 0;

    public SchedulerTask(Runnable runner, long startDelay, long repeatDelay, boolean async) {
        this.runner = runner;
        this.startDelay = startDelay;
        this.repeatDelay = repeatDelay;
        this.async = async;
    }

    @Override
    public void run() {
        this.runCounter++;
        if(this.hasBeenExecuted() && this.isRepeating()) {
            if(this.runCounter == this.repeatDelay) { // runCounter reached the number of the repeatDelay
                this.runner.run();
                this.runCounter = 0;
            } else if(this.runCounter > this.repeatDelay) // to be secure
                this.runCounter = 0;
        } else {
            if(this.runCounter == this.startDelay) { // runCounter reached the number of the startDelay
                this.runner.run();
                this.startDelay = -1; // symbolises that the task has been executed
            }
        }
    }

    /**
     * @return if the task should be repeated
     */

    public boolean isRepeating() {
        return this.repeatDelay != -1;
    }

    /**
     * @return if the task has been executed yet
     */

    public boolean hasBeenExecuted() {
        return this.startDelay == -1;
    }

    public long getStartDelay() {
        return startDelay;
    }

    public long getRepeatDelay() {
        return repeatDelay;
    }

    public boolean isAsync() {
        return async;
    }
}
