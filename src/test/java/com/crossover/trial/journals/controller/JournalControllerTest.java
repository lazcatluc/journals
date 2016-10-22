package com.crossover.trial.journals.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import com.crossover.trial.journals.model.Category;
import com.crossover.trial.journals.model.Journal;
import com.crossover.trial.journals.model.Publisher;
import com.crossover.trial.journals.model.Subscription;
import com.crossover.trial.journals.model.User;
import com.crossover.trial.journals.repository.JournalRepository;
import com.crossover.trial.journals.repository.UserRepository;
import com.crossover.trial.journals.service.CurrentUser;

public class JournalControllerTest {
	
	@InjectMocks
	private JournalController journalController;

	@Mock
	private JournalRepository journalRepository;

	@Mock
	private UserRepository userRepository;
	
	@Mock
	private Authentication principal;
	
	@Mock
	private CurrentUser currentUser;
	
	@Mock
	private Journal journal;
	
	@Mock
	private Publisher publisher;
	
	@Mock
	private User user;
	
	@Before
	public void setUp() throws Exception {
		journalController = new JournalController() {
			@Override
			ResponseEntity getJournalContent(Journal journal) {
				return ResponseEntity.ok("mock");
			}
		};
		MockitoAnnotations.initMocks(this);
		when(principal.getPrincipal()).thenReturn(currentUser);
		when(currentUser.getUser()).thenReturn(mock(User.class));
		when(journalRepository.findOne(anyLong())).thenReturn(journal);
		when(userRepository.findOne(anyLong())).thenReturn(user);
		when(journal.getPublisher()).thenReturn(publisher);
		
	}

	@Test
	public void returnsOKWhenUserIsSubscribedToSameCategory() throws IOException {
		Category category = new Category();
		category.setId(1L);
		when(journal.getCategory()).thenReturn(category);
		Subscription subscription = new Subscription();
		subscription.setCategory(category);
		when(user.getSubscriptions()).thenReturn(Arrays.asList(subscription));

		assertThat(journalController.renderDocument(principal, 1L).getStatusCode()).isEqualTo(HttpStatus.OK);
	}
	
	@Test
	public void returnsOKWhenUserIsTheOriginalPublisherEvenIfNotSubscribed() throws Exception {
		when(user.getSubscriptions()).thenReturn(Collections.emptyList());
		when(user.getId()).thenReturn(1L);
		when(publisher.getId()).thenReturn(1L);
		
		assertThat(journalController.renderDocument(principal, 1L).getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void returnsNotFoundWhenUserIsNeitherSubscribedNorPublisher() throws Exception {
		when(user.getSubscriptions()).thenReturn(Collections.emptyList());
		when(user.getId()).thenReturn(1L);
		when(publisher.getId()).thenReturn(2L);
		
		assertThat(journalController.renderDocument(principal, 1L).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		
	}

}
