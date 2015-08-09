package com.jwm.stockwatch.service;

import org.springframework.util.*;
import com.jwm.stockwatch.domain.*;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

import static com.googlecode.charts4j.Color.*;
import static com.googlecode.charts4j.UrlUtil.normalize;
import com.googlecode.charts4j.*;

/**
 * ChartService generates a chart url for a google chart
 */
public class ChartService {

	private static Logger log = LogManager.getLogger(ChartService.class);
	private UnitPriceFileService priceService;
	public ChartService(UnitPriceFileService priceService) {
		Assert.notNull(priceService);
		this.priceService = priceService;
	}	

	/**
	 * Generate a url for a google chart
	 */
	public String generateChartUrl(int nDays) {

		List<UnitPrice> prices = priceService.getLastNPrices(nDays);
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

		// would be ideal to generalize this and not have this volatile dependency here.  Maybe later..
		LineChart chart = GCharts.newLineChart(dataLine);
		chart.setSize(600, 450);
		chart.setTitle("Price Chart", WHITE, 14);
		chart.setGrid(25, 25, 3, 2);

		AxisStyle axisStyle = AxisStyle.newAxisStyle(WHITE, 12, AxisTextAlignment.CENTER);
		AxisLabels yAxis = AxisLabelsFactory.newAxisLabels("12.0", "13.5", "15.0");
		yAxis.setAxisStyle(axisStyle);
		AxisLabels yAxis2 = AxisLabelsFactory.newAxisLabels("Price", 50.0);
		yAxis2.setAxisStyle(AxisStyle.newAxisStyle(WHITE, 14, AxisTextAlignment.CENTER));
		yAxis2.setAxisStyle(axisStyle);

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
}
