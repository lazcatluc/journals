package com.crossover.trial.journals.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.crossover.trial.journals.model.Category;
import com.crossover.trial.journals.model.Subscription;
import com.crossover.trial.journals.model.User;
import com.crossover.trial.journals.repository.CategoryRepository;
import com.crossover.trial.journals.repository.UserRepository;

public class UserServiceImplTest {

	@InjectMocks
	private UserServiceImpl userServiceImpl;

	private UserRepository userRepository;

	@Mock
	private CategoryRepository categoryRepository;

	@Mock
	private User user;

	@Mock
	private Subscription subscription;

	@Mock
	private Category category;

	@Before
	public void setUp() throws Exception {
		userRepository = mock(UserRepository.class);
		userServiceImpl = new UserServiceImpl(userRepository);
		MockitoAnnotations.initMocks(this);
		when(user.getSubscriptions()).thenReturn(new ArrayList<>(Arrays.asList(subscription)));
		when(subscription.getCategory()).thenReturn(category);
	}

	@Test
	public void doNothingWhenUserAlreadySubscribedToCategory() {
		when(category.getId()).thenReturn(1L);

		userServiceImpl.subscribe(user, 1L);

		verify(userRepository, never()).save(user);
	}
	
	@Test(expected = ServiceException.class)
	public void throwsExceptionWhenTryingToAddNonExistentCategory() throws Exception {
		when(category.getId()).thenReturn(1L);
		
		userServiceImpl.subscribe(user, 2L);
	}
	
	@Test
	public void addCategoryWhenUserDoesNotHaveIt() throws Exception {
		when(category.getId()).thenReturn(1L);
		when(categoryRepository.findOne(2L)).thenReturn(mock(Category.class));
		
		userServiceImpl.subscribe(user, 2L);
		
		verify(userRepository, times(1)).save(user);
	}

	@Test
	@Ignore("TODO")
	public void addsSubscriptionsToUserWhenNullInitially() throws Exception {
		boolean toDo = true;
		
		assertThat(toDo).isFalse();
	}
}
