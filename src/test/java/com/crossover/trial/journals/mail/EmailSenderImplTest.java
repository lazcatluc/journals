package com.crossover.trial.journals.mail;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import com.crossover.trial.journals.model.Category;
import com.crossover.trial.journals.model.Journal;
import com.crossover.trial.journals.model.Subscription;
import com.crossover.trial.journals.model.User;
import com.crossover.trial.journals.repository.JournalRepository;
import com.crossover.trial.journals.repository.SubscriptionRepository;
import com.crossover.trial.journals.repository.UserRepository;
import com.mysema.query.types.Predicate;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class EmailSenderImplTest {

	@InjectMocks
	private EmailSenderImpl emailSenderImpl;
	
	@Mock
	private SubscriptionRepository subscriptionRepository;
	
	@Mock
	private EmailProvider emailProvider;
	
	@Mock
	private Journal journal;
	
	@Mock
	private Subscription subscription;
	
	@Mock
	private User user;
	
	@Mock
	private Configuration configuration;
	
	@Mock
	private JournalRepository journalRepository;
	
	@Mock
	private UserRepository userRepository;
	
	private Date date= new Date();
	
	@Before
	public void setUp() throws Exception {
		emailSenderImpl = new EmailSenderImpl(() -> date);
		MockitoAnnotations.initMocks(this);
		when(userRepository.findAll()).thenReturn(Arrays.asList(user));
		when(subscriptionRepository.findByCategory(any(Category.class))).thenReturn(Arrays.asList(subscription));
		when(subscription.getUser()).thenReturn(user);
		when(configuration.getTemplate(anyString())).thenReturn(mock(Template.class));
		when(user.getEmail()).thenReturn("my-email");
	}

	@Test
	public void whenSendingNotificationForJournalSubscribedUsersMustBeNotified() {		
		emailSenderImpl.sendImmediateNotificationFor(journal);
		
		verify(emailProvider, times(1)).sendMessageTo(eq("my-email"), anyString(), anyString());
	}

	@Test
	public void whenSendingNotificationForJournalSubscribedUsersMustBeLookedUpInTheSameCategory() {
		Category category = mock(Category.class);
		when(journal.getCategory()).thenReturn(category);
		
		emailSenderImpl.sendImmediateNotificationFor(journal);
		
		verify(subscriptionRepository, times(1)).findByCategory(category);
	}
	
	@Test
	public void dailyDigestLooksForJournalsFromTheLast24Hours() throws Exception {
		date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse("2016/01/01 23:59:59");
		
		assertThat(emailSenderImpl.journalsFromLast24Hours().toString()).contains("2015");
		assertThat(emailSenderImpl.journalsFromLast24Hours().toString()).contains(">");
	}
	
	@Test
	public void whenNoJournalsFoundNoDailyDigestIsSent() throws Exception {
		when(journalRepository.findAll(any(Predicate.class))).thenReturn(Collections.emptyList());
		
		emailSenderImpl.sendDailyDigest();
		
		verify(emailProvider, never()).sendMessageTo(anyString(), anyString(), anyString());
	}
	
	@Test
	public void whenJournalsFoundInLast24HoursAllUsersAreNotifiedInDigest() throws Exception {
		when(journalRepository.findAll(any(Predicate.class))).thenReturn(Arrays.asList(journal));
		
		emailSenderImpl.sendDailyDigest();
		
		verify(emailProvider, times(1)).sendMessageTo(eq("my-email"), anyString(), anyString());
	}
}
