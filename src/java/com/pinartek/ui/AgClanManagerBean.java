/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pinartek.ui;

import com.corejsf.util.Messages;
import com.pinartek.dbentities.ClanEntity;
import com.pinartek.dbentities.DbEntityException;
import com.pinartek.dbentities.EntityTemplate;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import org.primefaces.component.tabview.Tab;
import org.primefaces.component.tabview.TabView;
import org.primefaces.event.TabChangeEvent;


/**
 *
 * @author Family
 */
@Named(value = "agClanManagerBean")
@SessionScoped
public class AgClanManagerBean implements Serializable {
    
    
    static final Logger logger = Logger.getLogger("com.pinartek.ui");
    static final String BACK = "back";
    static final String SUCCESS = "success";
    static final String FAIL = null;
    static final String EDITMANAGED = "editmanagedclan";
    static final String EDITMEMBER = "editmemberclan";
        
    @NotNull( message="{com.pinartek.clanNameError}") 
    @Size( min=1, message="{com.pinartek.ClanNameError}" )
    private String  name = "";
    
    @NotNull( message="{com.pinartek.address1NameError}") 
    @Size( min=1, message="{com.pinartek.address1NameError}" )
    private String  address1 = "";
    
    private String  address2 = "";
    
    @NotNull( message="{com.pinartek.cityError}") 
    @Size( min=1, message="{com.pinartek.cityError}" )
    private String  city = "";

    @NotNull( message="{com.pinartek.stateError}") 
    @Size( min=2, max=2, message="{com.pinartek.stateError}" )
    private String  state = "";
    
    @NotNull( message="{com.pinartek.zipError}") 
    @Size( min=5, max=5, message="{com.pinartek.zipError}" )
    private String  zip = "";
    
    private Integer idClan = null;
    private String status = null;
    
    @NotNull( message="{com.pinartek.secretCodeError}") 
    private String  secretCode = "";

    @Inject private AgUserBean theUser;

    private List managedClans = null;
    private List memberClans = null;

   
    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    
    public List<ClanEntity> getManagedClans() {

        logger.log(Level.FINE, "inside getManagedClans" );

        if( managedClans == null ) {
            
            /*
             * Ensure we have a user
             */
            if( theUser == null ) {
                logger.log(Level.SEVERE, "The user is null - Dependance injection sucks" );
            }
            /*
             * We decided to load the clans from the database each time for now
             * Will have to fix someday when we get a lot of use.
             */
            if( !loadManagedClans() ) {
                logger.log(Level.INFO, "User {0} doesn''t have any clans to manage", theUser.getIdUser() );
            }
        }
    
        return managedClans;
    }

    public List<ClanEntity> getMemberClans() {

        logger.log(Level.FINE, "inside getMemberClans" );
        
        if( memberClans == null ) {
            
            /*
             * Ensure we have a user
             */
            if( theUser == null ) {
                logger.log(Level.SEVERE, "The user is null - Dependance injection sucks" );
            }

            // Once the user is select, grab this users managed clans
            if( !loadMemberClans() ) {
                logger.log(Level.INFO, "Member {0} isn''t a part of any clans", theUser.getIdMember() );
            }
        }

        return memberClans;
    }

    public Integer getIdClan() {
        return idClan;
    }

    
    public String getStatus() {
        return status;
    }
    
    public void setStatus( String status ) {
        this.status = status;
    }

    public String getSecretCode() {
        return secretCode;
    }

    public void setSecretCode(String secretCode) {
        this.secretCode = secretCode;
    }

    public AgClanManagerBean() {
        logger.log(Level.INFO, "inside agClanManagerBean Constructor" );
    }

    /**
     * Sets the clan passed in to the data in this bean.
     * 
     * @param clan 
     */
    public void setClan( ClanEntity clan ) {
        idClan = clan.getIdClan();
        address1 = clan.getAddress1();
        address2 = clan.getAddress2();
        city = clan.getCity();
        name = clan.getName();
        state = clan.getState();
        zip = clan.getZip();
        status = clan.getStatus();
        secretCode = clan.getSecretCode();
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
     * loadMemberClans - fetches the clans this user is a member of and
     * loads them into this bean
     * 
     * @return true if it successfully loaded the member clans for this user.
     *              it is important to note that NO clans is a valid state
     *         false if there was a problem.  
     */
    public boolean loadMemberClans() {

        logger.log(Level.FINE, "inside loadMemberClans" );
        
        if( memberClans != null ) {
            memberClans.clear();
        }
        
        ClanEntity theClan = new ClanEntity();
        theClan.setIdMember( theUser.getIdMember() );
        theClan.setIdUser(null);
        
        try {
            memberClans = theClan.findList(ClanEntity.MEMBER_QUERY);
        }
        catch( Exception e ) {
            logger.log( Level.SEVERE, "Inside loadMemberClans", e );
            return false;
        }
        return true;
    }
    
    /**
     * loadManagedClans - fetches the clans this user is a manager of and
     * loads them into this bean
     * 
     * @return true if it successfully loaded the managed clans for this user.
     *              it is important to note that NO clans is a valid state
     *         false if there was a problem.  
     */
    public boolean loadManagedClans() {
        
        logger.log(Level.FINE, "inside loadManagedClans" );

        if( this.managedClans != null ) {
            managedClans.clear();
        }
        
        ClanEntity theClan = new ClanEntity();
        theClan.setIdUser( theUser.getIdUser() );
        theClan.setIdMember(null);
        
        try {
            managedClans = theClan.findList(ClanEntity.MANAGER_QUERY);
        }
        catch( Exception e ) {
            logger.log( Level.SEVERE, "Inside loadManagedClans", e );
            return false;
        }
        return true;
    }
    
    
    /**
     * clear - clears out this bean and returns back so the
     * user can navigate to the calling page.
     * @return 
     */
    public String clear() {

        idClan = null;
        name = "";
        address1 = "";
        address2 = "";
        state = "";
        zip = "";
        status = "";
        secretCode = "";
        
        if( managedClans != null ) {
            managedClans.clear();
            managedClans = null;
        }
        
        if( memberClans != null ) {
            memberClans.clear();
            memberClans = null;
        }
        return BACK;
    }
    
    /**
     * addManagedClanAction - Takes the data in the bean and inserts
     * it into the database.  If successful, proclaim success and
     * allow the configured UI send us to the right place.  If not
     * success, put a message on the MESSAGE mechanism and display that.
     * 
     * @return 
     */
    public String addManagedClanAction() {
       
        logger.log( Level.INFO, "Inside addManagedClanAction" );
        
        ClanEntity theClan = new ClanEntity();
        
        theClan.setIdUser( theUser.getIdUser() );
        theClan.setName( this.name );
        theClan.setAddress1( this.address1 );
        theClan.setAddress2( this.address2 );
        theClan.setCity( this.city );
        theClan.setState( this.state );
        theClan.setZip( this.zip );
        theClan.setSecretCode( this.secretCode );
       
        try {
            theClan.insert();
            idClan = theClan.getIdClan();
            loadManagedClans();
            return SUCCESS;
        }
        catch( Exception e ) {
            logger.log(Level.SEVERE, e.getMessage() );
        }

        addErrorMessage( Messages.getMessage("com.pinartek.ui.messages", "add_managed_clan_insert_error", null ));
        return FAIL;
    }

    
    /**
     * removeManagedClan - Remove the selected component from the
     * managed clan list.  Remove means mark the clan as inactive
     * in the database.
     * 
     * @param clan - ClanEntity which should enable me to find the
     * selected clan in the list

     */
    public String removeManagedClan( ClanEntity clan ) {
        
        logger.log( Level.INFO, "Inside removeManagedClan" );
        if( clan == null ) {
            addErrorMessage( Messages.getMessage("com.pinartek.ui.messages", "techical_error", null ));
            return FAIL;
        }
        
        try {
            if( clan.removeFromManagedClans() == EntityTemplate.SUCCESS ) {
                this.clear();
                this.loadManagedClans();
                return SUCCESS;
            }
            else
                return FAIL;
        }
        catch( DbEntityException e ) {
            addErrorMessage( Messages.getMessage("com.pinartek.ui.messages", "techical_error", null ));
            return FAIL;
        }
    }

    /**
     * removeMemberClan - Remove the selected component from the
     * member clan list.  Remove means mark the clan as inactive
     * in the database.
     * 
     * @param clan - ClanEntity - Object selected
     */
    public void removeMemberClan( ClanEntity clan ) {
        
        logger.log( Level.INFO, "Inside removeMemberClan" );
        if( clan == null ) {
            addErrorMessage( Messages.getMessage("com.pinartek.ui.messages", "techical_error", null ));
            return;
        }
        
        try {
            clan.removeFromMemberClans();
            this.loadMemberClans();
        }
        catch( Exception e ) {
            addErrorMessage( Messages.getMessage("com.pinartek.ui.messages", "techical_error", null ));
        }
        
        return;
    }

    
    
    /**
     * editManagedClan - Takes the clan passed in and sets this bean
     * equal to this that clan data and if successful, passes out a 
     * success instruction
     * 
     * @param clan
     * @return EDIT - If we loaded the clan successfully
     *         FAIL for some reason
     */
    public String editManagedClan( ClanEntity clan ) {
        
        logger.log( Level.INFO, "Inside editManagedClan" );
        if( clan == null ) {
            addErrorMessage( Messages.getMessage("com.pinartek.ui.messages", "techical_error", null ));
            return FAIL;
        }
 
        this.setClan( clan );
        
        return EDITMANAGED;
    }
    
    /**
     * saveManagedClan - Saves the newly entered data in this bean in the
     * current object.
     * 
     * @return  SUCCESS on successful save
     *          FAIL on some failure
     */
    public String saveManagedClan() {
        
        logger.log( Level.INFO, "Inside saveManagedClan" );

        /*
         *  Make sure that the data in the bean are appropriate to save
         */

        if( this.idClan == null ) {
            logger.log( Level.INFO, "idClan is null inside saveManagedClan" );
            addErrorMessage( Messages.getMessage("com.pinartek.ui.messages", "technical_error", null ));
            return FAIL;
        }
        
        ClanEntity theClan = new ClanEntity();
        
        theClan.setIdUser( theUser.getIdUser() );
        theClan.setIdClan( this.idClan );
        theClan.setIdMember( theUser.getIdMember() );
        theClan.setName( this.name );
        theClan.setAddress1( this.address1 );
        theClan.setAddress2( this.address2 );
        theClan.setCity( this.city );
        theClan.setState( this.state );
        theClan.setZip( this.zip );
        theClan.setStatus( this.getStatus() );
        theClan.setSecretCode( this.secretCode );
        try {
            theClan.update( this.idClan );
            logger.log( Level.INFO, "Saved clan successfully" );
        }
        catch( Exception e ) {
            logger.log(Level.SEVERE, e.getMessage() );
            addErrorMessage( Messages.getMessage("com.pinartek.ui.messages", "technical_error", null ));
            return FAIL;
        }
        
        addErrorMessage( Messages.getMessage("com.pinartek.ui.messages", "edit_managed_clan_save_success", null ));
        return SUCCESS;
    }
    
    /**
     * createSecretCode - creates the secretCode, sets it in the bean
     * and asks the UI to render it again.
     * @param e 
     */
    public void createSecretCode( ActionEvent e ) {
        logger.log( Level.INFO, "Inside agClanManagerBean:createSecretCode" );
        UUID uuidSecretCode = UUID.randomUUID();
        this.secretCode = uuidSecretCode.toString();
        logger.log( Level.INFO, "The secretCode is {0}", this.secretCode );
    }
    
    /**
     * joinClanActionListener - grabs the value in secretCode and
     * looks it up to see if we can join this clan.
     * 
     * @param e 
     */
    public void joinClanActionListener( ActionEvent event ) {
       
        logger.log( Level.INFO, "Inside agClanManagerBean:joinClanActionListener" );
        

        // Check to see if the secret code is entered
        if( this.secretCode == null ) {
            addErrorMessage( Messages.getMessage("com.pinartek.ui.messages", "add_member_clan_secretCode_error", null ));
            return;
        }

        // See if you can find the secret code entered
        
        ClanEntity ce = new ClanEntity();
        ce.setSecretCode( this.secretCode );
        try {
            if( ce.findOne( ClanEntity.FIND_BY_SECRET_CODE ) == ClanEntity.SUCCESS ) {
                
                // We found the clan, add this person to this clan as a member
                ce.setIdMember( theUser.getIdMember() );
                if( ce.addClanMember() == ClanEntity.SUCCESS ) {
                    logger.log( Level.INFO, "agClanManagerBean:joinClanActionListener - Member added to clan" );
                    addErrorMessage( Messages.getMessage("com.pinartek.ui.messages", "add_member_clan_success", null ));
                }
                else {
                    logger.log( Level.INFO, "agClanManagerBean:joinClanActionListener - Member FAILED to add to clan" );
                    addErrorMessage( Messages.getMessage("com.pinartek.ui.messages", "technical_error", null ));
                }
    
                this.loadMemberClans();
                return;
            }
        }
        catch( Exception e ) {
            logger.log( Level.INFO, "agClanManagerBean:joinClanActionListener - Exception: ", e.getMessage() );
            addErrorMessage( Messages.getMessage("com.pinartek.ui.messages", "technical_error", null ));
        }
    }

    /**
     * editMemberClan - Takes the clan passed in and sets this bean
     * equal to this that clan data and if successful, passes out a 
     * success instruction
     * 
     * @param clan
     * @return EDIT - If we loaded the clan successfully
     *         FAIL for some reason
     */
    public String editMemberClan( ClanEntity clan ) {
        
        logger.log( Level.INFO, "Inside editMemberClan" );
        if( clan == null ) {
            addErrorMessage( Messages.getMessage("com.pinartek.ui.messages", "techical_error", null ));
            return FAIL;
        }
 
        this.setClan( clan );
        
        return EDITMEMBER;
    }

}
