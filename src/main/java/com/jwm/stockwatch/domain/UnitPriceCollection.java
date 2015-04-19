package com.jwm.stockwatch.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class UnitPriceCollection implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7054105633853335756L;

	private Collection<UnitPrice> prices;

	private UnitPrice latestPrice = null;

	public UnitPrice getLatestPrice() {
		return latestPrice;
	}

	public UnitPriceCollection() {
		this.prices = new ArrayList<UnitPrice>();
	}

	public void addPrice(UnitPrice price) {
		prices.add(price);
		if (latestPrice == null || price.getDate().after(latestPrice.getDate())) {
			latestPrice = price;
		}
	}

	public Collection<UnitPrice> getPrices() {
		return prices;
	}

	public boolean containsPrice(UnitPrice price) {
		return prices.contains(price);
	}

}
