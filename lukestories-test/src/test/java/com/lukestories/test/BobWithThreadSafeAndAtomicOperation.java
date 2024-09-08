package com.lukestories.test;

import org.junit.jupiter.api.Test;

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

		Bob bob = new Bob(10, 10);

		for (int i = 0; i < 20; i++) {
			System.out.println(i);
			Thread t = new Thread(() -> {
				bob.handle(this::reserveRoomWithException);
			});
			t.start();
		}
	}

	static class Bob {

		private final int maxWaiting;
		private final int maxErrors;
		private int counterError = 0;
		private int counterWaiting = 0;

		Bob(int maxErrors, int maxWaiting) {
			this.maxErrors = maxErrors;
			this.maxWaiting = maxWaiting;
		}

		public Object handle(BobMethod bobMethod) {

			Object result = null;
			synchronized (this) {

				if (counterError < maxErrors) {
					try {
						result = bobMethod.process();
					} catch (Exception e) {
						counterError++;
						System.out.println("failure " + counterError);
					}
				} else if (counterError >= maxErrors) {
//						Thread.sleep(1000);
						counterWaiting++;
						System.out.println("waiting " + counterWaiting);
					if (counterWaiting == maxWaiting) {
						counterError = 0;
						counterWaiting = 0;
					}
				}
				return result;
			}
		}
	}

	@FunctionalInterface
	interface BobMethod {
		String process() throws Exception;
	}
}
