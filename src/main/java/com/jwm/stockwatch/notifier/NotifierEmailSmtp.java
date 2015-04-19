package com.jwm.stockwatch.notifier;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.jwm.stockwatch.PropertiesLoader;

/**
 * Notifier of the email variety
 * @author Jeff
 *
 */
public class NotifierEmailSmtp implements Notifier {

	private String recipient, host, fromAddress, fromPass;

	public NotifierEmailSmtp(PropertiesLoader propsLoader) {
		Properties props = propsLoader.getProperties();
		this.recipient = props.getProperty("email_dest");
		this.host = props.getProperty("email_host");
		this.fromAddress = props.getProperty("email_from_user");
		this.fromPass = props.getProperty("email_from_password");
		this.port = Integer.parseInt(props.getProperty("email_port"));
	}

	private int port;

	@Override
	public void sendNotification(String subject, String messageBody) throws Exception {
		Properties props = System.getProperties();
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.user", fromAddress);
		props.put("mail.smtp.password", fromPass);
		props.put("mail.smtp.port", port);
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.auth", "true");

		Session session = Session.getDefaultInstance(props, null);
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(fromAddress));
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
		message.setSubject(subject);
		message.setContent(messageBody, "text/html; charset=utf-8");

		Transport transport = session.getTransport("smtp");
		transport.connect(host, fromAddress, fromPass);
		transport.sendMessage(message, message.getAllRecipients());
		transport.close();
	}
}
