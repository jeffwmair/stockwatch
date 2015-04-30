package com.jwm.stockwatch.service;

import com.jwm.stockwatch.domain.UnitPrice;
import com.jwm.stockwatch.domain.UnitPriceCollection;

public interface UnitPriceService {
	UnitPriceCollection getSavedPrices();
	boolean hasSentNotificationForPrice(UnitPrice price);
	void savePrice(UnitPrice price);
	void saveSentPriceNotification(UnitPrice price);
}

