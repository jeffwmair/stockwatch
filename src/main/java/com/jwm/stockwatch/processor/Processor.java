package com.jwm.stockwatch.processor;

import com.jwm.stockwatch.domain.UnitPrice;

public interface Processor {
	void process(UnitPrice price, UnitPrice lastPrice);
}
