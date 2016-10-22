package com.crossover.trial.journals.mail;

import com.crossover.trial.journals.model.Journal;

public interface EmailSender {
	
	void sendDailyDigest();
	
	void sendImmediateNotificationFor(Journal newJournal);
}
