/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pinartek.channel;

import com.pinartek.dbentities.EventEntity;
import com.pinartek.dbentities.MessageEntity;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author Family
 */
public class EmailConnector extends ChannelConnectorTemplate {
    
    public EmailConnector() {
        
    }

    private Integer sendEmail( List to, String subject, String msg) throws ChannelConnectorException {
           
        String host = "smtp.gmail.com";
        String pass = "abby@021";
        String from = "scyankee2007@gmail.com";
        Properties props = System.getProperties();
        props.put("mail.smtp.starttls.enable", "true"); // added this line
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.user", from );
        props.put("mail.smtp.password", pass);
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");


        try {
            Session session = Session.getDefaultInstance(props, null);
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));

            InternetAddress[] toAddress = new InternetAddress[to.size()];

            // To get the array of addresses
            for( int i=0; i < to.size(); i++ ) {
                toAddress[i] = new InternetAddress((String) to.get(i));
            }

            for( int i=0; i < toAddress.length; i++) {
                message.addRecipient(Message.RecipientType.TO, toAddress[i]);
            }
            message.setSubject( subject );
            message.setText( msg );
            Transport transport = session.getTransport("smtp");
            transport.connect(host, from, pass);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            
        }
        catch( Exception e ) {
            logger.log(Level.SEVERE, "EmailConnector:sendEmail: ", e.getMessage() );
            throw new ChannelConnectorException( "EmailConnector:sendMessage", e);
        }
        return SUCCESS;
    }

    @Override
    public Integer sendEvent( List to, EventEntity event) throws ChannelConnectorException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * sendMessage - sends the message via email
     * @param to - List of email addresses to which to send the data
     * @param msg - the message to send
     * @return SUCCESS if all goes well and FAIL if something is wrong
     * @throws ChannelConnectorException 
     */
    @Override
    public Integer sendMessage(List to, MessageEntity msg) throws ChannelConnectorException {
        
        logger.log( Level.INFO, "Inside EmailConnector:sendMessage");
        return sendEmail( to, msg.getTitle(), msg.getText() );
    }
    
}
