package com.crossover.trial.journals.mail;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class JavaMailProvider implements EmailProvider {
	
	private final static Logger LOG = Logger.getLogger(JavaMailProvider.class);
	
	@Autowired
	private JavaMailSender javaMailSender;

	@Value("${spring.mail.from}")
	private String from;
	
	@Value("${spring.mail.reallySend}")
	private String reallySend;
	
	@Override
	@Async
	public void sendMessageTo(String to, String subject, String message) {
		MimeMessage mail = javaMailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(mail, true);
			helper.setTo(to);
			helper.setFrom(from);
			helper.setSubject(subject);
			helper.setText(message, true);
			if ("true".equalsIgnoreCase(reallySend)) {
				javaMailSender.send(mail);
			}
			else {
				LOG.info("Would have sent the message `"+subject+"`: `"+message+"` to "+to);
			}
		} catch (MessagingException | MailException e) {
			LOG.error(e);
		} 
		
	}
	
}
