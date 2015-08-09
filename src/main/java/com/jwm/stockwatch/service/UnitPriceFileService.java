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

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.jwm.stockwatch.domain.SentNotification;
import com.jwm.stockwatch.domain.UnitPrice;
import com.jwm.stockwatch.domain.UnitPriceCollection;

import org.jsoup.Jsoup;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;

/**
 * Service for persisting price data to the disk. Quick and simple object
 * serialization.
 * 
 * @author Jeff
 *
 */
public class UnitPriceFileService {

	private static Logger log = LogManager.getLogger(UnitPriceFileService.class);
	private final String DataDirectory = "data";
	private final String DataFileName = DataDirectory + "/Prices.dat";
	private final String DataFileNameNotifications = DataDirectory + "/Notifications.dat";

	public UnitPriceFileService() {

		File dir = new File(DataDirectory);
		/* create the data dir if not already there */
		if (!dir.exists()) {
			dir.mkdir();
		}

		/* write an empty data file if not already there. */
		File datafile = new File(DataFileName);
		if (!datafile.exists()) {
			save(new UnitPriceCollection(), DataFileName);
		}

		File notificationsFile = new File(DataFileNameNotifications);
		if (!notificationsFile.exists()) {
			saveNotification(new ArrayList<SentNotification>());
		}
	}

	public boolean hasSentNotificationForPrice(UnitPrice price) {
		List<SentNotification> notifications = getSentNotifications();
		boolean sent = false;
		for (SentNotification sentNotification : notifications) {
			if (sentNotification.getDate().equals(price.getDate())) {
				sent = true;
			}
		}

		if (log.isDebugEnabled()) {
			log.debug("Checked if we already sent a notification for price: " + price.toString() + "; already sent=" + Boolean.toString(sent));
		}

		return sent;
	}

	public void saveSentPriceNotification(UnitPrice price) {
		List<SentNotification> notifications = getSentNotifications();
		notifications.add(new SentNotification(price));
		saveNotification(notifications);
	}

	public void saveUnitPriceCollection(UnitPriceCollection prices) {
		save(prices, DataFileName);
	}

	/**
	 * Get all unit prices already saved to the disk
	 */
	public UnitPriceCollection getSavedPrices() {
		return getPricesFromFile();
	}
	private List<SentNotification> getSentNotifications() {
		return load(DataFileNameNotifications);
	}

	/**
	 * Save some object to the disk
	 */
	private void save(Object objToSave, String filename) {
		try {
			FileOutputStream fileOut = new FileOutputStream(filename);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(objToSave);
			out.close();
			fileOut.close();
		} catch (IOException i) {
			log.error(i);
		}
	}

	private <T> T load(String filename) {
		T retval = null;
		try {
			FileInputStream fileIn = new FileInputStream(filename);
			ObjectInputStream in = null;
			in = new ObjectInputStream(fileIn);
			retval = (T) in.readObject();
			in.close();
			fileIn.close();
			return retval;
		} catch (Exception e) {
			throw new RuntimeException("Unable to load file with name '"+filename+"'.  Exception Message:" + e.getMessage());
		}
	}

	private void saveNotification(List<SentNotification> notifications) {
		save(notifications, DataFileNameNotifications);
	}

	private UnitPriceCollection getPricesFromFile() {
		return load(DataFileName);
	}

	public double getNetChangeOverLastN(int nDays) {
		double netChange = 0;
		List<UnitPrice> nPrices = getLastNPrices(nDays);
		for (int i = 0; i < nPrices.size(); i++) {
			netChange += nPrices.get(i).getChangeInPrice();
		}

		return netChange;
	}

	public List<UnitPrice> getLastNPrices(int nDays) {
		List<UnitPrice> prices = new ArrayList<UnitPrice>(getPricesFromFile().getPrices());
		Collections.reverse(prices);
		int top = prices.size() < nDays ? prices.size() : nDays;
		List<UnitPrice> nPrices = new ArrayList<UnitPrice>();
		for (int i = 0; i < top; i++) {
			nPrices.add(prices.get(i));
		}
		return nPrices;
	}

}
