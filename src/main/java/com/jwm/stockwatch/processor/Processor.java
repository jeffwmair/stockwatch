package com.jwm.stockwatch.processor;

import com.jwm.stockwatch.domain.PortfolioUnitPrice;

public interface Processor {
	void process(PortfolioUnitPrice price, PortfolioUnitPrice lastPrice);
}
