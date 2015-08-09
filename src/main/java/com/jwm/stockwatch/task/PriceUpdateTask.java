package com.jwm.stockwatch.task;

import com.jwm.stockwatch.processor.*;
import com.jwm.stockwatch.service.*;
import com.jwm.stockwatch.domain.*;
import com.jwm.stockwatch.fetcher.*;

import org.springframework.util.*;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class PriceUpdateTask {

	private WebFetcher fetcher;
	private UnitPriceFileService priceService;
	private Processor processor;
	private ChartService chartService;
	private static Logger log = LogManager.getLogger(ProcessorThresholdImpl.class);

	public PriceUpdateTask(WebFetcher fetcher, UnitPriceFileService priceService, Processor processor, ChartService chartService) {
		Assert.notNull(fetcher);
		Assert.notNull(priceService);
		Assert.notNull(processor);
		Assert.notNull(chartService);
		this.fetcher = fetcher;
		this.priceService = priceService;
		this.processor = processor;
		this.chartService = chartService;
	}


	/**
	 * Run this task
	 */
	public void execute() {
		UnitPrice price = fetcher.fetchPortfolioPrice();
		if (price == null) {
			log.error("Price object was returned as null.  Sleeping until the next fetch");
			return;
		}

		UnitPriceCollection prices = priceService.getSavedPrices();
		if (!prices.containsPrice(price)) {
			prices.addPrice(price);
			priceService.saveUnitPriceCollection(prices);

			if (log.isDebugEnabled()) {
				log.debug("Printing all prices:");
				for (UnitPrice p : prices.getPrices()) {
					log.debug(p);
				}
			}
		}

		if (priceService.hasSentNotificationForPrice(price)) {
			log.info("Already sent notification for price with date: " + price.getDate());
			return;
		}

		// todo: clean up the following, including the processor method signature
		int chartDays = 120;
		String chartUrl = chartService.generateChartUrl(chartDays);
		double last3DaysChange = priceService.getNetChangeOverLastN(3);
		double last10DaysChange = priceService.getNetChangeOverLastN(10);
		double last15DaysChange = priceService.getNetChangeOverLastN(15);
		double last20DaysChange = priceService.getNetChangeOverLastN(20);
		boolean processed = processor.process(price, last3DaysChange, last10DaysChange, last15DaysChange, last20DaysChange, chartUrl);
		if (processed) {
			priceService.saveSentPriceNotification(price);
		}
	}
}
