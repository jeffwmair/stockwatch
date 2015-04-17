package com.jwm.stockwatch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

/**
 * Hello world!
 *
 */
public class Main {
	private static Logger log = LogManager.getLogger(Main.class);

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		ApplicationContext springCtx = new GenericXmlApplicationContext("beans.xml");
		App app = (App) springCtx.getBean("app");
		try {
			app.run();
		} catch (Exception ex) {
			log.error(ex);
		}
	}
}
