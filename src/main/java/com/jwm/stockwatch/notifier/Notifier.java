package com.jwm.stockwatch.notifier;

public interface Notifier {
	void sendNotification(String subject, String message) throws Exception;
}
