/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pinartek.ui;

import com.corejsf.util.Messages;
import com.pinartek.dbentities.DbEntityException;
import com.pinartek.dbentities.EventEntity;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
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
@Named(value = "agEventBean")
@SessionScoped
public class AgEventBean implements Serializable {

    static final Logger logger = Logger.getLogger("com.pinartek.ui");
    static final String TRYAGAIN = "";
    static final String REGISTER = "register";
    static final String LOGIN = "login";
    static final String SUCCESS = "success";
    static final String FAIL = null;
    static final String EDIT = null;
    static final String ALL_STATUS = "ALL";
    
    @Inject private AgClanManagerBean theClanManager;
    
    private Integer idEvent = null;
    
    @NotNull( message="{com.pinartek.eventNameError}") 
    @Size( min=1, message="{com.pinartek.eventNameError}")
    private String name = "";
    
    private String location = "";
    
    private String status = EventEntity.PENDING_STATUS;
    private String text = "";

    @NotNull( message="{com.pinartek.eventStartTimeError}")
    private Date startTime = null;
    
    @NotNull( message="{com.pinartek.eventEndTimeError}")
    private Date endTime = null;
    
    private Timestamp timeCreated = null;
    private Timestamp timeModified = null;
    private String listType = EventEntity.PENDING_STATUS;
    private EventEntity selectedEvent = null;

    private List eventList = null;

    /**
     * getShowSaveBtn - If the event doesn't have a name, don't save it
     * 
     * @return true if the name is not blank
     */
    public boolean getShowSaveBtn() {
        if( name != null ) {
            String trimmed = name.trim();
            if( trimmed.length() > 0 )             
                return true;
        }
        return false;
    
    }

    /**
     * getShowPublishBtn - If you can save the event and the status
     * is still PENDING, show PUBLISH Button
     * 
     * @return  true if you can save the event and status is still pending
     */
    public boolean getShowPublishBtn() {        
        if( getShowSaveBtn() ) {          
            if( status != null && status.equalsIgnoreCase( EventEntity.PENDING_STATUS )) {
                logger.log(Level.FINE, "agEventBean:getShowPublishBtn - status is {0} ", status );
                return true;
            }
        }
        logger.log(Level.FINE, "agEventBean:getShowPublishBtn - status is {0} ", status );
        return false;
    }
    
    
    public String getPendingStatus() {
        return EventEntity.PENDING_STATUS;
    }
    
    public String getAllStatus() {
        return ALL_STATUS;
    }
    
    public String getListType() {
        return this.listType;
    }
    
    public void setListType( String listType ) {
        this.listType = listType;
        loadEvents();
    }
    
    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public List getEventList() {
        if( eventList == null ) loadEvents();
        return eventList;
    }

    public Integer getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(Integer idEvent) {
        this.idEvent = idEvent;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    
    /** Creates a new instance of AgEventBean */
    public AgEventBean() {
    }
    
    /**
     * addErrorMessage - Adds and error message to the FacesContext
     * 
     */
    private void addErrorMessage( FacesMessage error  ) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage( null , error );
    }

    
    /**
     * loadEvents - loads the events for the clan this bean serves
     */
    private void loadEvents() {

        logger.log(Level.INFO, "agEventBean:loadEvents - Inside" );
        
        if( theClanManager.getIdClan() == null ) {
            logger.log(Level.SEVERE, "agEventBean:loadEvents - idClan is null" );
        }

        if( eventList != null ) eventList.clear();
        
        EventEntity ee = new EventEntity();
        ee.setIdClan( theClanManager.getIdClan() );
        try {
            if( listType.equalsIgnoreCase(EventEntity.PENDING_STATUS) ) {
                eventList = ee.findList( EventEntity.PENDING_CLAN_EVENTS );
            }
            else {
                eventList = ee.findList( EventEntity.ALL_FUTURE_CLAN_EVENTS );
            }
        }
        catch( DbEntityException e ) {
            logger.log(Level.SEVERE, "agEventBean:loadEvents - {0}", e.getMessage() );
        }

    }
    
        /**
     * loadBean - Set this bean to the EventEntity passed in
     * 
     * @param me 
     */
    private void loadBean( EventEntity ee ) {
       
        /*
         * Check to ensure that my current idClan matches the db clan passed in
         */
        
        if( theClanManager.getIdClan() != ee.getIdClan() ) {
            logger.log(Level.SEVERE, "agEventBean:loadBean - EventEntity clan does not match this bean idClan" );
        }
        
        this.name = ee.getName();
        this.text = ee.getText();
        this.location = ee.getLocation();
        this.status = ee.getStatus();
        this.startTime = ee.getStartTime();
        this.endTime = ee.getEndTime();
        this.timeCreated = ee.getTimeCreated();
        this.timeModified = ee.getTimeModified();
        this.idEvent = ee.getIdEvent();
    }

    /**
     * saveEventAction - processes the save button by saving the
     * data in the bean in the database.  If the event already has an idEvent
     * then we update the current record.  If there is no idEvent yet, we insert
     * the record for the first time
     * @return 
     */
    public void saveEvent() {
        
        logger.log(Level.INFO, "agEventBean:saveEvent - Inside Save Event" );
        
        EventEntity ee = new EventEntity();

        ee.setIdClan( theClanManager.getIdClan() );
        ee.setStatus( this.status );
        ee.setText( this.text );
        ee.setLocation( this.location );
        ee.setName( this.name );
        ee.setIdEvent( this.idEvent );
        ee.setStartTime( new Timestamp( this.startTime.getTime()) );
        ee.setEndTime( new Timestamp( this.endTime.getTime()) );
        
        /*
         * If the idEvent is set for this item, then we update
         * the existing event in the database.  Otherwise, we insert
         * the fresh one
         */
        
        if( idEvent == null ) {
            try {
                if( ee.insert() != EventEntity.SUCCESS ) {
                   addErrorMessage( Messages.getMessage("com.pinartek.ui.messages", "event_insert_error", null ));
                   logger.log(Level.SEVERE, "agEventBean:SaveEventAction - Failed to add event for some reason" );
                   return;
                }
            }
            catch( DbEntityException e ) {
               addErrorMessage( Messages.getMessage("com.pinartek.ui.messages", "event_insert_error", null ));
               logger.log(Level.SEVERE, "agEventBean:SaveEventAction - {0}", e.getMessage() );
            }
        }
        else {
            try {
                if( ee.update( this.idEvent ) != EventEntity.SUCCESS ) {
                   addErrorMessage( Messages.getMessage("com.pinartek.ui.messages", "event_update_error", null ));
                   logger.log(Level.SEVERE, "agEventBean:SaveEventAction - Failed to edit event for some reason" );
                   return;
                }
            }
            catch( DbEntityException e ) {
               addErrorMessage( Messages.getMessage("com.pinartek.ui.messages", "event_update_error", null ));
               logger.log(Level.SEVERE, "agEventBean:SaveEvent - {0}", e.getMessage() );
            }
        }
        
        // Reload the event list since we have a new one
        loadEvents();
        newEvent();
        addErrorMessage( Messages.getMessage("com.pinartek.ui.messages", "event_save_success", null ));
        return;
    }
    
    /**
     * publishEvent - publishes this event and sends it out to the members.
     * 
     * @return 
     */
    public void publishEvent() {
        logger.log(Level.INFO, "agEventBean:publishEvent - Inside Publish Event" );
        this.status = EventEntity.PUBLISHED_STATUS;
        saveEvent();
        return;
    }


    /**
     * newEvent - Resets the bean values because the user wants
     * to create a new event
     */
    public void newEvent() {
        logger.log( Level.INFO, "Inside newEvent" );
        EventEntity newEvent = new EventEntity();
        newEvent.setIdClan( this.theClanManager.getIdClan());
        loadBean( newEvent );
    }
    
    /**
     * selectEvent - Sets the current selection to the passed in event.
     * We need this to use dialog box confirmation.
     * 
     * @param me 
     */
    public void selectEvent( EventEntity ee ) {
        logger.log( Level.INFO, "Inside selectEvent" );
        this.selectedEvent = ee;
        loadBean( ee );
        return;
    }

    
    /**
     * deleteEvent - this will delete an event.  If the event
     * is published, it will expire the event instead of removing
     * if from the database.
     * 
     * @param me 
     */
    public void deleteEvent() {
    
        logger.log( Level.INFO, "Inside deleteEvent" );
        if( this.selectedEvent == null ) {
           addErrorMessage( Messages.getMessage("com.pinartek.ui.messages", "event_delete_error", null ));
           logger.log(Level.SEVERE, "agEventBean:deleteEvent- selectedEvent is null NOT GOOD" );
           return;
        }

        try {
            selectedEvent.removeOne();
        }
        catch( Exception e ) {
           addErrorMessage( Messages.getMessage("com.pinartek.ui.messages", "event_delete_error", null ));
           logger.log(Level.SEVERE, "agEventBean:deleteEvent - {0}", e.getMessage() );
        }
        loadEvents();
        newEvent();
        return;
    }

}
