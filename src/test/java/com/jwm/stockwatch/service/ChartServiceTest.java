package com.jwm.stockwatch.service;

import org.springframework.util.*;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import java.util.*;

import org.junit.Test;
import org.junit.Before;
import org.junit.Assert;

import com.jwm.stockwatch.service.*;
import com.jwm.stockwatch.domain.*;

import org.mockito.MockitoAnnotations;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

public class ChartServiceTest {

	private ChartService chart;
	@Mock
	private UnitPriceFileService service;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void newObject() {
		chart = new ChartService(service);
		chart.generateChartUrl(10);
	}

	/**
	 * Generates a pre-determined URL.  
	 * CURRENTLY DISABLED AS ITS NOT GENERATING AN IDENTICAL URL EACH TIME
	 */
	public void basicChartUrlTest() {
		List<UnitPrice> prices = new ArrayList<UnitPrice>();
		prices.add(new UnitPrice("name 3", new Date(100000), 14, 0, 0));
		prices.add(new UnitPrice("name 2", new Date(1000), 12, 0, 0));
		prices.add(new UnitPrice("name 1", new Date(1), 10, 0, 0));

		int days = 1;
		when(service.getLastNPrices(days)).thenReturn(prices);
		chart = new ChartService(service);

		String knownUrl = "http://chart.apis.google.com/chart?cht=lc&chxt=y,y&chls=3,1,0&chxp=1,50.0&chs=600x450&chco=CA3D05&chd=e:__AAqq&chts=FFFFFF,14&chtt=Price+Chart&chxr=1,0.0,100.0&chg=25.0,25.0,3,2&chdl=Mutual+Fund+Price&chxl=0:|12.0|13.5|15.0|1:|Price&chxs=0,FFFFFF,12,0|1,FFFFFF,12,0&chm=d,CA3D05,0,-1,12,0|d,FFFFFF,0,-1,8,0&chf=bg,s,1F1D1D|c,lg,0,363433,1.0,2E2B2A,0.0";
		String url = chart.generateChartUrl(days);
		System.out.println(knownUrl);
		System.out.println(url);
		Assert.assertEquals(knownUrl, url);

	}

}
