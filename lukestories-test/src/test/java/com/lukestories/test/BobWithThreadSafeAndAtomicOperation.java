package com.lukestories.test;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class BobWithThreadSafeAndAtomicOperation {

	String reserveRoom() {
		System.out.println("functiona interface success");
		return "room reserved";
	}

	String reserveRoomWithException() throws Exception {
		System.out.println("functional interface throw ex");
		throw new Exception();
	}

	@Test
	public void thread_atomicInteger_error_handle() {

		Library library = new Library(10, 10);

		for (int i = 0; i < 20; i++) {
			System.out.println(i);
			Runnable bob = () -> library.handle(this::reserveRoomWithException);
			new Thread(bob).start();
		}
	}

	static class Library {

		private final int maxWaiting;
		private final int maxErrors;
		private AtomicInteger counterError = new AtomicInteger(0);
		private AtomicInteger counterWaiting = new AtomicInteger(0);

		Library(int maxErrors, int maxWaiting) {
			this.maxErrors = maxErrors;
			this.maxWaiting = maxWaiting;
		}

		public Object handle(BobMethod bobMethod) {
			Object result = null;
			if (counterError.get() < maxErrors) {
				try {
					result = bobMethod.process();
				} catch (Exception e) {
					counterError.incrementAndGet();
					System.out.println("failure " + counterError);
				}
			} else if (counterError.get() >= maxErrors) {
//						Thread.sleep(1000);
					counterWaiting.incrementAndGet();
					System.out.println("waiting " + counterWaiting.get());
				if (counterWaiting.get() == maxWaiting) {
					counterError.set(0);
					counterWaiting.set(0);
				}
			}
			return result;
			}
	}

	@FunctionalInterface
	interface BobMethod {
		String process() throws Exception;
	}
}
