

import com.sun.mail.util.MailSSLSocketFactory;
import java.security.GeneralSecurityException;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.apache.log4j.Logger;
import org.ow2.bonita.connector.impl.email.SMTPAuthenticator;
import java.io.IOException;
import java.security.cert.X509Certificate;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.URL;
import javax.net.ssl.SSLContext;
import java.security.SecureRandom;
import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.StringTokenizer;
import javax.mail.Address;
import javax.mail.util.ByteArrayDataSource;

public class CommunicationUtility {

    /* Minimum Details to Send SMS through NIC*/
    private static final String SMS_username = ;
    private static final String SMS_pin = ;
    private static final String SMS_signature = ;
    private static final StringBuilder SMS_url = new StringBuilder();

    /* Minimum Details to Send Mail through NIC*/
    private static final String senderEmailID =;
    private static final String senderEmailPassword =;
    private static final String mailPort = "465";

    private static final Logger logger = Logger.getLogger(CommunicationUtility.class);

    private static TrustManager[] get_trust_mgr() {
        TrustManager[] certs = new TrustManager[]{new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] certs, String t) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] certs, String t) {
            }
        }};
        return certs;
    }

    private static void print_content(HttpsURLConnection con) {
        if (con != null) {

            try {
                System.out.println("****** Content of the SMS URL ********");
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        con.getInputStream()));

                String input;

                while ((input = br.readLine()) != null) {
                    System.out.println(input);
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    
    public static void SendMail(
            String receipientEmailID,
            String subject,
            String body,
            boolean isHtmlBody) throws GeneralSecurityException, MessagingException, UnsupportedEncodingException {

        SMTPAuthenticator auth = new SMTPAuthenticator(senderEmailID, senderEmailPassword);

        MailSSLSocketFactory sf = new MailSSLSocketFactory();
        sf.setTrustAllHosts(true);

        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.setProperty("mail.smtp.host", "mail.gov.in");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", mailPort);
        props.put("mail.smtp.socketFactory.port", mailPort);
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.ssl.socketFactory", sf);
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.quitwait", "false");

        Session session = Session.getInstance(props, auth);
        session.setDebug(true);

        MimeMessage message = new MimeMessage(session);

        message.setFrom(new InternetAddress(senderEmailID, ""));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(receipientEmailID));
        message.setRecipient(Message.RecipientType.CC, new InternetAddress(senderEmailID));
        message.setSubject(subject);

        if (isHtmlBody) {
            message.setContent(body, "text/html; charset=utf-8");
        } else {
            message.setText(body);
        }

        System.out.println("Before Sending...");
        Transport.send(message);
        System.out.println("Mail Send.");
    }

    public static void SendMail(
            String receipientEmailID,
            String subject,
            String body,
            boolean isHtmlBody,
            String filePath,
            String fileName) throws GeneralSecurityException, MessagingException, UnsupportedEncodingException {

        SMTPAuthenticator auth = new SMTPAuthenticator(senderEmailID, senderEmailPassword);

        MailSSLSocketFactory sf = new MailSSLSocketFactory();
        sf.setTrustAllHosts(true);

        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.setProperty("mail.smtp.host", "mail.gov.in");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", mailPort);
        props.put("mail.smtp.socketFactory.port", mailPort);
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.ssl.socketFactory", sf);
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.quitwait", "false");

        Session session = Session.getInstance(props, auth);
        session.setDebug(true);

        MimeMessage message = new MimeMessage(session);

        message.setFrom(new InternetAddress(senderEmailID, ));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(receipientEmailID));
        message.setRecipient(Message.RecipientType.CC, new InternetAddress(senderEmailID));
        message.setSubject(subject);

//            MimeBodyPart mbp = new MimeBodyPart();
//            if (isHtmlBody) {
//                mbp.setContent(body, "text/html");
//            }
//
//            message.setText(body);
        // Create the message part 
        BodyPart messageBodyPart = new MimeBodyPart();
        // Fill the message 
        if (isHtmlBody) {
            messageBodyPart.setContent(body, "text/html; charset=utf-8");
        } else {
            messageBodyPart.setText(body);
        }
        // Create a Multipart 
        Multipart multipart = new MimeMultipart();
        // Add part one
        multipart.addBodyPart(messageBodyPart);
        // // Part two is attachment // // Create second body part 
        messageBodyPart = new MimeBodyPart();
        // Get the attachment 
        DataSource source = new FileDataSource(filePath);
        // Set the data handler to the attachment 
        messageBodyPart.setDataHandler(new DataHandler(source));
        // Set the filename
        messageBodyPart.setFileName(fileName);
        // Add part two 
        multipart.addBodyPart(messageBodyPart);
        // Put parts in message
        message.setContent(multipart);

        System.out.println("Before Sending...");
        Transport.send(message);
        System.out.println("Mail Send.");
    }

    // To Multiple Receipients...
    public static void SendMail(
            String TOs,
            String CCs,
            String BCCs,
            String subject,
            String body,
            boolean isHtmlBody) throws GeneralSecurityException, MessagingException, UnsupportedEncodingException {

        SMTPAuthenticator auth = new SMTPAuthenticator(senderEmailID, senderEmailPassword);

        MailSSLSocketFactory sf = new MailSSLSocketFactory();
        sf.setTrustAllHosts(true);

        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.setProperty("mail.smtp.host", "mail.gov.in");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", mailPort);
        props.put("mail.smtp.socketFactory.port", mailPort);
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.ssl.socketFactory", sf);
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.quitwait", "false");

        Session session = Session.getInstance(props, auth);
        session.setDebug(true);

        MimeMessage message = new MimeMessage(session);

        message.setFrom(new InternetAddress(senderEmailID, ));

        StringTokenizer st = null;

        // Getting & Setting TOs Recipient(s)
        st = new StringTokenizer(TOs, ",");
        while (st.hasMoreTokens()) {
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(st.nextToken()));
        }
        st = null;

        // Getting & Setting CCs Receipient(s)
        st = new StringTokenizer(CCs, ",");
        while (st.hasMoreTokens()) {
            message.addRecipient(Message.RecipientType.CC, new InternetAddress(st.nextToken()));
        }
        st = null;

        // Getting & Setting BCCs Receipient(s)
        st = new StringTokenizer(BCCs, ",");
        while (st.hasMoreTokens()) {
            message.addRecipient(Message.RecipientType.BCC, new InternetAddress(st.nextToken()));
        }
        st = null;

        message.setSubject(subject);

        if (isHtmlBody) {
            message.setContent(body, "text/html; charset=utf-8");
        } else {
            message.setText(body);
        }

        System.out.println("Before Sending...");
        Transport.send(message);
        System.out.println("Mail Send.");
    }

    public static void SendMail(
            String TOs,
            String CCs,
            String BCCs,
            String subject,
            String body,
            boolean isHtmlBody,
            String filePath,
            String fileName) throws GeneralSecurityException, MessagingException, UnsupportedEncodingException {

        SMTPAuthenticator auth = new SMTPAuthenticator(senderEmailID, senderEmailPassword);

        MailSSLSocketFactory sf = new MailSSLSocketFactory();
        sf.setTrustAllHosts(true);

        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.setProperty("mail.smtp.host", "mail.gov.in");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", mailPort);
        props.put("mail.smtp.socketFactory.port", mailPort);
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.ssl.socketFactory", sf);
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.quitwait", "false");

        Session session = Session.getInstance(props, auth);
        session.setDebug(true);

        MimeMessage message = new MimeMessage(session);

        message.setFrom(new InternetAddress(senderEmailID, ));

        StringTokenizer st = null;

        // Getting & Setting TOs Recipient(s)
        st = new StringTokenizer(TOs, ",");
        while (st.hasMoreTokens()) {
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(st.nextToken()));
        }
        st = null;

        // Getting & Setting CCs Receipient(s)
        st = new StringTokenizer(CCs, ",");
        while (st.hasMoreTokens()) {
            message.addRecipient(Message.RecipientType.CC, new InternetAddress(st.nextToken()));
        }
        st = null;

        // Getting & Setting BCCs Receipient(s)
        st = new StringTokenizer(BCCs, ",");
        while (st.hasMoreTokens()) {
            message.addRecipient(Message.RecipientType.BCC, new InternetAddress(st.nextToken()));
        }
        st = null;

        message.setSubject(subject);

//            MimeBodyPart mbp = new MimeBodyPart();
//            if (isHtmlBody) {
//                mbp.setContent(body, "text/html");
//            }
//
//            message.setText(body);
        // Create the message part 
        BodyPart messageBodyPart = new MimeBodyPart();
        // Fill the message 
        if (isHtmlBody) {
            messageBodyPart.setContent(body, "text/html; charset=utf-8");
        } else {
            messageBodyPart.setText(body);
        }
        // Create a Multipart 
        Multipart multipart = new MimeMultipart();
        // Add part one
        multipart.addBodyPart(messageBodyPart);
        // // Part two is attachment // // Create second body part 
        messageBodyPart = new MimeBodyPart();
        // Get the attachment 
        DataSource source = new FileDataSource(filePath);
        // Set the data handler to the attachment 
        messageBodyPart.setDataHandler(new DataHandler(source));
        // Set the filename
        messageBodyPart.setFileName(fileName);
        // Add part two 
        multipart.addBodyPart(messageBodyPart);
        // Put parts in message
        message.setContent(multipart);

        System.out.println("Before Sending...");
        Transport.send(message);
        System.out.println("Mail Send.");
    }

    public static void SendMail(
            String TOs,
            String CCs,
            String BCCs,
            String subject,
            String body,
            boolean isHtmlBody,
            byte[] bytes,
            String fileName,
            String projectRootPath) throws GeneralSecurityException, MessagingException, UnsupportedEncodingException, IOException {

        SMTPAuthenticator auth = new SMTPAuthenticator(senderEmailID, senderEmailPassword);

        MailSSLSocketFactory sf = new MailSSLSocketFactory();
        sf.setTrustAllHosts(true);

        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.setProperty("mail.smtp.host", "mail.gov.in");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", mailPort);
        props.put("mail.smtp.socketFactory.port", mailPort);
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.ssl.socketFactory", sf);
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.quitwait", "false");

        Session session = Session.getInstance(props, auth);
        session.setDebug(false);

        MimeMessage message = new MimeMessage(session);

        message.setFrom(new InternetAddress(senderEmailID, );

        StringTokenizer st = null;

        // Getting & Setting TOs Recipient(s)
        st = new StringTokenizer(TOs, ",");
        while (st.hasMoreTokens()) {
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(st.nextToken()));
        }
        st = null;

        // Getting & Setting CCs Receipient(s)
        st = new StringTokenizer(CCs, ",");
        while (st.hasMoreTokens()) {
            message.addRecipient(Message.RecipientType.CC, new InternetAddress(st.nextToken()));
        }
        st = null;

        // Getting & Setting BCCs Receipient(s)
        st = new StringTokenizer(BCCs, ",");
        while (st.hasMoreTokens()) {
            message.addRecipient(Message.RecipientType.BCC, new InternetAddress(st.nextToken()));
        }
        st = null;

        message.setSubject(subject);

//            MimeBodyPart mbp = new MimeBodyPart();
//            if (isHtmlBody) {
//                mbp.setContent(body, "text/html");
//            }
//
//            message.setText(body);
        // Create the message part 
        BodyPart messageBodyPart = new MimeBodyPart();
        // Fill the message 
        if (isHtmlBody) {
            messageBodyPart.setContent(body, "text/html; charset=utf-8");
        } else {
            messageBodyPart.setText(body);
        }
        // Create a Multipart 
        Multipart multipart = new MimeMultipart();
        // Add part one
        multipart.addBodyPart(messageBodyPart);
        // // Part two is attachment // // Create second body part 
        messageBodyPart = new MimeBodyPart();
        // Get the attachment 
        String mimeType = PTaxUtility.getMIMEType(fileName.split("[.]")[1], projectRootPath);
        DataSource source = new ByteArrayDataSource(bytes, mimeType); // Set the data handler to the attachment 
        messageBodyPart.setDataHandler(new DataHandler(source));
        // Set the filename
        messageBodyPart.setFileName(fileName);
        // Add part two 
        multipart.addBodyPart(messageBodyPart);
        // Put parts in message
        message.setContent(multipart);

        System.out.println("Before Sending...");
        Transport.send(message);
        System.out.println("Mail Send.");
    }
	public static void SendMail(
            String TOs,
            String CCs,
            String BCCs,
            String subject,
            String body,
            boolean isHtmlBody,
            byte[] bytes,
            String fileName,
            String projectRootPath,
            String mimeType) throws GeneralSecurityException, MessagingException, UnsupportedEncodingException, IOException {

        SMTPAuthenticator auth = new SMTPAuthenticator(senderEmailID, senderEmailPassword);

        MailSSLSocketFactory sf = new MailSSLSocketFactory();
        sf.setTrustAllHosts(true);

        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.setProperty("mail.smtp.host", "mail.gov.in");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", mailPort);
        props.put("mail.smtp.socketFactory.port", mailPort);
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.ssl.socketFactory", sf);
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.quitwait", "false");

        Session session = Session.getInstance(props, auth);
        session.setDebug(false);

        MimeMessage message = new MimeMessage(session);

        message.setFrom(new InternetAddress(senderEmailID, ));

        StringTokenizer st = null;

        // Getting & Setting TOs Recipient(s)
        st = new StringTokenizer(TOs, ",");
        while (st.hasMoreTokens()) {
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(st.nextToken()));
        }
        st = null;

        // Getting & Setting CCs Receipient(s)
        st = new StringTokenizer(CCs, ",");
        while (st.hasMoreTokens()) {
            message.addRecipient(Message.RecipientType.CC, new InternetAddress(st.nextToken()));
        }
        st = null;

        // Getting & Setting BCCs Receipient(s)
        st = new StringTokenizer(BCCs, ",");
        while (st.hasMoreTokens()) {
            message.addRecipient(Message.RecipientType.BCC, new InternetAddress(st.nextToken()));
        }
        st = null;

        message.setSubject(subject);

//            MimeBodyPart mbp = new MimeBodyPart();
//            if (isHtmlBody) {
//                mbp.setContent(body, "text/html");
//            }
//
//            message.setText(body);
        // Create the message part 
        BodyPart messageBodyPart = new MimeBodyPart();
        // Fill the message 
        if (isHtmlBody) {
            messageBodyPart.setContent(body, "text/html; charset=utf-8");
        } else {
            messageBodyPart.setText(body);
        }
        // Create a Multipart 
        Multipart multipart = new MimeMultipart();
        // Add part one
        multipart.addBodyPart(messageBodyPart);
        // // Part two is attachment // // Create second body part 
        messageBodyPart = new MimeBodyPart();
        
        DataSource source = new ByteArrayDataSource(bytes, mimeType); // Set the data handler to the attachment 
        messageBodyPart.setDataHandler(new DataHandler(source));
        // Set the filename
        messageBodyPart.setFileName(fileName);
        // Add part two 
        multipart.addBodyPart(messageBodyPart);
        // Put parts in message
        message.setContent(multipart);

        System.out.println("Before Sending...");
        Transport.send(message);
        System.out.println("Mail Send.");
    }
}
