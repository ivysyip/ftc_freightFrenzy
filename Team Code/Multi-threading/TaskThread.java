package org.firstinspires.ftc.teamcode.thread;

public class TaskThread {
    Actions actions;
    int delay;
    boolean isLoop;
    TaskRunnable taskRunnable;
    boolean started;

    public TaskThread(Actions actions) {
        this(0, actions, true);
    }

    public TaskThread(Actions actions, boolean isLoop) { this( 0, actions, isLoop );}

    public TaskThread(int delay, Actions actions, boolean isLoop ) {
        this.actions = actions;
        this.delay = delay;
        this.isLoop = isLoop;
        this.taskRunnable = new TaskRunnable();
        started = false;
    }

    /**
     * An interface to be passed to a {@link TaskThread} constructor.
     */
    public interface Actions {
        /**
         * Robot code to be ran periodically on its own thread.
         */
        public void work();
    }

    void start() {
        taskRunnable.start();
    }

    void stop() {
        taskRunnable.stop();
    }

    class TaskRunnable implements Runnable {
        private Thread t;

        TaskRunnable() {

        }

        public void run() {
            try {
                while(!t.isInterrupted()) {
                    actions.work();
                    Thread.sleep(delay);

                    if (!isLoop)
                        break;
                }
            } catch (InterruptedException e) {

            }
        }

        public void start() {
            if (t == null)
                t = new Thread(this);
            t.start();
        }

        public void stop() {
            t.interrupt();
        }
    }
}
