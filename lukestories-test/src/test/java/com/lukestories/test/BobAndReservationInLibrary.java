package com.lukestories.test;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

class BobAndReservationInLibrary {

	String reserveRoom() {
		System.out.println("functiona interface success");
		return "room reserved";
	}

	String reserveRoomWithException() throws Exception {
		System.out.println("functional interface throw ex");
		throw new Exception();
	}

	@Test
	void thread_atomicInteger_error_handle() {

		Library library = new Library(10, 10);

		//todo why failure has 11 or failure 13? WHy the behaviour is not the same as using with classic Thread.start()?
		Runnable runnable = () -> library.handle(this::reserveRoomWithException);
		ExecutorService executorService = Executors.newFixedThreadPool(20);
		try {
			for (int i = 0; i < 20; i++) {
				System.out.println(i);
				executorService.submit(runnable);
			}
		} finally {
			executorService.shutdown();
			try {
				if (executorService.awaitTermination(5, TimeUnit.SECONDS)) {
					executorService.shutdownNow();
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new RuntimeException(e);
			}
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
//			synchronized (this) {
				if (counterError.get() < maxErrors) {
					try {
						result = bobMethod.process();
					} catch (Exception e) {
						counterError.incrementAndGet();
						System.out.println("failure " + counterError.get());
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
//			}
		}
	}

	@FunctionalInterface
	interface BobMethod {
		String process() throws Exception;
	}
}
