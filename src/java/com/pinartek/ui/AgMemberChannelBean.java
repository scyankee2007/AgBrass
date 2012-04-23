/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pinartek.ui;

import com.corejsf.util.Messages;
import com.pinartek.dbentities.Channeltype;
import com.pinartek.dbentities.ClanChannelEntity;
import com.pinartek.dbentities.MemberChannelEntity;
import com.pinartek.dbentities.DbEntityException;
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

/**
 *
 * @author Family
 */
@Named(value = "agMemberChannelBean")
@SessionScoped
public class AgMemberChannelBean implements Serializable {

    static final Logger logger = Logger.getLogger("com.pinartek.ui");
    static final String TRYAGAIN = "";
    static final String REGISTER = "register";
    static final String LOGIN = "login";
    static final String SUCCESS = "success";
    static final String FAIL = null;
    static final String EDIT = null;
    static final String ALL_STATUS = "ALL";

    @Inject private AgUserBean theUser;
    @Inject private AgClanManagerBean theClan;
    
    private Integer idMemberChannel = null;
    private Integer idChannelType = null;
    private Channeltype channelType = null;
    private List channelTypes = null;
    private String electronicAddress1 = null;
    private String electronicAddress2 = null;
    private String password = null;
    private Timestamp timeCreated = null;
    private Timestamp timeModified = null;

    private List memberChannels = null;
    private MemberChannelEntity selectedMemberChannel = null;
    
    public String getElectronicAddress1() {
        return electronicAddress1;
    }

    public void setElectronicAddress1(String electronicAddress1) {
        this.electronicAddress1 = electronicAddress1;
    }

    public String getElectronicAddress2() {
        return electronicAddress2;
    }

    public void setElectronicAddress2(String electronicAddress2) {
        this.electronicAddress2 = electronicAddress2;
    }

    public Integer getIdChannelType() {
        return idChannelType;
    }

    public void setIdChannelType( Integer idChannelType ) {
        if( idChannelType != null ) {
            this.idChannelType = idChannelType;
        }
    }
    
    public Channeltype getChannelType() {
        return this.channelType;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List getMemberChannels() {
        if( memberChannels == null ) loadMemberChannels();
        return memberChannels;
    }
    
    public List getChannelTypes() {
        if( this.channelTypes == null ) loadChannelTypes();
        return this.channelTypes;
    }
    
    public boolean getShowSaveBtn() {
        if( electronicAddress1 != null ) {
            String trimmed = electronicAddress1.trim();
            if( trimmed.length() > 0 )             
                return true;
        }
        return false;
    }

    
    /**
     * loadChannelTypes - reads all of the possible channel types from the database.
     */
    private void loadChannelTypes() {
        
        if( this.channelTypes != null ) 
            this.channelTypes.clear();
        else
            this.channelTypes = new ArrayList();

        /*
         * Find the channels that this clan supports and allow these types for this member
         */
        
        ClanChannelEntity cce = new ClanChannelEntity();
        cce.setIdClan( theClan.getIdClan() );
        
        try {
            List ccl = cce.findList(null);
            for( int i = 0; i < ccl.size(); i++ ) {
                ClanChannelEntity ce = (ClanChannelEntity) ccl.get(i);
                Channeltype aType = ce.loadChannelType( ce.getIdChannelType() );
                this.channelTypes.add( aType );
            }
        }
        catch( DbEntityException e ) {
           addErrorMessage( Messages.getMessage("com.pinartek.ui.messages", "technical_error", null ));
           logger.log(Level.SEVERE, "agMemberChannelBean:loadChannelTypes - Failed to load channel types" );
           return;
        }
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
     * @param mce - MemberChannelEntity
     */
    private void loadBean( MemberChannelEntity mce ) {
       
        /*
         * Check to ensure that my current idMember matches the db memberChannel passed in
         */
        
        if( this.theUser.getIdMember().intValue() != mce.getIdMember().intValue() ) {
            logger.log(Level.SEVERE, "agMemberChannelBean:loadBean - MemberChannelEntity member does not match this bean idMember" );
            logger.log(Level.SEVERE, "agMemberChannelBean:loadBean - theUser:idMember is {0}", this.theUser.getIdMember() );
            logger.log(Level.SEVERE, "agMemberChannelBean:loadBean - mce:idMember is {0}", mce.getIdMember() );
        }
        
        logger.log(Level.INFO, "agMemberChannelBean:loadBean - This message was loaded: {0}", mce.getElectronicAddress1() );

        this.idMemberChannel = mce.getIdMemberChannel();
        this.idChannelType = mce.getIdChannelType();
        this.channelType = mce.getChannelType();
        this.electronicAddress1 = mce.getElectronicAddress1();
        this.electronicAddress2 = mce.getElectronicAddress2();
        this.password = mce.getPassword();
        this.timeCreated = mce.getTimeCreated();
        this.timeModified = mce.getTimeModified();
}
    /**
     * loadMessages - loads the channels for the member this bean serves
     */
    private void loadMemberChannels() {

        logger.log(Level.INFO, "agMemberChannelBean:loadMemberChannels - Inside" );
        
        if( theUser == null ) {
            logger.log(Level.SEVERE, "agMemberChannelBean:loadMemberChannels - theUser did not get injected" );
        }

        if( memberChannels != null ) memberChannels.clear();
        
        MemberChannelEntity mce = new MemberChannelEntity();
        mce.setIdMember( theUser.getIdMember() );
        try {
            memberChannels = mce.findList( null );
        }
        catch( DbEntityException e ) {
            logger.log(Level.SEVERE, "agMemberChannelBean:loadMemberChannels - {0}", e.getMessage() );
        }
    }

    /**
     * saveMemberChannel - Called by the UI to save the member channel selection in 
     * this bean to the database.
     * 
     * @return  SUmceSS when successful
     *          FAIL when we have trouble - The trouble is stored
     * in the MESSAGE construct.
     */
    public void saveMemberChannel() {
        
        MemberChannelEntity mce = new MemberChannelEntity();

        mce.setIdMember( theUser.getIdMember() );
        mce.setElectronicAddress1( this.electronicAddress1 );
        mce.setElectronicAddress2( this.electronicAddress2);
        mce.setIdChannelType( this.idChannelType);
        mce.setPassword(this.password);
        logger.log(Level.INFO, "agMemberChannelBean:SaveMemberChannel - Entered Save Member Channel" );
        
        /*
         * If the idMessage is set for this item, then we update
         * the existing message in the database.  Otherwise, we insert
         * the fresh one
         */
        
        if( idMemberChannel == null ) {
            logger.log(Level.INFO, "agMemberChannelBean:SaveMemberChannel - Doing an Insert" );
            try {
                if( mce.insert() != MemberChannelEntity.SUCCESS ) {
                   addErrorMessage( Messages.getMessage("com.pinartek.ui.messages", "memberchannel_insert_error", null ));
                   logger.log(Level.SEVERE, "agMemberChannelBean:SaveMemberChannel - Failed to add member channel for some reason" );
                   return;
                }
                // After the insert, ensure that the new messageId gets copied to the worker bean
                this.idMemberChannel = mce.getIdMemberChannel();
            }
            catch( DbEntityException e ) {
               addErrorMessage( Messages.getMessage("com.pinartek.ui.messages", "memberchannel_insert_error", null ));
               logger.log(Level.SEVERE, "agMemberChannelBean:SaveMemberChannel - {0}", e.getMessage() );
               return;
            }
        }
        else {
            logger.log(Level.INFO, "agMemberChannelBean:SaveMemberChannel - Doing an update" );
            try {
                if( mce.update( this.idMemberChannel ) != MemberChannelEntity.SUCCESS ) {
                   addErrorMessage( Messages.getMessage("com.pinartek.ui.messages", "memberchannel_update_error", null ));
                   logger.log(Level.SEVERE, "agMemberChannelBean:SaveMemberChannel - Failed to edit message for some reason" );
                   return;
                }
            }
            catch( DbEntityException e ) {
               addErrorMessage( Messages.getMessage("com.pinartek.ui.messages", "memberchannel_update_error", null ));
               logger.log(Level.SEVERE, "agMemberChannelBean:SaveMemberChannel - {0}", e.getMessage() );
               return;
            }
        }
        
        // Reload the message list since we have a new one
        loadMemberChannels();
        newMemberChannel();
        addErrorMessage( Messages.getMessage("com.pinartek.ui.messages", "memberchannel_save_success", null ));
        return;
    }
    
    /**
     * editMemberChannel - Sets this current bean to the selected MemberChannelEntity
     * and returns the edit message
     * 
     * @param me
     * @return 
     */
    public void editMemberChannel( MemberChannelEntity mce ) {
        logger.log( Level.INFO, "Inside editMemberChannel" );
        loadBean( mce );        
        return;
    }


    /**
     * selectMemberChannel - Sets the current selection to the passed in clan channel
     * We need this to use dialog box confirmation.
     * 
     * @param me 
     */
    public void selectMemberChannel( MemberChannelEntity mce ) {
        logger.log( Level.INFO, "Inside selectMemberChannel" );
        this.selectedMemberChannel = mce;
        return;
    }
    
    /**
     * deleteMemberChannel - this will delete a member's channel.  
     * 
     */
    public void deleteMemberChannel() {
    
        logger.log( Level.INFO, "Inside deleteMemberChannel" );
        if( this.selectedMemberChannel == null ) {
           addErrorMessage( Messages.getMessage("com.pinartek.ui.messages", "memberchannel_delete_error", null ));
           logger.log(Level.SEVERE, "agMemberChannelBean:deleteMemberChannel- selectedMemberChannel is null NOT GOOD" );
        }

        try {
            selectedMemberChannel.removeOne();
        }
        catch( Exception e ) {
           addErrorMessage( Messages.getMessage("com.pinartek.ui.messages", "memberchannel_delete_error", null ));
           logger.log(Level.SEVERE, "agMemberChannelBean:deleteMemberChannel- {0}", e.getMessage() );
        }
        loadMemberChannels();
        newMemberChannel();
        return;
    }

    /**
     * newMemberChannel - Resets the bean values because the user wants
     * to create a new channel for this member
     */
    public void newMemberChannel() {
        logger.log( Level.INFO, "Inside newMemberChannel" );
        MemberChannelEntity newMemberChannel = new MemberChannelEntity();
        newMemberChannel.setIdMember( this.theUser.getIdMember());
        loadBean( newMemberChannel );
    }
}
