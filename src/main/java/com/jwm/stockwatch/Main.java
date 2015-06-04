package com.jwm.stockwatch;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import com.jwm.stockwatch.reporting.ReportGenerator;

/**
 * Application entry point.
 *
 */
public class Main {
	
	private static Logger log = LogManager.getLogger(Main.class);

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		ApplicationContext springCtx = new GenericXmlApplicationContext("beans.xml");
		
		if (args.length == 1 && args[0].equals("report")) {
			ReportGenerator report = (ReportGenerator)springCtx.getBean("reportGeneratorConsole");
			log.info("Running a report...");
			report.generateReport();
			return;
		}
		
		App app = (App) springCtx.getBean("app");
		try {
			app.run();
		} catch (Exception ex) {
			log.error(ex, ex);
		}
	}
}
