package com.jwm.stockwatch.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Collection of UnitPrice objects
 * @author Jeff
 *
 */
public class UnitPriceCollection implements Serializable {

	private static final long serialVersionUID = -7054105633853335756L;
	private Collection<UnitPrice> prices;
	private UnitPrice latestPrice = null;

	public UnitPriceCollection() {
		this.prices = new ArrayList<UnitPrice>();
	}

	/**
	 * Return the unit price that has the most recent date
	 * @return
	 */
	public UnitPrice getLatestPrice() {
		return latestPrice;
	}

	/**
	 * Add a new unit price to the collection
	 * @param price
	 */
	public void addPrice(UnitPrice price) {
		prices.add(price);
		if (latestPrice == null || price.getDate().after(latestPrice.getDate())) {
			latestPrice = price;
		}
	}

	/**
	 * Get all unit prices
	 * @return
	 */
	public Collection<UnitPrice> getPrices() {
		return prices;
	}

	/** 
	 * Does this collection contain the given price?
	 * @param price
	 * @return
	 */
	public boolean containsPrice(UnitPrice price) {
		return prices.contains(price);
	}

}
