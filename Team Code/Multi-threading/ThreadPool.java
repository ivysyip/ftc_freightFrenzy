package org.firstinspires.ftc.teamcode.thread;

import java.util.ArrayList;
import java.util.List;

public class ThreadPool {
    private List<TaskThread> threads = new ArrayList<>();
    private boolean started = false;

    public final void registerThread(TaskThread taskThread) {
        threads.add(taskThread);
        // Reset started flag
        started = false;
    }

    public void startTasks() {
        if ( !started ) {
            for (TaskThread taskThread : threads) {
                taskThread.start();
            }
            started = true;
        }
    }
    public void startTask(int index) {
        if ( !threads.get(index).started) {
                threads.get(index).start();
            }
        threads.get(index).started = true;

    }

    public final void stopTasks() {
        for(TaskThread taskThread : threads) {
            taskThread.stop();
        }
        started = false;
    }
    public void stopTask(int index) {
        threads.get(index).stop();
    }
}
