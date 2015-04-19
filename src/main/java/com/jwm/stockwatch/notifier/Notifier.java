package com.jwm.stockwatch.notifier;

/**
 * A general notifier
 * @author Jeff
 *
 */
public interface Notifier {
	void sendNotification(String subject, String message) throws Exception;
}
