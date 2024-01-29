package mainPackage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Properties;


import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class CommonMethods {
	
	public static void sendFileToMail() 
	   {
	     
	      // Assuming you are sending email through relay.jangosmtp.net
	      String host = "smtpout.asia.secureserver.net";

	      Properties props = new Properties();
	      props.put("mail.smtp.auth", "true");
	      props.put("mail.smtp.starttls.enable", "true");
	      props.put("mail.smtp.host", host);
	      props.put("mail.smtp.port", "465");

	      // Get the Session object.
	      Session session = Session.getInstance(props,
	         new javax.mail.Authenticator() {
	            protected PasswordAuthentication getPasswordAuthentication() {
	               return new PasswordAuthentication(AppConfig.fromEmail, AppConfig.fromEmailPassword);
	            }
	         });

	      try {
	         // Create a default MimeMessage object.
	         Message message = new MimeMessage(session);

	         // Set From: header field of the header.
	         message.setFrom(new InternetAddress(AppConfig.fromEmail));

	         InternetAddress[] toAddresses = InternetAddress.parse(AppConfig.toEmail);
	         // Set To: header field of the header.
	        message.setRecipients(Message.RecipientType.TO,
	        		toAddresses);

	        
	        InternetAddress[] CCAddresses = InternetAddress.parse(AppConfig.CCEmail);
	         // Set CC: header field of the header.
	         message.setRecipients(Message.RecipientType.CC,
	        		 CCAddresses);
	         
	         // Set Subject: header field
	        String subject = RunnerClass.mailSubject;
	        message.setSubject(subject);

	         // Create the message part
	         BodyPart messageBodyPart = new MimeBodyPart();

	         // Now set the actual message
	        
	         messageBodyPart.setText(RunnerClass.messageBody);

	         // Create a multipar message
	         Multipart multipart = new MimeMultipart();

	         // Set text message part
	         multipart.addBodyPart(messageBodyPart);

	      

	         // Send message
	         Transport.send(message);

	         System.out.println("Sent Email successfully....");
	  
	       
	       
	      } catch (Exception e) 
	      {
	    	  e.printStackTrace();
	         throw new RuntimeException(e);
	      }
	   }

}
