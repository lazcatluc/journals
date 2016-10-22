package com.crossover.trial.journals.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.crossover.trial.journals.model.Category;

public class SubscriptionDTOTest {

	private Category category;
	private SubscriptionDTO subscriptionDTO;
	
	@Before
	public void setUp() throws Exception {
		category = new Category();
		category.setId(1L);
		subscriptionDTO = new SubscriptionDTO(category);
	}

	@Test
	public void isEqualToSelf() {
		assertThat(subscriptionDTO).isEqualTo(subscriptionDTO);
	}

	@Test
	public void isNotEqualToNull() {
		assertThat(subscriptionDTO).isNotEqualTo(null);
	}
	
	@Test
	public void isNotEqualToOtherClass() {
		assertThat(subscriptionDTO).isNotEqualTo(category);
	}
	
	@Test
	public void isEqualToOtherSubscriptionWithTheSameCategory() throws Exception {
		assertThat(subscriptionDTO).isEqualTo(new SubscriptionDTO(category));
	}
	
	@Test
	public void isNotEqualToOtherSubscriptionWithDifferentCategory() throws Exception {
		Category category = new Category();
		category.setId(2L);
		assertThat(subscriptionDTO).isNotEqualTo(new SubscriptionDTO(category));
	}
	
	@Test
	@Ignore("TODO")
	public void hasSameHashCodeWithSubscriptoinWithTheSameCategory() throws Exception {
		assertThat(subscriptionDTO.hashCode()).isEqualTo(new SubscriptionDTO(category).hashCode());	
	}
}
