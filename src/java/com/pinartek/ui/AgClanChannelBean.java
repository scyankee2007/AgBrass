/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pinartek.ui;

import com.corejsf.util.Messages;
import com.pinartek.dbentities.Channeltype;
import com.pinartek.dbentities.ClanChannelEntity;
import com.pinartek.dbentities.DbEntityException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 *
 * @author Family
 */
@Named(value = "agClanChannelBean")
@SessionScoped
public class AgClanChannelBean implements Serializable {

    static final Logger logger = Logger.getLogger("com.pinartek.ui");
    static final String TRYAGAIN = "";
    static final String REGISTER = "register";
    static final String LOGIN = "login";
    static final String SUCCESS = "success";
    static final String FAIL = null;
    static final String EDIT = null;
    static final String ALL_STATUS = "ALL";

    
    /** Creates a new instance of AgClanChannelBean */
    public AgClanChannelBean() {
    }

    @Inject private AgClanManagerBean theClan;
    
    private Integer idClanChannel = null;
    private Integer idChannelType = null;
    private Channeltype channelType = null;
    private List channelTypes = null;
    private String electronicAddress1 = null;
    private String electronicAddress2 = null;
    private String password = null;
    private Timestamp timeCreated = null;
    private Timestamp timeModified = null;

    private List clanChannels = null;
    private ClanChannelEntity selectedClanChannel = null;
    
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

    public List getClanChannels() {
        if( clanChannels == null ) loadClanChannels();
        return clanChannels;
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
    
    public ClanChannelEntity getSelectedChannel() {
        return this.selectedClanChannel;
    }

    public void setSelectedChannel( ClanChannelEntity cce ) {
        this.selectedClanChannel = cce;
        loadBean( cce );
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
     * loadChannelTypes - reads all of the possible channel types from the database.
     */
    private void loadChannelTypes() {
        
        if( this.channelTypes != null ) this.channelTypes.clear();

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("AgBrassPU");
        EntityManager em = emf.createEntityManager();
        Query q = em.createNamedQuery("Channeltype.findAll");
        this.channelTypes = q.getResultList();
        
    }

    /**
     * loadBean - Set this bean to the MessageEntity passed in
     * 
     * @param cce - ClanChannelEntity
     */
    private void loadBean( ClanChannelEntity cce ) {
       
        /*
         * Check to ensure that my current idClan matches the db clan passed in
         */
        
        if( this.theClan.getIdClan().intValue() != cce.getIdClan().intValue() ) {
            logger.log(Level.SEVERE, "agClanChannelBean:loadBean - ClanChannelEntity clan does not match this bean idClan" );
            logger.log(Level.SEVERE, "agClanChannelBean:loadBean - theClan:clanId is {0}", this.theClan.getIdClan() );
            logger.log(Level.SEVERE, "agClanChannelBean:loadBean - me:clanId is {0}", cce.getIdClan() );
        }
        
        logger.log(Level.INFO, "agClanChannelBean:loadBean - This message was loaded: {0}", cce.getElectronicAddress1() );

        this.idClanChannel = cce.getIdClanChannel();
        this.idChannelType = cce.getIdChannelType();
        this.channelType = cce.getChannelType();
        this.electronicAddress1 = cce.getElectronicAddress1();
        this.electronicAddress2 = cce.getElectronicAddress2();
        this.password = cce.getPassword();
        this.timeCreated = cce.getTimeCreated();
        this.timeModified = cce.getTimeModified();
}
    /**
     * loadMessages - loads the messages for the clan this bean serves
     */
    private void loadClanChannels() {

        logger.log(Level.INFO, "agClanChannelBean:loadClanChannels - Inside" );
        
        if( theClan == null ) {
            logger.log(Level.SEVERE, "agClanChannelBean:loadClanChannels - clan did not get injected" );
        }

        if( clanChannels != null ) clanChannels.clear();
        
        ClanChannelEntity cce = new ClanChannelEntity();
        cce.setIdClan( theClan.getIdClan() );
        try {
            clanChannels = cce.findList( null );
        }
        catch( DbEntityException e ) {
            logger.log(Level.SEVERE, "agClanChannelBean:loadClanChannels - {0}", e.getMessage() );
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
    public void saveClanChannel() {
        
        ClanChannelEntity cce = new ClanChannelEntity();

        cce.setIdClan( theClan.getIdClan() );
        cce.setElectronicAddress1( this.electronicAddress1 );
        cce.setElectronicAddress2( this.electronicAddress2);
        cce.setIdChannelType( this.idChannelType);
        cce.setPassword(this.password);
        logger.log(Level.INFO, "agClanChannelBean:SaveClanChannel - Entered Save Clan Channel" );
        
        /*
         * If the idMessage is set for this item, then we update
         * the existing message in the database.  Otherwise, we insert
         * the fresh one
         */
        
        if( idClanChannel == null ) {
            logger.log(Level.INFO, "agClanChannelBean:SaveClanChannel - Doing an Insert" );
            try {
                if( cce.insert() != ClanChannelEntity.SUCCESS ) {
                   addErrorMessage( Messages.getMessage("com.pinartek.ui.messages", "clanchannel_insert_error", null ));
                   logger.log(Level.SEVERE, "agClanChannelBean:SaveClanChannelAction - Failed to add clan channel for some reason" );
                   return;
                }
                // After the insert, ensure that the new messageId gets copied to the worker bean
                this.idClanChannel = cce.getIdClanChannel();
            }
            catch( DbEntityException e ) {
               addErrorMessage( Messages.getMessage("com.pinartek.ui.messages", "clanchannel_insert_error", null ));
               logger.log(Level.SEVERE, "agClanChannelBean:SaveClanChannelAction - {0}", e.getMessage() );
               return;
            }
        }
        else {
            logger.log(Level.INFO, "agClanChannelBean:SaveClanChannel - Doing an update" );
            try {
                if( cce.update( this.idClanChannel ) != ClanChannelEntity.SUCCESS ) {
                   addErrorMessage( Messages.getMessage("com.pinartek.ui.messages", "clanchannel_update_error", null ));
                   logger.log(Level.SEVERE, "agClanChannelBean:SaveMessageAction - Failed to edit message for some reason" );
                   return;
                }
            }
            catch( DbEntityException e ) {
               addErrorMessage( Messages.getMessage("com.pinartek.ui.messages", "clanchannel_update_error", null ));
               logger.log(Level.SEVERE, "agClanChannelBean:SaveMessageAction - {0}", e.getMessage() );
               return;
            }
        }
        
        // Reload the message list since we have a new one
        loadClanChannels();
        newClanChannel();
        addErrorMessage( Messages.getMessage("com.pinartek.ui.messages", "message_save_success", null ));
        return;
    }
    
    /**
     * editClanChannel - Sets this current bean to the selected ClanChannelEntity
     * and returns the edit message
     * 
     * @param me
     * @return 
     */
    public void editClanChannel( ClanChannelEntity cce ) {
        logger.log( Level.INFO, "Inside editClanChannel" );
        loadBean( cce );        
        return;
    }


    /**
     * selectClanChannel - Sets the current selection to the passed in clan channel
     * We need this to use dialog box confirmation.
     * 
     * @param me 
     */
    public void selectClanChannel( ClanChannelEntity cce ) {
        logger.log( Level.INFO, "Inside selectClanChannel" );
        this.selectedClanChannel = cce;
        return;
    }
    
    /**
     * deleteClanChannel - this will delete a clan's channel.  
     * 
     */
    public void deleteClanChannel() {
    
        logger.log( Level.INFO, "Inside deleteClanChannel" );
        if( this.selectedClanChannel == null ) {
           addErrorMessage( Messages.getMessage("com.pinartek.ui.messages", "clanchannel_delete_error", null ));
           logger.log(Level.SEVERE, "agClanChannelBean:deleteClanChannel- selecteClanChannel is null NOT GOOD" );
        }

        try {
            selectedClanChannel.removeOne();
        }
        catch( Exception e ) {
           addErrorMessage( Messages.getMessage("com.pinartek.ui.messages", "clanchannel_delete_error", null ));
           logger.log(Level.SEVERE, "agClanChannelBean:deleteClanChannel- {0}", e.getMessage() );
        }
        loadClanChannels();
        newClanChannel();
        return;
    }

    /**
     * newClanChannel - Resets the bean values because the user wants
     * to create a new channel for this clan
     */
    public void newClanChannel() {
        logger.log( Level.INFO, "Inside newClanChannel" );
        ClanChannelEntity newClanChannel = new ClanChannelEntity();
        newClanChannel.setIdClan( this.theClan.getIdClan());
        loadBean( newClanChannel );
    }

}
