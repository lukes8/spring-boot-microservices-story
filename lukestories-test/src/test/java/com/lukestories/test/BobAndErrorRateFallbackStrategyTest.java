package com.lukestories.test;

import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertTrue;

class BobAndErrorRateFallbackStrategyTest {

	@Test
	void fallback_strategy_circuit_breaker_dontCallFn10TimesWhenOccur10FailsInSlidingWindow() throws InterruptedException {
		ErrorRateLimiter limiter = new ErrorRateLimiter();
		Supplier<Object> s1 = limiter::someOtherFunc;

		// Create a thread pool with 20 threads
		ExecutorService executor = Executors.newFixedThreadPool(20);

		AtomicInteger totalCallsMade = new AtomicInteger();
		AtomicInteger totalCallsSkipped = new AtomicInteger();

		// Submit tasks to the executor
		for (int i = 0; i < 100; i++) {
			executor.submit(() -> {
				if (limiter.shouldSkipCall()) {
					totalCallsSkipped.incrementAndGet();
				} else {
					totalCallsMade.incrementAndGet();
					limiter.handle(s1);
				}
			});
		}

		executor.shutdown();
		executor.awaitTermination(1, TimeUnit.MINUTES);

		assertTrue(totalCallsSkipped.get() > 0, "Expected some calls to be skipped during fallback mode");
		assertTrue(totalCallsMade.get() + totalCallsSkipped.get() <= 100, "Total calls should not exceed the number of submitted tasks");
	}

	public static class ErrorRateLimiter {

		private static final int WINDOW_SIZE = 20;
		private static final int ERROR_THRESHOLD = 10;
		private static final int SKIP_COUNT = 10;

		private final Queue<Boolean> errorQueue = new LinkedList<>();
		private final AtomicInteger skipCallCount = new AtomicInteger(0);
		private volatile boolean skipCalls = false;

		public void handle(Supplier<Object> customMethod) {
			if (skipCalls && skipCallCount.get() < SKIP_COUNT) {
				// Skip function calls while in skip mode
				skipCallCount.incrementAndGet();
				return;
			}

			if (skipCalls && skipCallCount.get() >= SKIP_COUNT) {
				// End skip mode after SKIP_COUNT calls
				skipCalls = false;
				skipCallCount.set(0);
			}

			boolean isError = false;
			try {
				customMethod.get();  // Invoke the actual function
			} catch (Exception e) {
				isError = true;
			}

			// Add the result to the queue
			errorQueue.add(isError);

			// Remove the oldest result if the queue exceeds the window size
			if (errorQueue.size() > WINDOW_SIZE) {
				errorQueue.poll();
			}

			// Calculate the number of errors in the queue
			long errorCount = errorQueue.stream().filter(result -> result).count();

			// Check if the error threshold is exceeded
			if (errorCount >= ERROR_THRESHOLD) {
				skipCalls = true;  // Trigger skipping the next SKIP_COUNT calls
			}

			summary();
		}

		public boolean shouldSkipCall() {
			return skipCalls;
		}

		private void summary() {
			System.out.println(errorQueue.stream().filter(f -> !f).count() + " success");
			System.out.println(errorQueue.stream().filter(f -> f).count() + " errors");
		}

		public Object someOtherFunc() {
			if (Math.random() > 0.5) {
				throw new RuntimeException("Simulated failure");
			}
			return "Success";
		}
	}
}
