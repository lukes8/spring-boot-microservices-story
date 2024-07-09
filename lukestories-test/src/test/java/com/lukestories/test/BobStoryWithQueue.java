package com.lukestories.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class BobStoryWithQueue {

    @Test
    void test() {
        int rand = (new Random().nextInt()) % 3;
        int rand2 = new Random().nextInt(0, 2);
        System.out.println(rand);
        System.out.println(rand2);
    }

    @Test
    void bobInRetailQueueAndOthers() {

        System.out.println("Story with Bob and deque usage");
        System.out.println("Bob stays at first position in cash desk where are N people in total FIFO");
        System.out.println("The cash desk is suddenly closed and hence Bob needs to wait... LIFO");

        Queue<String> queueSimple = new LinkedList<>();

        queueSimple.add("A");
        queueSimple.add("B");
        queueSimple.offer("C");
        queueSimple.offer(null);

        System.out.println(queueSimple);

        Assertions.assertEquals(queueSimple.peek(), "A");
        Assertions.assertEquals(queueSimple.poll(), "A");
        Assertions.assertEquals(queueSimple.poll(), "B");
        Assertions.assertInstanceOf(Queue.class, queueSimple);

        System.out.println(queueSimple);

        Deque<Integer> doubleEndedQueue = new LinkedList<>();
        LinkedList<Integer> l = new LinkedList<>();
        doubleEndedQueue.offer(1);
        doubleEndedQueue.offer(32);
        doubleEndedQueue.offer(121);
        doubleEndedQueue.offer(3);

        System.out.println(doubleEndedQueue);

        Assertions.assertInstanceOf(Deque.class, doubleEndedQueue);
        Assertions.assertEquals(1, doubleEndedQueue.peekFirst());
        Assertions.assertEquals(3, doubleEndedQueue.peekLast());
        Assertions.assertEquals(1, doubleEndedQueue.pollFirst());
        Assertions.assertEquals(3, doubleEndedQueue.pollLast());

        Assertions.assertEquals(2, doubleEndedQueue.size());
        Assertions.assertEquals(32, doubleEndedQueue.peekFirst());
        Assertions.assertEquals(121, doubleEndedQueue.peekLast());
        Iterator<Integer> iterator = doubleEndedQueue.iterator();
        Assertions.assertEquals(32, iterator.next());
        Assertions.assertEquals(121, iterator.next());

        Iterator<Integer> iteratorDesc = doubleEndedQueue.descendingIterator();
        Assertions.assertEquals(121, iteratorDesc.next());
        Assertions.assertEquals(32, iteratorDesc.next());

        System.out.println(doubleEndedQueue);

        Deque<String> bobInQueueOrStack = new ArrayDeque<>();
        bobInQueueOrStack.offer("Bob");
        bobInQueueOrStack.offer("Polly");
        bobInQueueOrStack.offer("Ben");
        bobInQueueOrStack.offer("Elizabeth");
        Assertions.assertEquals("Bob", bobInQueueOrStack.peek());
        Assertions.assertEquals("Bob", bobInQueueOrStack.poll());
        Assertions.assertEquals(3, bobInQueueOrStack.size());
        bobInQueueOrStack.offerFirst("Bob");
        Assertions.assertEquals(4, bobInQueueOrStack.size());
        Assertions.assertEquals("Bob", bobInQueueOrStack.peek());

        System.out.println("Cash desk now closed, Bob needs to choose new one and wait");
        Assertions.assertEquals("Elizabeth", bobInQueueOrStack.pollLast());
        Assertions.assertEquals("Ben", bobInQueueOrStack.pollLast());
        Assertions.assertEquals("Polly", bobInQueueOrStack.pollLast());
        Assertions.assertEquals("Bob", bobInQueueOrStack.pollLast());

    }
}
