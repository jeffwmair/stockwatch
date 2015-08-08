package com.jwm.stockwatch;

import java.io.IOException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.jwm.stockwatch.task.*;

/**
 * Application main worker
 * @author Jeff
 *
 */
public class App {

	private static Logger log = LogManager.getLogger(App.class);

	private PriceUpdateTask task;
	private int minutesBetweenUpdates;

	public App(PropertiesLoaderImpl props, PriceUpdateTask task) throws IOException {
		this.task = task;
		this.minutesBetweenUpdates = Integer.parseInt(props.getProperties().getProperty("sleeptime_minutes"));
	}

	public void run() throws InterruptedException {

		log.info("***************************************************");
		log.info("Starting PortfolioWatcher");
		log.info("***************************************************");

		while (true) {
			task.execute();
			sleep();
		}
	}

	private void sleep() throws InterruptedException {
		int milliseconds = minutesBetweenUpdates * 60 * 1000;
		log.debug("Going to sleep for " + milliseconds + "ms");
		Thread.sleep(milliseconds);
	}
}
