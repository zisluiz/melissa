package model;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;

public class JavaFXConcurrent {
	private static JavaFXConcurrent instance;
	private Queue<Runnable> updates = new ConcurrentLinkedQueue<>();

	private JavaFXConcurrent() {
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

		Runnable task = new Runnable() {
			public void run() {
				Queue<Runnable> copy = new LinkedBlockingQueue<>();
				
				while (!getUpdates().isEmpty())
					copy.add(getUpdates().poll());
				
				getUpdates().clear();
				
				if (!copy.isEmpty()) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							while (!copy.isEmpty())
								copy.poll().run();;
						}
					});				
				}
			}
		};

		scheduler.scheduleAtFixedRate(task, 0, 20, TimeUnit.MILLISECONDS);
	}
	
	public void addUpdate(Runnable runnable) {
		updates.add(runnable);
	}
	
//	public void addUpdate(Runnable runnable) {
//		Platform.runLater(runnable);	
//	}	

	public Queue<Runnable> getUpdates() {
		return updates;
	}
	
	public static JavaFXConcurrent getInstance() {
		if (instance == null)
			instance = new JavaFXConcurrent();
		
		return instance;
	}
}
