package com.jwm.stockwatch.processor;

import com.jwm.stockwatch.domain.UnitPrice;
import com.jwm.stockwatch.service.UnitPriceFileService;

/**
 * Some kind of processor that processes prices
 * @author Jeff
 *
 */
public interface Processor {
	boolean process(UnitPrice price, double last3DaysChange, double last10DaysChange, double last15DaysChange, double last20DaysChange, String chartUrl);
}
