package com.lukestories.test;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertTrue;

class BobAndErrorRateFallbackStrategy {

	@Test
	void fallback_strategy_circuit_breaker_dontCallFn10TimesWhenOccur10FailsInSlidingWindow() throws InterruptedException {
		ErrorRateLimiter limiter = new ErrorRateLimiter();
		Supplier<Object> s1 = () -> limiter.someOtherFunc();

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

	public class ErrorRateLimiter {

		private static final int WINDOW_SIZE = 20;
		private static final int ERROR_THRESHOLD = 10;
		private static final int SKIP_COUNT = 10;

		private AtomicInteger totalCalls = new AtomicInteger(0);
		private AtomicInteger errorCount = new AtomicInteger(0);
		private AtomicInteger successCount = new AtomicInteger(0);
		private AtomicInteger skipCallCount = new AtomicInteger(0);
		private volatile boolean skipCalls = false;

		public void handle(Supplier<Object> customMethod) {
			if (skipCalls && skipCallCount.get() < SKIP_COUNT) {
				// Skip function calls while in skip mode
				skipCallCount.incrementAndGet();
				System.out.println("waiting");
				return;
			}

			if (skipCalls && skipCallCount.get() >= SKIP_COUNT) {
				// End skip mode after SKIP_COUNT calls
				skipCalls = false;
				skipCallCount.set(0);
			}

			totalCalls.incrementAndGet();

			try {
				customMethod.get();  // Invoke the actual function
				successCount.incrementAndGet();
			} catch (Exception e) {
				errorCount.incrementAndGet();
			}

			// Check if the error threshold is exceeded
			if (errorCount.get() >= ERROR_THRESHOLD) {
				skipCalls = true;  // Trigger skipping the next SKIP_COUNT calls
			}

			summary();

			// Automatically reset error and success counts every WINDOW_SIZE calls
			if (totalCalls.get() % WINDOW_SIZE == 0) {
				reset();
			}
		}

		private void summary() {
			System.out.println(totalCalls.get() % WINDOW_SIZE + "th call");
			System.out.println(errorCount.get() + " errors");
			System.out.println(successCount.get() + " success");
		}

		// Resets error and success counters
		private void reset() {
			errorCount.set(0);
			successCount.set(0);
		}

		public Object someOtherFunc() {
			if (Math.random() > 0.5) {
				throw new RuntimeException("Simulated failure");
			}
			return "Success";
		}

		public boolean shouldSkipCall() {
			return skipCalls;
		}
	}
}
