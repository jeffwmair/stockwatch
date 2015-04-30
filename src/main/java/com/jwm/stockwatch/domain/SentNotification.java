package com.jwm.stockwatch.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents a sent notification
 * @author Jeff
 *
 */
public class SentNotification implements Serializable {
	private static final long serialVersionUID = 5080042467107785483L;
	Date date;
	
	public SentNotification() {
		
	}
	
	public SentNotification(UnitPrice p) {
		this.date = p.getDate();
	}

	public void setDate(Date d) {
		this.date = d;
	}

	public Date getDate() {
		return date;
	}
}
