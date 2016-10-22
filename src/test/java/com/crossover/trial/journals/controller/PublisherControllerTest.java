package com.crossover.trial.journals.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.crossover.trial.journals.model.Journal;
import com.crossover.trial.journals.model.Publisher;
import com.crossover.trial.journals.model.User;
import com.crossover.trial.journals.repository.PublisherRepository;
import com.crossover.trial.journals.service.CurrentUser;
import com.crossover.trial.journals.service.JournalService;

public class PublisherControllerTest {
	
	private static String folder;
	
	@Mock
	private PublisherRepository publisherRepository;

	@Mock
	private JournalService journalService;
	
	@Mock
	private MultipartFile file;
	
	@Mock
	private RedirectAttributes redirectAttributes;
	
	@Mock
	private Authentication principal;
	
	@Mock
	private CurrentUser currentUser;
	
	@Mock
	private Publisher publisher;
	
	private String previousUploadDir;
	
	@InjectMocks
	private PublisherController publisherController;

	@BeforeClass
	public static void setUpClass() {
		folder = "./src/test/resources/upload"+UUID.randomUUID().toString();
	}
	
	private static void deleteRecursively(File file) {
		if (file.isDirectory()) {
			Arrays.stream(file.listFiles()).forEach(PublisherControllerTest::deleteRecursively);
		}
		assertThat(file.delete()).isTrue();
	}
	
	@AfterClass
	public static void tearDownClass() {
		deleteRecursively(new File(folder));
	}
	
	@Before
	public void setUp() {
		publisherController = new PublisherController();
		MockitoAnnotations.initMocks(this);
		when(principal.getPrincipal()).thenReturn(currentUser);
		when(publisherRepository.findByUser(any(User.class))).thenReturn(Optional.of(publisher));
		previousUploadDir = System.setProperty("upload-dir", folder);
	}
	
	@After
	public void tearDown() {
		if (previousUploadDir == null) {
			System.clearProperty("upload-dir");
		}
		else {
			System.setProperty("upload-dir", previousUploadDir);
		}
	}
	
	@Test
	public void doesntPublishWhenFileIsEmpty() throws IOException { 
		when(file.isEmpty()).thenReturn(true);
		when(publisher.getId()).thenReturn(1L);
		
		publisherController.handleFileUpload("my-file", 1L, file, redirectAttributes, principal);
		
		verify(journalService, never()).publish(any(Publisher.class), any(Journal.class), anyLong());
	}
	
	@Test
	public void copiesWhenFileHasActualContent() throws Exception {
		when(file.isEmpty()).thenReturn(false);
		when(file.getInputStream()).thenReturn(new ByteArrayInputStream("actual-content".getBytes()));
		when(publisher.getId()).thenReturn(1L);
		
		publisherController.handleFileUpload("my-file", 2L, file, redirectAttributes, principal);
		
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(new File(folder+"/1").listFiles()[0])))) {
			assertThat(reader.readLine()).isEqualTo("actual-content");
		}
	}
	
	@Test
	public void publishesWhenFileHasActualContent() throws Exception {
		when(file.isEmpty()).thenReturn(false);
		when(file.getInputStream()).thenReturn(new ByteArrayInputStream("actual-content".getBytes()));
		when(publisher.getId()).thenReturn(1L);
		
		publisherController.handleFileUpload("my-file", 2L, file, redirectAttributes, principal);
		
		verify(journalService, times(1)).publish(any(Publisher.class), any(Journal.class), anyLong());
	}

	@Test
	public void warnsOnCopyError() throws Exception {
		when(file.isEmpty()).thenReturn(false);
		when(file.getInputStream()).thenThrow(mock(IOException.class));
		when(publisher.getId()).thenReturn(1L);
		
		publisherController.handleFileUpload("my-file", 1L, file, redirectAttributes, principal);
		
		verify(redirectAttributes, times(1)).addFlashAttribute(anyString(), anyString());
	}
}
