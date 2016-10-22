package com.crossover.trial.journals.service;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.crossover.trial.journals.mail.EmailSender;
import com.crossover.trial.journals.model.Category;
import com.crossover.trial.journals.model.Journal;
import com.crossover.trial.journals.model.Publisher;
import com.crossover.trial.journals.repository.CategoryRepository;
import com.crossover.trial.journals.repository.JournalRepository;
import com.crossover.trial.journals.repository.UserRepository;

public class JournalServiceImplTest {

	@Mock
	private JournalRepository journalRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private CategoryRepository categoryRepository;

	@Mock
	private EmailSender emailSender;

	@Mock
	private Publisher publisher;
	@Mock
	private Journal journal;

	@InjectMocks
	private JournalServiceImpl journalServiceImpl;

	@Before
	public void setUp() throws Exception {
		journalServiceImpl = new JournalServiceImpl();
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void callsMailSenderWhenANewJournalIsPosted() {
		when(categoryRepository.findOne(anyLong())).thenReturn(mock(Category.class));
		when(journalRepository.save(journal)).thenReturn(journal);
		
		journalServiceImpl.publish(publisher, journal, 1L);
		
		verify(emailSender, times(1)).sendImmediateNotificationFor(journal);
	}

}
