package com.jwm.stockwatch.processor;

import java.text.NumberFormat;

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
				double netChange = service.getNetChangeOverLastN(10);
				NumberFormat formatter = NumberFormat.getCurrencyInstance();
				int chartDays = 20;
				//String base64ImgData = service.getRecentPriceChartBase64Data(chartDays);
				String chartUrl = service.getRecentPriceChartUrl(chartDays);
				String message = price.toString()
					+ "<br><br><strong>Last 3 days change: " + formatter.format(service.getNetChangeOverLastN(3)) + "</strong>"
					+ "<br><br><strong>Last 10 days change: " + formatter.format(service.getNetChangeOverLastN(10)) + "</strong>"
					+ "<br><br><strong>Last 15 days change: " + formatter.format(service.getNetChangeOverLastN(15)) + "</strong>"
					+ "<br><br><strong>Last 20 days change: " + formatter.format(service.getNetChangeOverLastN(20)) + "</strong>"
					+ "<br><br>"
					+ "<a href='"+chartUrl+"'>Chart</a>";

				notifier.sendNotification("Portfolio Price Update", message);
				service.saveSentPriceNotification(price);
			} catch (Exception e) {
				log.error("Failed to send email: " + e.getMessage(), e);
			}
		} else {
			log.info("Latest price change is less than the threshold of:" + priceChangeThreshold);
		}
	}

}
