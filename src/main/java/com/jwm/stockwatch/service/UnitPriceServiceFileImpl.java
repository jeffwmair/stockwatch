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

			List<UnitPrice> prices = getLastNPrices(nDays);

			List<Double> dataset = new ArrayList<Double>();
			List<String> dates = new ArrayList<String>();

			for (int i = 0; i < prices.size(); i++) {
				UnitPrice p = prices.get(i);
				dataset.add(p.getCurrentPrice());
				dates.add(p.getDate().toString());
			}

			String datasetname = "Mutual Fund Price";
			Line dataLine = Plots.newLine(Data.newData(dataset), Color.newColor("CA3D05"),datasetname);
			dataLine.setLineStyle(LineStyle.newLineStyle(3, 1, 0));
			dataLine.addShapeMarkers(Shape.DIAMOND, Color.newColor("CA3D05"), 12);
			dataLine.addShapeMarkers(Shape.DIAMOND, Color.WHITE, 8);

			LineChart chart = GCharts.newLineChart(dataLine);
			chart.setSize(600, 450);
			chart.setTitle("Jeff's Chart", WHITE, 14);
			chart.setGrid(25, 25, 3, 2);

			AxisStyle axisStyle = AxisStyle.newAxisStyle(WHITE, 12, AxisTextAlignment.CENTER);
			AxisLabels yAxis = AxisLabelsFactory.newAxisLabels("", "25", "50", "75", "100");
			AxisLabels xAxis3 = AxisLabelsFactory.newAxisLabels("Date", 50.0);
			xAxis3.setAxisStyle(AxisStyle.newAxisStyle(WHITE, 14, AxisTextAlignment.CENTER));
			yAxis.setAxisStyle(axisStyle);
			AxisLabels yAxis2 = AxisLabelsFactory.newAxisLabels("Price", 50.0);
			yAxis2.setAxisStyle(AxisStyle.newAxisStyle(WHITE, 14, AxisTextAlignment.CENTER));
			yAxis2.setAxisStyle(axisStyle);

			chart.addXAxisLabels(xAxis3);
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
