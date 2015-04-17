package com.jwm.stockwatch.service;

import java.util.Properties;

import jwm.emailxmlgenerator.main.XmlEmailGenerator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jwm.stockwatch.PropertiesLoaderImpl;

public class NotifierServiceEmailImpl implements NotifyService {

	String mailboxDirectory, recipientEmail;
	private static Logger log = LogManager.getLogger(NotifierServiceEmailImpl.class);

	public NotifierServiceEmailImpl(PropertiesLoaderImpl propLoader) {
		Properties props = propLoader.getProperties(); 
		this.mailboxDirectory = props.getProperty("emailbox_directory");
		this.recipientEmail = props.getProperty("dest_email");
	}

	@SuppressWarnings("unused")
	@Override
	public void sendNotification(String subject, String message) {

		Exception exOut = null;
		StringBuilder messageSb = new StringBuilder(message);
		log.debug("Sending email.  Subject:'" + subject + "', Body:'" + message
				+ "'");
		XmlEmailGenerator.createEmailXml(mailboxDirectory, recipientEmail,
				subject, messageSb, false, exOut);
		if (exOut != null) {
			log.error(exOut);
		}

	}

}
