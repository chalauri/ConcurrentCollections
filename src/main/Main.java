package main;

import main.blocking_threadsafe_list_by_priority.Event;
import main.blocking_threadsafe_list_by_priority.PTask;
import main.blocking_threadsafe_lists.Client;
import main.generating_concurrent_random_numbers.TaskLocalRandom;
import main.nonblocking_threadsafe_lists.AddTask;
import main.nonblocking_threadsafe_lists.PollTask;
import main.threadsafe_lists_with_delayed_elements.DEvent;
import main.threadsafe_lists_with_delayed_elements.DTask;
import main.threadsafe_navigable_maps.CTask;
import main.threadsafe_navigable_maps.Contact;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by G.Chalauri on 03/29/17.
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {

        // nonblockingThreadsafeListsExample();
        // blockingThreadsafeListsExample();
        // blockingThreadsafeListByPriorityExample();
        // threadsafeListsWithDelayedElementsExample();
        // threadsafeNavigableMapExample();
        generatingConcurrentRandomNumbersExample();
    }

    private static void generatingConcurrentRandomNumbersExample() {
        Thread threads[] = new Thread[3];

        for (int i = 0; i < 3; i++) {
            TaskLocalRandom task = new TaskLocalRandom();
            threads[i] = new Thread(task);
            threads[i].start();
        }
    }

    private static void threadsafeNavigableMapExample() {
        ConcurrentSkipListMap<String, Contact> map;
        map = new ConcurrentSkipListMap<>();

        Thread threads[] = new Thread[25];
        int counter = 0;

        for (char i = 'A'; i < 'Z'; i++) {
            CTask task = new CTask(map, String.valueOf(i));
            threads[counter] = new Thread(task);
            threads[counter].start();
            counter++;
        }

        for (int i = 0; i < 25; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.printf("Main: Size of the map: %d\n", map.size());
        Map.Entry<String, Contact> element;
        Contact contact;
        element = map.firstEntry();
        contact = element.getValue();
        System.out.printf("Main: First Entry: %s: %s\n", contact.
                getName(), contact.getPhone());

        element = map.lastEntry();
        contact = element.getValue();
        System.out.printf("Main: Last Entry: %s: %s\n", contact.
                getName(), contact.getPhone());


        System.out.printf("Main: Submap from A1996 to B1002: \n");
        ConcurrentNavigableMap<String, Contact> submap = map.
                subMap("A1996", "B1002");
        do {
            element = submap.pollFirstEntry();
            if (element != null) {
                contact = element.getValue();
                System.out.printf("%s: %s\n", contact.getName(), contact.
                        getPhone());
            }
        } while (element != null);
    }

    private static void threadsafeListsWithDelayedElementsExample() throws InterruptedException {
        DelayQueue<DEvent> queue = new DelayQueue<>();
        Thread threads[] = new Thread[5];

        for (int i = 0; i < threads.length; i++) {
            DTask task = new DTask(i + 1, queue);
            threads[i] = new Thread(task);
        }

        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
        }

        for (int i = 0; i < threads.length; i++) {
            threads[i].join();
        }

        do {
            int counter = 0;
            DEvent event;
            do {
                event = queue.poll();
                if (event != null) counter++;
            } while (event != null);
            System.out.printf("At %s you have read %d events\n", new
                    Date(), counter);
            TimeUnit.MILLISECONDS.sleep(500);
        } while (queue.size() > 0);

    }

    //blocking thread-safe list by priority
    private static void blockingThreadsafeListByPriorityExample() {
        PriorityBlockingQueue<Event> queue = new
                PriorityBlockingQueue<>();

        Thread taskThreads[] = new Thread[5];

        for (int i = 0; i < taskThreads.length; i++) {
            PTask task = new PTask(i, queue);
            taskThreads[i] = new Thread(task);
        }

        for (int i = 0; i < taskThreads.length; i++) {
            taskThreads[i].start();
        }

        for (int i = 0; i < taskThreads.length; i++) {
            try {
                taskThreads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.printf("Main: Queue Size: %d\n", queue.size());
        for (int i = 0; i < taskThreads.length * 1000; i++) {
            Event event = queue.poll();
            System.out.printf("Thread %s: Priority %d\n", event.
                    getThread(), event.getPriority());
        }

        System.out.printf("Main: Queue Size: %d\n", queue.size());
        System.out.printf("Main: End of the program\n");
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
