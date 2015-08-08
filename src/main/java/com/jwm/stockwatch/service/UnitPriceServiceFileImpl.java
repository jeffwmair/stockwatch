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

import com.jwm.stockwatch.fetcher.WebFetcher;
import com.jwm.stockwatch.processor.Processor;
import com.jwm.stockwatch.PropertiesLoader;
import com.jwm.stockwatch.domain.SentNotification;
import com.jwm.stockwatch.domain.UnitPrice;
import com.jwm.stockwatch.domain.UnitPriceCollection;

import static com.googlecode.charts4j.Color.*;
import static com.googlecode.charts4j.UrlUtil.normalize;
import com.googlecode.charts4j.*;

import org.jsoup.Jsoup;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;

import org.apache.commons.codec.binary.Base64;

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
	private WebFetcher fetcher;
	private Processor processor;


	public UnitPriceServiceFileImpl(Processor processor, WebFetcher fetcher, PropertiesLoader propsLoader) {

		this.fetcher = fetcher;
		this.processor = processor;

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
			log.debug("Checked if we already sent a notification for price: " + price.toString() + "; already sent=" + Boolean.toString(sent));
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
	public void savePrice() {

		UnitPrice price = fetcher.fetchPortfolioPrice();
		if (price == null) {
			log.error("Price object was returned as null.  Sleeping until the next fetch");
			return;
		}

		UnitPriceCollection prices = getSavedPrices();
		if (!prices.containsPrice(price)) {
			prices.addPrice(price);
			save(prices, DataFileName);

			/* DEBUG */
			if (log.isDebugEnabled()) {
				log.debug("Printing all prices:");
				for (UnitPrice p : prices.getPrices()) {
					log.debug(p);
				}
			}
		}

		processor.process(price, this);
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

	@Override
	public double getNetChangeOverLastN(int nDays) {
		double netChange = 0;
		List<UnitPrice> nPrices = getLastNPrices(nDays);
		for (int i = 0; i < nPrices.size(); i++) {
			netChange += nPrices.get(i).getChangeInPrice();
		}

		return netChange;
	}

	private List<UnitPrice> getLastNPrices(int nDays) {
		List<UnitPrice> prices = new ArrayList<UnitPrice>(getPricesFromFile().getPrices());
		Collections.reverse(prices);
		int top = prices.size() < nDays ? prices.size() : nDays;
		List<UnitPrice> nPrices = new ArrayList<UnitPrice>();
		for (int i = 0; i < top; i++) {
			nPrices.add(prices.get(i));
		}
		return nPrices;
	}

	@Override
	public String getRecentPriceChartUrl(int nDays) {

		/* 
		 * this all needs to be cleaned up
		 */

		List<UnitPrice> prices = getLastNPrices(nDays);
		Collections.reverse(prices);

		List<Double> dataset = new ArrayList<Double>();
		List<String> dates = new ArrayList<String>();

		Double maxVal = 15.0;
		Double minVal = 12.0;

		for (int i = 0; i < prices.size(); i++) {
			UnitPrice p = prices.get(i);
			Double scaledVal = 100 * ((p.getCurrentPrice()-minVal)/(maxVal-minVal));
			dataset.add(scaledVal);
			dates.add(p.getDate().toString());
		}

		String datasetname = "Mutual Fund Price";
		Line dataLine = Plots.newLine(Data.newData(dataset), Color.newColor("CA3D05"),datasetname);
		dataLine.setLineStyle(LineStyle.newLineStyle(3, 1, 0));
		dataLine.addShapeMarkers(Shape.DIAMOND, Color.newColor("CA3D05"), 12);
		dataLine.addShapeMarkers(Shape.DIAMOND, Color.WHITE, 8);

		LineChart chart = GCharts.newLineChart(dataLine);
		chart.setSize(600, 450);
		chart.setTitle("Price Chart", WHITE, 14);
		chart.setGrid(25, 25, 3, 2);

		AxisStyle axisStyle = AxisStyle.newAxisStyle(WHITE, 12, AxisTextAlignment.CENTER);
		AxisLabels yAxis = AxisLabelsFactory.newAxisLabels("12.0", "13.5", "15.0");
		//AxisLabels xAxis3 = AxisLabelsFactory.newAxisLabels("Date", 10.0);
		//xAxis3.setAxisStyle(AxisStyle.newAxisStyle(WHITE, 14, AxisTextAlignment.CENTER));
		yAxis.setAxisStyle(axisStyle);
		AxisLabels yAxis2 = AxisLabelsFactory.newAxisLabels("Price", 50.0);
		yAxis2.setAxisStyle(AxisStyle.newAxisStyle(WHITE, 14, AxisTextAlignment.CENTER));
		yAxis2.setAxisStyle(axisStyle);

		//chart.addXAxisLabels(xAxis3);
		chart.addYAxisLabels(yAxis);
		chart.addYAxisLabels(yAxis2);
		chart.setBackgroundFill(Fills.newSolidFill(Color.newColor("1F1D1D")));
		LinearGradientFill fill = Fills.newLinearGradientFill(0, Color.newColor("363433"), 100);
		fill.addColorAndOffset(Color.newColor("2E2B2A"), 0);
		chart.setAreaFill(fill);

		String url = chart.toURLString();
		if (log.isDebugEnabled()) {
			log.debug("Generated chart url:" + url);
		}
		return url;
	}

	@Override
	public String getRecentPriceChartBase64Data(int nDays) {

		String url = getRecentPriceChartUrl(nDays);
		Response chartImageResponse = null;
		try {
			chartImageResponse = Jsoup.connect(url).ignoreContentType(true).execute();
		}
		catch (IOException ex) {
			log.error(ex);				
			return "";
		}
		byte[] imageBytes = chartImageResponse.bodyAsBytes();
		String imageBase64 = new String(Base64.encodeBase64(imageBytes));
		if (log.isDebugEnabled()) {
			log.debug("Generated chart image data base64:" + imageBase64);
		}
		return imageBase64;
	}

}
