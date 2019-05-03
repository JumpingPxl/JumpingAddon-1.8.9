package de.jumpingpxl.jumpingaddon.util;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 04.03.2019
 */

public class Task {

	private Timer timer = new Timer();
	private Runnable runnable;
	private boolean started;

	public Task(Runnable runnable) {
		this.runnable = runnable;
	}

	public Task delay(long time, TimeUnit timeUnit) {
		if (!started) {
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					runnable.run();
				}
			}, timeUnit.toMillis(time));
			started = true;
		}
		return this;
	}

	public Task repeat(long time, TimeUnit timeUnit) {
		if (!started) {
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					runnable.run();
				}
			}, timeUnit.toMillis(time), timeUnit.toMillis(time));
			started = true;
		}
		return this;
	}

	public Task perform() {
		runnable.run();
		return this;
	}

	public void cancel() {
		timer.cancel();
	}
}

