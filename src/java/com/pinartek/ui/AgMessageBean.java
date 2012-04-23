/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pinartek.ui;

import com.corejsf.util.Messages;
import com.pinartek.channel.EmailConnector;
import com.pinartek.dbentities.Channeltype;
import com.pinartek.dbentities.DbEntityException;
import com.pinartek.dbentities.MemberChannelEntity;
import com.pinartek.dbentities.MessageEntity;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Family
 */
@Named(value = "agMessageBean")
@SessionScoped
public class AgMessageBean implements Serializable {

    static final Logger logger = Logger.getLogger("com.pinartek.ui");
    static final String TRYAGAIN = "";
    static final String REGISTER = "register";
    static final String LOGIN = "login";
    static final String SUCCESS = "success";
    static final String FAIL = null;
    static final String EDIT = null;
    static final String ALL_STATUS = "ALL";
    static final String EMAIL_TYPE = "EMAIL";

    @Inject private AgClanManagerBean theClan;
    @Inject private AgMemberChannelBean theMembers;
    
    private Integer idMessage = null;
    
    @NotNull( message="{com.pinartek.messageTitleError}")
    @Size( min=1, message="{com.pinartek.messageTitleError}" )
    private String title = "";
    
    @NotNull( message="{com.pinartek.messageTextError")
    @Size( min=1, message="{com.pinartek.messageTextError}" )
    private String text = "";
    
    private String status = MessageEntity.PENDING_STATUS;
    private Timestamp expired = null;
    private Timestamp timeCreated = null;
    private Timestamp timeModified = null;
    private List messageList = null;
    private String listType = MessageEntity.PENDING_STATUS;
    private MessageEntity selectedMessage = null;
    
    
    public boolean getShowSaveBtn() {
        if( title != null ) {
            String trimmedTitle = title.trim();
            if( trimmedTitle.length() > 0 )             
                return true;
        }
        return false;
    }

    public boolean getShowPublishBtn() {        
        if( getShowSaveBtn() ) {          
            if( status != null && status.equalsIgnoreCase( MessageEntity.PENDING_STATUS )) {
                return true;
            }
        }
        return false;
    }

    public String getPendingStatus() {
        return MessageEntity.PENDING_STATUS;
    }

    public String getAllStatus() {
        return ALL_STATUS;
    }
   
    public String getListType() {
        return listType;
    }
    
    public void setListType( String listType ) {
        this.listType = listType;
        loadMessages();
    }
    
    public Timestamp getExpired() {
        return expired;
    }

    public void setExpired(Timestamp expired) {
        this.expired = expired;
    }

    public Integer getIdMessage() {
        return idMessage;
    }

    public void setIdMessage(Integer idMessage) {
        this.idMessage = idMessage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Timestamp getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Timestamp timeCreated) {
        this.timeCreated = timeCreated;
    }

    public Timestamp getTimeModified() {
        return timeModified;
    }

    public void setTimeModified(Timestamp timeModified) {
        this.timeModified = timeModified;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List getMessageList() {
        if( messageList == null ) loadMessages();
        return messageList;
    }

    /** Creates a new instance of AgMessageBean */
    public AgMessageBean() {
    }
    

    /**
     * addErrorMessage - Adds and error message to the FacesContext
     * 
     * @param summary - Summary of the error message
     * @param detail  - Detail of the error message
     */
    private void addErrorMessage( FacesMessage error  ) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage( null , error );
    }

    
    /**
     * loadBean - Set this bean to the MessageEntity passed in
     * 
     * @param me 
     */
    private void loadBean( MessageEntity me ) {
       
        /*
         * Check to ensure that my current idClan matches the db clan passed in
         */
        
        if( this.theClan.getIdClan().intValue() != me.getIdClan().intValue() ) {
            logger.log(Level.SEVERE, "agMessageBean:loadBean - MessageEntity clan does not match this bean idClan" );
            logger.log(Level.SEVERE, "agMessageBean:loadBean - theClan:clanId is {0}", this.theClan.getIdClan() );
            logger.log(Level.SEVERE, "agMessageBean:loadBean - me:clanId is {0}", me.getIdClan() );
            
        }
        
        
        logger.log(Level.INFO, "agMessageBean:loadBean - This message was loaded: {0}", me.getTitle() );
        this.title = me.getTitle();
        this.text = me.getText();
        this.status = me.getStatus();
        this.expired = me.getExpired();
        this.timeCreated = me.getTimeCreated();
        this.timeModified = me.getTimeModified();
        this.idMessage = me.getIdMessage();
    }
    /**
     * loadMessages - loads the messages for the clan this bean serves
     */
    private void loadMessages() {

        logger.log(Level.INFO, "agMessageBean:loadMessages - Inside" );
        
        if( theClan == null ) {
            logger.log(Level.SEVERE, "agMessageBean:loadMessages - clan did not get injected" );
        }

        if( messageList != null ) messageList.clear();
        
        MessageEntity me = new MessageEntity();
        me.setIdClan( theClan.getIdClan() );
        try {
            if( listType.equalsIgnoreCase( MessageEntity.PENDING_STATUS )) {
                messageList = me.findList( MessageEntity.PENDING_CLAN_MESSAGES );
            }
            else {
                messageList = me.findList( MessageEntity.ALL_ACTIVE_CLAN_MESSAGES );
            }
        }
        catch( DbEntityException e ) {
            logger.log(Level.SEVERE, "agMessageBean:loadMessages - {0}", e.getMessage() );
        }
    }

    /**
     * saveMessageAction - Called by the UI to save the message in 
     * this bean to the database.
     * 
     * @return  SUCCESS when successful
     *          FAIL when we have trouble - The trouble is stored
     * in the MESSAGE construct.
     */
    public void saveMessage() {
        
        MessageEntity me = new MessageEntity();

        me.setIdClan( theClan.getIdClan() );
        me.setStatus( this.status );
        me.setText( this.text );
        me.setTitle( this.title );
        me.setIdMessage( this.idMessage);
        me.setExpired( this.expired );
        
        /*
         * If the idMessage is set for this item, then we update
         * the existing message in the database.  Otherwise, we insert
         * the fresh one
         */
        
        if( idMessage == null ) {
            try {
                if( me.insert() != MessageEntity.SUCCESS ) {
                   addErrorMessage( Messages.getMessage("com.pinartek.ui.messages", "message_insert_error", null ));
                   logger.log(Level.SEVERE, "agMessageBean:SaveMessageAction - Failed to add message for some reason" );
                   return;
                }
                // After the insert, ensure that the new messageId gets copied to the worker bean
                this.idMessage = me.getIdMessage();
            }
            catch( DbEntityException e ) {
               addErrorMessage( Messages.getMessage("com.pinartek.ui.messages", "message_insert_error", null ));
               logger.log(Level.SEVERE, "agMessageBean:SaveMessageAction - {0}", e.getMessage() );
            }
        }
        else {
            try {
                if( me.update( this.idMessage ) != MessageEntity.SUCCESS ) {
                   addErrorMessage( Messages.getMessage("com.pinartek.ui.messages", "message_update_error", null ));
                   logger.log(Level.SEVERE, "agMessageBean:SaveMessageAction - Failed to edit message for some reason" );
                   return;
                }
            }
            catch( DbEntityException e ) {
               addErrorMessage( Messages.getMessage("com.pinartek.ui.messages", "message_update_error", null ));
               logger.log(Level.SEVERE, "agMessageBean:SaveMessageAction - {0}", e.getMessage() );
            }
        }
        
        // Reload the message list since we have a new one
        loadMessages();
        newMessage();
        addErrorMessage( Messages.getMessage("com.pinartek.ui.messages", "message_save_success", null ));
        return;
    }
        
    /**
     * publishMessageAction - publishes this message and sends it out to the members.
     * 
     * @return 
     */
    public void publishMessage() {
        
        logger.log(Level.INFO, "agMessageBean:publishMessage - Inside Publish Message" );
        
        ArrayList toEmails = new ArrayList();
        
        List memberChannels = theMembers.getMemberChannels();
        for( int i = 0; i < memberChannels.size(); i++ ) {
            MemberChannelEntity aChannel = (MemberChannelEntity) memberChannels.get(i);
            Channeltype aType = aChannel.getChannelType();
            if( aType.getName() != null && aType.getName().equalsIgnoreCase(EMAIL_TYPE) ) {
                toEmails.add( aChannel.getElectronicAddress1());
            }
            else {
               addErrorMessage( Messages.getMessage("com.pinartek.ui.messages", "message_send_error", null ));
               logger.log(Level.SEVERE, "agMessageBean:publishMessage - Failed to support a channel type in publishMessage" );
               return;
            }
        }
        

        try {
            if( !toEmails.isEmpty() ) {
                logger.log(Level.INFO, "agMessageBean:publishMessage - Working on Emails" );
                EmailConnector ec = new EmailConnector();
                if( selectedMessage != null ) {
                    if( ec.sendMessage( toEmails, selectedMessage) == EmailConnector.FAIL ) {
                        addErrorMessage( Messages.getMessage("com.pinartek.ui.messages", "message_send_error", null ));
                        logger.log(Level.SEVERE, "agMessageBean:publishMessage - Failed to send an email message"  );
                        return;
                    }
                }
                else {
                    addErrorMessage( Messages.getMessage("com.pinartek.ui.messages", "message_send_error", null ));
                    logger.log(Level.SEVERE, "agMessageBean:publishMessage - selectedMessage is null - BAD"  );
                    return;
                }
            }
        }
        catch( Exception e ) {
               addErrorMessage( Messages.getMessage("com.pinartek.ui.messages", "message_send_error", null ));
               logger.log(Level.SEVERE, "agMessageBean:publishMessage - Failed to send an email message {0}",  e.getMessage()  );
               return;
            
        }
            
        this.status = MessageEntity.PUBLISHED_STATUS;
        saveMessage();
        return;
    }

    /**
     * selectMessage - Sets the current selection to the passed in message.
     * We need this to use dialog box confirmation.
     * 
     * @param me 
     */
    public void selectMessage( MessageEntity me ) {
        logger.log( Level.INFO, "Inside selectMessage" );
        this.selectedMessage = me;
        loadBean( me );
        return;
    }
    
    /**
     * deleteMessage - this will delete a message.  If the message
     * is published, it will expire the message instead of removing
     * if from the database.
     * 
     * @param me 
     */
    public void deleteMessage() {
    
        logger.log( Level.INFO, "Inside deleteMessage" );
        if( this.selectedMessage == null ) {
           addErrorMessage( Messages.getMessage("com.pinartek.ui.messages", "message_delete_error", null ));
           logger.log(Level.SEVERE, "agMessageBean:deleteMessage- selectedMessage is null NOT GOOD" );
        }

        try {
            selectedMessage.removeOne();
        }
        catch( Exception e ) {
           addErrorMessage( Messages.getMessage("com.pinartek.ui.messages", "message_delete_error", null ));
           logger.log(Level.SEVERE, "agMessageBean:deleteMessage- {0}", e.getMessage() );
        }
        loadMessages();
        newMessage();
        return;
    }

    /**
     * newMessage - Resets the bean values because the user wants
     * to create a new message
     */
    public void newMessage() {
        logger.log( Level.INFO, "Inside newMessage" );
        MessageEntity newMsg = new MessageEntity();
        newMsg.setIdClan( this.theClan.getIdClan());
        loadBean( newMsg );
    }
}
