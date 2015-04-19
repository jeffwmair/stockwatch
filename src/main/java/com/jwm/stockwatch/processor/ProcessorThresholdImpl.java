package com.jwm.stockwatch.processor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jwm.stockwatch.domain.UnitPrice;
import com.jwm.stockwatch.notifier.Notifier;

/**
 * Processor implementation:  sends notifications if the price has changed and the absolute change amount is greater than some threshold
 * @author Jeff
 *
 */
public class ProcessorThresholdImpl implements Processor {

	private static Logger log = LogManager.getLogger(ProcessorThresholdImpl.class);
	private double priceChangeThreshold;
	private Notifier notifier;

	public ProcessorThresholdImpl(Notifier notifier, double threshold) {
		this.notifier = notifier;
		this.priceChangeThreshold = threshold;
	}

	@Override
	public void process(UnitPrice price, UnitPrice lastPrice) {

		if (price.equals(lastPrice)) {
			log.debug("Current portfolio price matches previous one, so no notification will be sent.  " + price.toString());
			return;
		}

		if (price.getAbsChangeInPrice() > priceChangeThreshold) {
			try {
				notifier.sendNotification("Portfolio Price Update", price.toString());
			} catch (Exception e) {
				log.error("Failed to send email: " + e.getMessage(), e);
			}
		}
	}

}
