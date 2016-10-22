package com.crossover.trial.journals.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;

import com.crossover.trial.journals.dto.SubscriptionDTO;
import com.crossover.trial.journals.model.Category;
import com.crossover.trial.journals.model.Publisher;
import com.crossover.trial.journals.model.Subscription;
import com.crossover.trial.journals.model.User;
import com.crossover.trial.journals.repository.CategoryRepository;
import com.crossover.trial.journals.repository.PublisherRepository;
import com.crossover.trial.journals.service.CurrentUser;
import com.crossover.trial.journals.service.JournalService;
import com.crossover.trial.journals.service.UserService;

public class JournalRestServiceTest {

	@InjectMocks
	private JournalRestService journalRestService;
	
	@Mock
	private PublisherRepository publisherRepository;

	@Mock
	private JournalService journalService;

	@Mock
	private UserService userService;

	@Mock
	private CategoryRepository categoryRepository;
	
	@Mock
	private Authentication principal;
	
	@Mock
	private CurrentUser currentUser;	
	
	@Before
	public void setUp() throws Exception {
		journalRestService = new JournalRestService();
		MockitoAnnotations.initMocks(this);
		when(principal.getPrincipal()).thenReturn(currentUser);
	}

	@Test
	public void getsUserSubscriptionsForBrowsing() {
		assertThat(journalRestService.browse(principal).getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void getsPublishedListOfValidPublisher() throws Exception {
		when(publisherRepository.findByUser(any(User.class))).thenReturn(Optional.of(mock(Publisher.class)));
		
		assertThat(journalRestService.publishedList(principal)).isEqualTo(Collections.emptyList());
	}
	
	@Test
	public void callsUnpublishServiceForValidPublisher() throws Exception {
		when(publisherRepository.findByUser(any(User.class))).thenReturn(Optional.of(mock(Publisher.class)));
		
		journalRestService.unPublish(1L, principal);
		
		verify(journalService, times(1)).unPublish(any(Publisher.class), eq(1L));
	}
	
	@Test
	public void callsSubscribeServiceForValidUser() throws Exception {
		when(currentUser.getUser()).thenReturn(mock(User.class));
		
		journalRestService.subscribe(1L, principal);
		
		verify(userService, times(1)).subscribe(any(User.class), eq(1L));
	}
	
	@Test
	public void buildsActiveSubscriptions() throws Exception {
		User user = mock(User.class);
		Category category = new Category();
		category.setId(1L);
		Subscription subscription = new Subscription();
		subscription.setCategory(category);
		when(userService.findById(anyLong())).thenReturn(user);
		when(user.getSubscriptions()).thenReturn(Arrays.asList(subscription));
		when(categoryRepository.findAll()).thenReturn(Arrays.asList(category));
		
		List<SubscriptionDTO> subscriptionDTOs = journalRestService.getUserSubscriptions(principal);
		
		assertThat(subscriptionDTOs.get(0).isActive()).isTrue();
	}
	
	@Test
	public void buildsInactiveSubscriptions() throws Exception {
		User user = mock(User.class);
		Category category = new Category();
		category.setId(1L);
		Category otherCategory = new Category();
		otherCategory.setId(2L);
		Subscription subscription = new Subscription();
		subscription.setCategory(category);
		when(userService.findById(anyLong())).thenReturn(user);
		when(user.getSubscriptions()).thenReturn(Arrays.asList(subscription));
		when(categoryRepository.findAll()).thenReturn(Arrays.asList(otherCategory));
		
		List<SubscriptionDTO> subscriptionDTOs = journalRestService.getUserSubscriptions(principal);
		
		assertThat(subscriptionDTOs.get(0).isActive()).isFalse();
	}
	
	@Test
	@Ignore("TODO")
	public void returns404NotFoundIfPublisherDoesNotExist() throws Exception {
		boolean toDo = true;
		
		assertThat(toDo).isFalse();
	}
}
