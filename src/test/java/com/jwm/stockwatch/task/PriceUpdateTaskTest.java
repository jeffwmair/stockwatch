package com.jwm.stockwatch.task;

import org.junit.Test;
import org.junit.Before;
import org.junit.Assert;

import com.jwm.stockwatch.fetcher.*;
import com.jwm.stockwatch.service.*;
import com.jwm.stockwatch.processor.*;
import com.jwm.stockwatch.domain.*;

import org.mockito.MockitoAnnotations;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

public class PriceUpdateTaskTest {

	PriceUpdateTask task;

	@Mock 
	UnitPriceFileService priceService;
	@Mock
	Processor processor;
	@Mock
	WebFetcher fetcher;
	@Mock
	ChartService chartService;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void executeAllSteps() {
		UnitPrice price = mock(UnitPrice.class);
		UnitPriceCollection priceCollection = new UnitPriceCollection();
		when(fetcher.fetchPortfolioPrice()).thenReturn(price);
		when(priceService.getSavedPrices()).thenReturn(priceCollection);
		task = new PriceUpdateTask(fetcher, priceService, processor, chartService);
		task.execute();
		verify(fetcher).fetchPortfolioPrice();
		verify(priceService).getSavedPrices();
		verify(priceService).saveUnitPriceCollection(priceCollection);
		verify(priceService).hasSentNotificationForPrice(price);
		verify(chartService).generateChartUrl(120);
		// tool lazy to mock the rest of this stuff
		verify(processor).process(price, 0, 0, 0, 0, null);

	}
}
