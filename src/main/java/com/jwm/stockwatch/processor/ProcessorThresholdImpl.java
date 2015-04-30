package com.jwm.stockwatch.processor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jwm.stockwatch.domain.UnitPrice;
import com.jwm.stockwatch.notifier.Notifier;
import com.jwm.stockwatch.service.UnitPriceService;

/**
 * Processor implementation: sends notifications if the price has changed and
 * the absolute change amount is greater than some threshold
 * 
 * @author Jeff
 *
 */
public class ProcessorThresholdImpl implements Processor {

	private static Logger log = LogManager.getLogger(ProcessorThresholdImpl.class);
	private double priceChangeThreshold;
	private Notifier notifier;
	private UnitPriceService service;

	public ProcessorThresholdImpl(Notifier notifier, double threshold, UnitPriceService service) {
		this.notifier = notifier;
		this.priceChangeThreshold = threshold;
		this.service = service;
	}

	@Override
	public void process(UnitPrice price) {

		if (service.hasSentNotificationForPrice(price)) {
			log.info("Already sent notification for price with date: " + price.getDate());
			return;
		}

		if (price.getAbsChangeInPrice() > priceChangeThreshold) {
			try {
				notifier.sendNotification("Portfolio Price Update", price.toString());
				service.saveSentPriceNotification(price);
			} catch (Exception e) {
				log.error("Failed to send email: " + e.getMessage(), e);
			}
		} else {
			log.info("Latest price change is less than the threshold of:" + priceChangeThreshold);
		}
	}

}
