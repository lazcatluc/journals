package com.crossover.trial.journals.mail;

public interface EmailProvider {
	void sendMessageTo(String email, String subject, String message);
}
