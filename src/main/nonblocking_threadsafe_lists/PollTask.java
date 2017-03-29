package main.nonblocking_threadsafe_lists;

import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Created by G.Chalauri on 03/29/17.
 */
public class PollTask implements Runnable {

    private ConcurrentLinkedDeque<String> list;

    public PollTask(ConcurrentLinkedDeque<String> list) {
        this.list = list;
    }


    @Override
    public void run() {
        for (int i = 0; i < 5000; i++) {
            list.pollFirst();
            list.pollLast();
        }
    }
}
