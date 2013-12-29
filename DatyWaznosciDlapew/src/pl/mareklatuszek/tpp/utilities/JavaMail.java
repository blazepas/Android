package pl.mareklatuszek.tpp.utilities;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Security;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import android.util.Log;

public class JavaMail extends javax.mail.Authenticator {   
    private String mailhost = "smtp.gmail.com";  
	private String sender = "tpptesty@gmail.com"; 
    private String user = "tpptesty";   
    private String password = "tpptesty1"; 
    private String port = "465";
    private Session session;
  

    static {   
        Security.addProvider(new JSSEProvider());   
    }  

    public JavaMail() {   
        Properties props = new Properties();   
        props.setProperty("mail.transport.protocol", "smtp");   
        props.setProperty("mail.host", mailhost);   
        props.put("mail.smtp.auth", "true");   
        props.put("mail.smtp.port", port);   
        props.put("mail.smtp.socketFactory.port", port);   
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");   
        props.put("mail.smtp.socketFactory.fallback", "false");   
        props.setProperty("mail.smtp.quitwait", "false");   

        session = Session.getDefaultInstance(props, this);   
    }   
    
    public synchronized void sendMailWithList(String subject, String body, String recipients) throws Exception {   
    	
        try{
        	
	        MimeMessage message = new MimeMessage(session);   
	        DataHandler handler = new DataHandler(new ByteArrayDataSource(body.getBytes(), "text/html"));   
	        message.setSender(new InternetAddress(sender));   
	        message.setSubject(subject);   
	        message.setDataHandler(handler);   
	        if (recipients.indexOf(',') > 0)   
	            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));   
	        else  
	            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));   
	        Transport.send(message);   
        }catch(Exception e){
        	//TODO
        }
    } 
    
    public synchronized boolean sendMailWithProduct(String subject, String body, String imagePath, String codePath,
    			String recipients) throws Exception {  
    	
    	Log.i("image path", imagePath);
    	Log.i("code path", codePath);
    	
        try{
        	
	        MimeMessage message = new MimeMessage(session);     
	        message.setSender(new InternetAddress(sender));   
	        message.setSubject(subject);    
	        if (recipients.indexOf(',') > 0)   
	            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));   
	        else  
	            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients)); 
	        
	        MimeMultipart multipart = new MimeMultipart("related");
	
	        // tabela
	        BodyPart messageBodyPart = new MimeBodyPart();
	        messageBodyPart.setContent(body, "text/html; charset=utf-8");
	        multipart.addBodyPart(messageBodyPart);
	        
	        // obrazek
	        if (imagePath != null && !imagePath.isEmpty()) {
	        	messageBodyPart = new MimeBodyPart();
	  	        DataSource fds = new FileDataSource(imagePath);
	  	        messageBodyPart.setDataHandler(new DataHandler(fds));
	  	        messageBodyPart.setHeader("Content-ID","<image>");
	  	        multipart.addBodyPart(messageBodyPart);
	        }
 
	        //QR code
	        messageBodyPart = new MimeBodyPart();
	        DataSource fds2 = new FileDataSource(codePath);
	        messageBodyPart.setDataHandler(new DataHandler(fds2));
	        messageBodyPart.setHeader("Content-ID","<image2>");
	        multipart.addBodyPart(messageBodyPart);
	
	        // dodanie wszystkiego do maila
	        message.setContent(multipart);
	        
	        Transport.send(message);   
        }catch(Exception e){
        	Log.i("sendMailWithProduct", "error");
        	return false;
        }
        
        return true;
    } 

    protected PasswordAuthentication getPasswordAuthentication() {   
        return new PasswordAuthentication(user, password);   
    }   

    public class ByteArrayDataSource implements DataSource {   
        private byte[] data;   
        private String type;   

        public ByteArrayDataSource(byte[] data, String type) {   
            super();   
            this.data = data;   
            this.type = type;   
        }   

        public ByteArrayDataSource(byte[] data) {   
            super();   
            this.data = data;   
        }   

        public void setType(String type) {   
            this.type = type;   
        }   

        public String getContentType() {   
            if (type == null)   
                return "application/octet-stream";   
            else  
                return type;   
        }   

        public InputStream getInputStream() throws IOException {   
            return new ByteArrayInputStream(data);   
        }   

        public String getName() {   
            return "ByteArrayDataSource";   
        }   

        public OutputStream getOutputStream() throws IOException {   
            throw new IOException("Not Supported");   
        }   
    }   
}  
