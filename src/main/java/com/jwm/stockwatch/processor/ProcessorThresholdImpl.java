package com.jwm.stockwatch.processor;

import java.text.NumberFormat;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.jwm.stockwatch.domain.UnitPrice;
import com.jwm.stockwatch.notifier.Notifier;

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

	public ProcessorThresholdImpl(Notifier notifier, double threshold) {
		this.notifier = notifier;
		this.priceChangeThreshold = threshold;
	}

	@Override
	public boolean process(UnitPrice price, double last3DaysChange, double last10DaysChange, double last15DaysChange, double last20DaysChange, String chartUrl) {

		if (price.getAbsChangeInPrice() > priceChangeThreshold) {
			try {
				NumberFormat formatter = NumberFormat.getCurrencyInstance();
				String message = price.toString()
					+ "<br><br><strong>Last 3 days change: " + formatter.format(last3DaysChange) + "</strong>"
					+ "<br><br><strong>Last 10 days change: " + formatter.format(last10DaysChange) + "</strong>"
					+ "<br><br><strong>Last 15 days change: " + formatter.format(last15DaysChange) + "</strong>"
					+ "<br><br><strong>Last 20 days change: " + formatter.format(last20DaysChange) + "</strong>"
					+ "<br><br>"
					+ "<a href='"+chartUrl+"'>Chart</a>";

				notifier.sendNotification("Portfolio Price Update", message);
				return true;
			} catch (Exception e) {
				log.error("Failed to send email: " + e.getMessage(), e);
				return false;
			}
		} else {
			log.info("Latest price change is less than the threshold of:" + priceChangeThreshold);
			return false;
		}
	}

}
