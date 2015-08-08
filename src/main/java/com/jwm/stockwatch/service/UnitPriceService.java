package com.jwm.stockwatch.service;

import com.jwm.stockwatch.domain.UnitPrice;
import com.jwm.stockwatch.domain.UnitPriceCollection;

public interface UnitPriceService {
	UnitPriceCollection getSavedPrices();
	boolean hasSentNotificationForPrice(UnitPrice price);
	void savePrice();
	void saveSentPriceNotification(UnitPrice price);
	double getNetChangeOverLastN(int nDays);
	String getRecentPriceChartBase64Data(int nDays);
	String getRecentPriceChartUrl(int nDays);
}

