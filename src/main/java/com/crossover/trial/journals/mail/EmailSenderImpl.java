package com.crossover.trial.journals.mail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.crossover.trial.journals.model.Journal;
import com.crossover.trial.journals.model.QJournal;
import com.crossover.trial.journals.model.Subscription;
import com.crossover.trial.journals.repository.JournalRepository;
import com.crossover.trial.journals.repository.SubscriptionRepository;
import com.crossover.trial.journals.repository.UserRepository;
import com.mysema.query.types.Predicate;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;

@Service
public class EmailSenderImpl implements EmailSender {

	private final static Logger LOG = Logger.getLogger(EmailSenderImpl.class);
	
	@Autowired
	private SubscriptionRepository subscriptionRepository;

	@Autowired
	private EmailProvider emailProvider;
	
	@Autowired
	private Configuration configuration;
	
	@Autowired
	private JournalRepository journalRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	private Supplier<Date> currentDateFactory;
	
	public EmailSenderImpl() {
		currentDateFactory = Date::new;
	}
	
	EmailSenderImpl(Supplier<Date> dateFactory) {
		this.currentDateFactory = dateFactory;
	}
	
	Predicate journalsFromLast24Hours() {
		Date date = new Date(currentDateFactory.get().getTime() - TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS));
		return QJournal.journal.publishDate.after(date);
	}
	
	@Override
	@Scheduled(cron="${email.digest.cron.expression}")
	public void sendDailyDigest() {
		LOG.info("Sending daily digest");
		Iterable<Journal> journals = journalRepository.findAll(journalsFromLast24Hours());
		List<Journal> journalList = new ArrayList<>();
		journals.forEach(journalList::add);
		if (journalList.isEmpty()) {
			return;
		}
		userRepository.findAll().forEach(user -> {
			String email = user.getEmail();
			String subjectForDigest = "New Journals Today";
			Map<String, Object> model = new HashMap<>();
			model.put("user", user);
			model.put("journals", journalList);
			try {
				String messageForDigest = FreeMarkerTemplateUtils.processTemplateIntoString(
						configuration.getTemplate("email/journalsDigest.html"), model);
				emailProvider.sendMessageTo(email, subjectForDigest, messageForDigest);
			} catch (IOException | TemplateException e) {
				LOG.error(e);
			}
		});
	}

	@Override
	public void sendImmediateNotificationFor(Journal newJournal) {
		LOG.info("Sending notification for "+newJournal.getName());
		subscriptionRepository.findByCategory(newJournal.getCategory())
			.stream().map(Subscription::getUser).forEach(user -> {
				String email = user.getEmail();
				String subjectForNewJournal = "New Journal: "+newJournal.getName();
				Map<String, Object> model = new HashMap<>();
				model.put("user", user);
				model.put("journal", newJournal);
				try {
					String messageForNewJournal = FreeMarkerTemplateUtils.processTemplateIntoString(
							configuration.getTemplate("email/newJournal.html"), model);
					emailProvider.sendMessageTo(email, subjectForNewJournal, messageForNewJournal);
				} catch (IOException | TemplateException e) {
					LOG.error(e);
				}
		});
	}

}
