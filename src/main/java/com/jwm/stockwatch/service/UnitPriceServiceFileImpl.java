package com.jwm.stockwatch.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jwm.stockwatch.PropertiesLoader;
import com.jwm.stockwatch.domain.SentNotification;
import com.jwm.stockwatch.domain.UnitPrice;
import com.jwm.stockwatch.domain.UnitPriceCollection;

/**
 * Service for persisting price data to the disk. Quick and simple object
 * serialization.
 * 
 * @author Jeff
 *
 */
public class UnitPriceServiceFileImpl implements UnitPriceService {

	private static Logger log = LogManager.getLogger(UnitPriceServiceFileImpl.class);
	private final String DataDirectory = "data";
	private final String DataFileName = DataDirectory + "/Prices.dat";
	private final String DataFileNameNotifications = DataDirectory + "/Notifications.dat";

	public UnitPriceServiceFileImpl(PropertiesLoader propsLoader) {

		File dir = new File(DataDirectory);
		/* create the data dir if not already there */
		if (!dir.exists()) {
			dir.mkdir();
		}

		/* write an empty data file if not already there. */
		File datafile = new File(DataFileName);
		if (!datafile.exists()) {
			savePricesToFile(new UnitPriceCollection());
		}

		File notificationsFile = new File(DataFileNameNotifications);
		if (!notificationsFile.exists()) {
			saveNotification(new ArrayList<SentNotification>());
		}
	}

	@Override
	public boolean hasSentNotificationForPrice(UnitPrice price) {
		List<SentNotification> notifications = getSentNotifications();
		boolean sent = false;
		for (SentNotification sentNotification : notifications) {
			if (sentNotification.getDate().equals(price.getDate())) {
				sent = true;
			}
		}

		if (log.isDebugEnabled()) {
			log.debug("Checked if we already sent a notification for price: " + price.toString() + "; already sent="
					+ Boolean.toString(sent));
		}
		return sent;
	}

	@Override
	public void saveSentPriceNotification(UnitPrice price) {
		List<SentNotification> notifications = getSentNotifications();
		notifications.add(new SentNotification(price));
		saveNotification(notifications);
	}

	/**
	 * Get all unit prices already saved to the disk
	 */
	@Override
	public UnitPriceCollection getSavedPrices() {
		return getPricesFromFile();
	}

	/**
	 * Save the unit price to the disk
	 */
	@Override
	public void savePrice(UnitPrice price) {
		UnitPriceCollection prices = getSavedPrices();
		if (!prices.containsPrice(price)) {
			prices.addPrice(price);
			savePricesToFile(prices);

			/* DEBUG */
			if (log.isDebugEnabled()) {
				log.debug("Printing all prices:");
				for (UnitPrice p : prices.getPrices()) {
					log.debug(p);
				}
			}
		}
	}

	private List<SentNotification> getSentNotifications() {
		List<SentNotification> notifications = null;
		try {
			FileInputStream fileIn = new FileInputStream(DataFileNameNotifications);
			ObjectInputStream in = null;
			in = new ObjectInputStream(fileIn);
			notifications = (List<SentNotification>) in.readObject();
			in.close();
			fileIn.close();
			return notifications;
		} catch (Exception e) {
			log.error(e);
			return null;
		}
	}

	private void saveNotification(List<SentNotification> notifications) {
		try {
			FileOutputStream fileOut = new FileOutputStream(DataFileNameNotifications);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(notifications);
			out.close();
			fileOut.close();
		} catch (IOException i) {
			log.error(i);
		}
	}

	private UnitPriceCollection getPricesFromFile() {
		UnitPriceCollection prices = null;
		try {
			FileInputStream fileIn = new FileInputStream(DataFileName);
			ObjectInputStream in = null;
			in = new ObjectInputStream(fileIn);
			prices = (UnitPriceCollection) in.readObject();
			in.close();
			fileIn.close();
			return prices;
		} catch (Exception e) {
			log.error(e);
			return null;
		}
	}

	private void savePricesToFile(UnitPriceCollection prices) {
		try {
			FileOutputStream fileOut = new FileOutputStream(DataFileName);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(prices);
			out.close();
			fileOut.close();
		} catch (IOException i) {
			log.error(i);
		}
	}

	@Override
	public double getNetChangeOverLast10() {
		int n = 10;
		List<UnitPrice> prices = new ArrayList<UnitPrice>(getPricesFromFile().getPrices());
		Collections.reverse(prices);
		int top = prices.size() < n ? prices.size() : n;
		double netChange = 0;
		for (int i = 0; i < top; i++) {
			netChange += prices.get(i).getChangeInPrice();
		}
		return netChange;
	}

}
