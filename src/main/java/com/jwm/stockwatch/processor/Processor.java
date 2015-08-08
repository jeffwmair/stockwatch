package com.jwm.stockwatch.processor;

import com.jwm.stockwatch.domain.UnitPrice;
import com.jwm.stockwatch.service.UnitPriceService;

/**
 * Some kind of processor that processes prices
 * @author Jeff
 *
 */
public interface Processor {
	void process(UnitPrice price, UnitPriceService service);
}
