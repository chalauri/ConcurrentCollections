package main;

import main.blocking_threadsafe_lists.Client;
import main.nonblocking_threadsafe_lists.AddTask;
import main.nonblocking_threadsafe_lists.PollTask;

import java.util.Date;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * Created by G.Chalauri on 03/29/17.
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
       // nonblockingThreadsafeListsExample();
        blockingThreadsafeListsExample();
    }

    //non-blocking thread-safe lists example
    private static void nonblockingThreadsafeListsExample() {
        ConcurrentLinkedDeque<String> list = new
                ConcurrentLinkedDeque<>();

        Thread threads[] = new Thread[100];

        for (int i = 0; i < threads.length; i++) {
            AddTask task = new AddTask(list);
            threads[i] = new Thread(task);
            threads[i].start();
        }

        System.out.printf("Main: %d AddTask threads have been launched\n", threads.length);

        for (int i = 0; i < threads.length; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.printf("Main: Size of the List: %d\n", list.size());


        for (int i = 0; i < threads.length; i++) {
            PollTask task = new PollTask(list);
            threads[i] = new Thread(task);
            threads[i].start();
        }
        System.out.printf("Main: %d PollTask threads have been launched\n", threads.length);

        for (int i = 0; i < threads.length; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.printf("Main: Size of the List: %d\n", list.size());

    }

    //blocking thread-safe lists example
    private static void blockingThreadsafeListsExample() throws InterruptedException {
        LinkedBlockingDeque<String> list = new LinkedBlockingDeque<>(3);

        Client client = new Client(list);
        Thread thread = new Thread(client);
        thread.start();

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 3; j++) {
                String request = list.take();
                System.out.printf("Main: Request: %s at %s. Size: %d\n", request, new Date(), list.size());
            }
            TimeUnit.MILLISECONDS.sleep(300);
        }

        System.out.printf("Main: End of the program.\n");
    }
}
