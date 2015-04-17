package com.jwm.stockwatch.processor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jwm.stockwatch.domain.PortfolioUnitPrice;
import com.jwm.stockwatch.service.NotifyService;

public class ProcessorImpl implements Processor {

	private static Logger log = LogManager.getLogger(ProcessorImpl.class);
	private double priceChangeThreshold;
	private NotifyService notifier;

	public ProcessorImpl(NotifyService notifier, double threshold) {
		this.notifier = notifier;
		this.priceChangeThreshold = threshold;
	}

	@Override
	public void process(PortfolioUnitPrice price, PortfolioUnitPrice lastPrice) {

		if (price.equals(lastPrice)) {
			log.debug("Current portfolio price matches previous one, so no notification will be sent.  " + price.toString());
			return;
		}

		if (price.getAbsChangeInPrice() > priceChangeThreshold) {
			notifier.sendNotification("Portfolio Price Update", price.toString());
		}
	}

}
