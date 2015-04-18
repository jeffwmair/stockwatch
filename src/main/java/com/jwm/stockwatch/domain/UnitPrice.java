package com.jwm.stockwatch.domain;

import java.util.Date;

/**
 * UnitPrice - price of a portfolio unit at a particular moment in
 * time.
 * 
 * @author Jeff
 *
 */
public class UnitPrice {
	private double currentPrice, changeInPrice, changeInPercent;
	private String name;
	private Date date;

	public UnitPrice(String name, Date date, double currentPrice,
			double changeInPrice, double changeInPercent) {
		super();
		this.currentPrice = currentPrice;
		this.changeInPrice = changeInPrice;
		this.changeInPercent = changeInPercent;
		this.date = date;
		this.name = name;
	}

	public double getCurrentPrice() {
		return currentPrice;
	}

	public double getChangeInPrice() {
		return changeInPrice;
	}

	public double getAbsChangeInPrice() {
		return Math.abs(changeInPrice);
	}

	public double getChangeInPercent() {
		return changeInPercent;
	}

	public double getAbsChangeInPercent() {
		return Math.abs(changeInPercent);
	}

	public String getName() {
		return name;
	}

	public Date getDate() {
		return date;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UnitPrice other = (UnitPrice) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Portfolio: '" + name + "', Date: " + date.toString()
				+ ", Price: $" + currentPrice + ", Change: $" + changeInPrice
				+ ", Change Pct: " + changeInPercent + "%";
	}

}
