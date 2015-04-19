package com.jwm.stockwatch.processor;

import com.jwm.stockwatch.domain.UnitPrice;

/**
 * Some kind of processor that processes prices
 * @author Jeff
 *
 */
public interface Processor {
	void process(UnitPrice price, UnitPrice lastPrice);
}
