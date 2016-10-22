package com.crossover.trial.journals.email;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.crossover.trial.journals.Application;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class MailSenderTest {
	@Autowired
	private JavaMailSender javaMailSender;

	@Test
	@Ignore("needs password")
	public void send() {
		MimeMessage mail = javaMailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(mail, true);
			helper.setTo("lazcatluc@gmail.com");
			helper.setFrom("lazcatluc@gmail.com");
			helper.setSubject("Lorem ipsum");
			helper.setText("Lorem ipsum dolor sit amet [...]");
		} catch (MessagingException e) {
			e.printStackTrace();
		} finally {
		}
		javaMailSender.send(mail);
	}
}
