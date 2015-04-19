package com.jwm.stockwatch;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jwm.stockwatch.domain.UnitPrice;
import com.jwm.stockwatch.fetcher.WebFetcher;
import com.jwm.stockwatch.processor.Processor;
import com.jwm.stockwatch.service.UnitPriceService;

public class App {

	private static Logger log = LogManager.getLogger(App.class);

	private WebFetcher fetcher;
	private Processor processor;
	private UnitPriceService service;
	private int minutesBetweenUpdates;

	public App(PropertiesLoaderImpl props, WebFetcher fetcher, Processor processor, UnitPriceService fileService) throws IOException {
		this.fetcher = fetcher;
		this.processor = processor;
		this.service = fileService;
		this.minutesBetweenUpdates = Integer.parseInt(props.getProperties().getProperty("sleeptime_minutes"));
	}

	public void run() throws InterruptedException {

		log.info("***************************************************");
		log.info("Starting PortfolioWatcher");
		log.info("***************************************************");

		UnitPrice lastEmailedPrice = service.getSavedPrices().getLatestPrice();
		
		while (true) {

			UnitPrice price = fetcher.fetchPortfolioPrice();
			service.savePrice(price);

			if (price == null) {
				log.error("Price object was returned as null.  Sleeping until the next fetch");
				sleep();
				continue;
			}

			if (lastEmailedPrice == null) {
				log.info("LastEmail is null, so we are using this current price as the starting point.");
				lastEmailedPrice = price;
				sleep();
				continue;
			}

			processor.process(price, lastEmailedPrice);
			lastEmailedPrice = price;
			sleep();
		}
	}

	private void sleep() throws InterruptedException {
		int milliseconds = minutesBetweenUpdates * 60 * 1000;
		log.debug("Going to sleep for " + milliseconds + "ms");
		Thread.sleep(milliseconds);
	}
}
