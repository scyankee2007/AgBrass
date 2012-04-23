/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pinartek.dbentities;

import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.ArrayList;
import java.sql.Statement;
import java.util.Date;

/**
 *
 * @author Family
 */
public class EventEntity extends EntityTemplate {

    private Integer idEvent = null;
    private String name = null;
    private Timestamp startTime = null;
    private Timestamp endTime = null;
    private String location = null;
    private String status = null;
    private String text = null;
    private Timestamp timeCreated = null;
    private Timestamp timeModified = null;
    private Integer idClan = null;
    
    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
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

    public static final String PUBLISHED_STATUS = "PUBLISHED_EVENT";
    public static final String PENDING_STATUS = "PENDING_EVENT";
    
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

    public Integer getIdClan() {
        return idClan;
    }

    public void setIdClan(Integer idClan) {
        this.idClan = idClan;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
    
    
    public static final String ALL_FUTURE_CLAN_EVENTS = "WHERE_ALL_FUTURE_EVENTS";
    public static final String ALL_CLAN_EVENTS = "WHERE_ALL_EVENTS";
    public static final String PUBLISHED_CLAN_EVENTS = "WHERE_PUBLISHED_EVENTS";
    public static final String PENDING_CLAN_EVENTS = "WHERE_PENDING_EVENTS";
    
    private static final String ALL_CLAN_EVENT_SQL = "select idEvent, name, location, startTime, endTime, text, status, timeCreated, timeModified, "
            + "Clan_idClan from silvertrumpets.event "
            + "where Clan_idClan = ?";
    
    private static final String ALL_FUTURE_CLAN_EVENTS_SQL = "select idEvent, name, location, startTime, endTime, text, status, timeCreated, timeModified, "
            + "Clan_idClan from silvertrumpets.event "
            + "where Clan_idClan = ? "
            + "and endTime > now()";    

    private static final String PUBLISHED_CLAN_EVENTS_SQL = "select idEvent, name, location, startTime, endTime, text, status, timeCreated, timeModified, "
            + "Clan_idClan from silvertrumpets.event "
            + "where Clan_idClan = ? and status = '" + PUBLISHED_STATUS + "' "
            + "and endTime > now()";    
            
    private static final String PENDING_CLAN_EVENTS_SQL = "select idEvent, name, location, startTime, endTime, text, status, timeCreated, timeModified, "
            + "Clan_idClan from silvertrumpets.event "
            + "where Clan_idClan = ? and status = '" + PENDING_STATUS + "' "
            + "and endTime > now()";    
    
    
    @Override
    public List<EntityTemplate> findList(String where) throws DbEntityException {
        String sql = "";
        
        if( this.idClan == null ) {
            logger.severe("EventEntity:findList: idClan is null which is bad");
            return null;
        }

        if( where.equalsIgnoreCase(ALL_CLAN_EVENTS)) {
            logger.log( Level.INFO, "EventEntity:findList called for ALL_CLAN_EVENTS query" );
            sql = ALL_CLAN_EVENT_SQL;
        }
        else if( where.equalsIgnoreCase( ALL_FUTURE_CLAN_EVENTS ) ) {
            logger.log( Level.INFO, "EventEntity:findList called for FUTURE_EVENT query" );
            sql = ALL_FUTURE_CLAN_EVENTS_SQL;
        }
        else if( where.equalsIgnoreCase( PUBLISHED_CLAN_EVENTS ) ) {
            logger.log( Level.INFO, "EventEntity:findList called for PUBLISHED query" );
            sql = PUBLISHED_CLAN_EVENTS_SQL;
        }
        else if( where.equalsIgnoreCase( PENDING_CLAN_EVENTS ) ) {
            logger.log( Level.INFO, "EventEntity:findList called for PENDING query" );
            sql = PENDING_CLAN_EVENTS_SQL;
        }
        
        List theList = new ArrayList();
        
        Connection aConnection = null;
        ResultSet rs = null;
        PreparedStatement stmt = null;
        try {
            
            aConnection = getConnection();
            stmt = aConnection.prepareStatement(sql);
            stmt.setInt(1, this.idClan.intValue() );
            logger.log( Level.INFO, stmt.toString() );
            rs = stmt.executeQuery();
            while ( rs != null && rs.next() ) {
                EventEntity anEvent = new EventEntity();
                anEvent.setIdClan( rs.getInt("Clan_idClan"));
                anEvent.setIdEvent( rs.getInt("idEvent"));
                anEvent.setStatus( rs.getString("status"));
                anEvent.setName( rs.getString("name"));
                anEvent.setLocation( rs.getString("location"));
                anEvent.setText( rs.getString("text"));
                anEvent.setStartTime( rs.getTimestamp("startTime"));
                anEvent.setEndTime( rs.getTimestamp("endTime"));
                anEvent.setTimeCreated( rs.getTimestamp("timeCreated") );
                anEvent.setTimeModified( rs.getTimestamp("timeModified") );
                
                theList.add( anEvent );
                logger.log( Level.FINE, "Found an Event");
            }

            return (List<EntityTemplate>) theList;
        }
        catch( SQLException e ) {
            logger.log(Level.SEVERE, "EventEntity:findList - fetch failed:{0}", e.getMessage());
            throw new DbEntityException( "EventEntity:findList - fetch failed", e );
        }
        finally {
            try {            
                logger.log( Level.INFO, "Closing jdbc objects in EventEntity" );
                if( rs != null ) rs.close();
                if( stmt != null ) stmt.close();
                if( aConnection != null ) aConnection.close();
            }
            catch( SQLException e ) {
                logger.severe("EventEntity:findList failed to close connection");
                throw new DbEntityException( "EventEntity:findList failed to close connection", e );
            }
        }
    }

    @Override
    public Integer findOne(Integer key) throws DbEntityException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private static final String INSERT_SQL = "insert into silvertrumpets.event" 
            + "( name, location, startTime, endTime, text, status, Clan_idClan ) "
            + "values(?,?,?,?,?,?,?)";

    
    /**
     * insert - Adds an Event to the database
     * @return
     * @throws DbEntityException 
     */
    @Override
    public Integer insert() throws DbEntityException {
        logger.log( Level.INFO, "EventEntity insert called" );

        if( this.idClan == null ) {
            logger.severe("EventEntity:insert: idClan is null which is bad");
            return FAIL;
        }
        
        Connection aConnection = null;
        
        
        this.setIdEvent(ZERO);
        this.setStatus(PENDING_STATUS);
        try {
            
            aConnection = getConnection();
            PreparedStatement stmt = aConnection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
            stmt.setString( 1, this.name );
            stmt.setString( 2, this.location );
            stmt.setTimestamp( 3, this.startTime );
            stmt.setTimestamp( 4, this.endTime );
            stmt.setString( 5, this.text );
            stmt.setString( 6, this.status );
            stmt.setInt( 7, this.idClan );
            
            // Note that timeCreated and timeModified are set by triggers in the database
            stmt.execute();
            
            // Now, fetch the row we just inserted and grab the idMessage generated
            // by the database and add this to Clan_has_User table
            
            int key = -1;
            ResultSet rs = stmt.getGeneratedKeys();
            if( rs.next() ) {
                key = rs.getInt(1);
                this.idEvent = new Integer( key );
            }
            
            stmt.close();
           
        }
        catch( SQLException e ) {
            logger.log(Level.SEVERE, "EventEntity:insert - insert failed:{0}", e.getMessage());
            throw new DbEntityException( "EventEntity:insert - insert failed", e );
        }
        finally {
            try {            
                if( aConnection != null ) aConnection.close();
            }
            catch( SQLException e ) {
                logger.severe("EventEntity:insert failed to close connection");
                throw new DbEntityException( "EventEntity:insert failed to close connection", e );
            }
        }
        return SUCCESS;
    }

     private static final String DELETE_MESSAGE_SQL = "delete from silvertrumpets.event "
       + "where idEvent = ?";
   
    
    /**
     * removeOne - This will evaluate the message.  If the message
     * has already been publish, it will expire the message with the
     * current time.  If the message has not yet been published, it
     * will remove the message from the database.
     * 
     * @return SUCCESS if the operation succeeds
     *         FAIL if we have a problem
     * 
     * @throws DbEntityException 
     */
    @Override
    public Integer removeOne() throws DbEntityException {
     
        // Check to see if the message is published yet
        if( this.status.equalsIgnoreCase(PUBLISHED_STATUS) ) {
            Date now = new Date();
            this.endTime = new Timestamp( now.getTime() );
            update( this.getIdEvent() );
        }
        else {
            // Do the work
            Connection aConnection = null;
            PreparedStatement stmt = null;
            int ret;
            try {
                aConnection = getConnection();
                stmt = aConnection.prepareStatement( DELETE_MESSAGE_SQL );
                stmt.setInt( 1, this.getIdEvent() );
                stmt.execute();
                
                logger.log(Level.INFO, "EventEntity:removeOne - delete did not throw an exception" );
                return SUCCESS;
            }
            catch( SQLException e ) {
                logger.log(Level.SEVERE, "EventEntity:removeOne - delete update failed:{0}", e.getMessage());
                throw new DbEntityException( "EventEntity:removeOne - delete failed", e );
            }
            finally {
                try {            
                    logger.log( Level.INFO, "Closing jdbc objects in EventEntity" );
                    if( stmt != null ) stmt.close();
                    if( aConnection != null ) aConnection.close();
                }
                catch( SQLException e ) {
                    logger.severe("MessageEntity:removeOne: failed to close connection");
                    throw new DbEntityException( "EventEntity:removeOne failed to close connection", e );
                }
            }

        }
        
        return SUCCESS;
    }
    
    
    private static final String UPDATE_EVENT_SQL = "update silvertrumpets.event "
       + "set name = ?, location = ?, startTime = ?, endTime = ?, text = ?, status = ?, Clan_idClan = ? "
       + "where idEvent = ?";


    /**
     * update - Updates the record in the database where
     * idEvent is the Key
     * 
     * @param key
     * @return
     * @throws DbEntityException 
     */
    @Override
    public Integer update(Integer key) throws DbEntityException {
        
        logger.log(Level.INFO, "EventEntity:update - Inside update" );

        // Check to see that this object contains something connected to the
        // database.
        if( key == null ) {
            logger.log(Level.SEVERE, "EventEntity:update - idEvent not set" );
            return FAIL;
        }

        // Do the work
        Connection aConnection = null;
        PreparedStatement stmt = null;
        int ret;
        try {
            aConnection = getConnection();
            stmt = aConnection.prepareStatement( UPDATE_EVENT_SQL );
            stmt.setString( 1, this.getName() );
            stmt.setString( 2, this.getLocation() );
            stmt.setTimestamp( 3, this.getStartTime() );
            stmt.setTimestamp( 4, this.getEndTime() );
            stmt.setString( 5, this.getText() );
            stmt.setString( 6, this.getStatus() );
            stmt.setInt( 7, this.getIdClan() );
            stmt.setInt( 8, key );

            if( (ret = stmt.executeUpdate()) < 1 ) {
                logger.log(Level.SEVERE, "EventEntity:update - update returned zero rows {0}", ret );
                return FAIL;
            }
            
            logger.log(Level.INFO, "EventEntity:update - update modified {0} rows", ret );
            return SUCCESS;
        }
        catch( SQLException e ) {
            logger.log(Level.SEVERE, "EventEntity:update - update failed:{0}", e.getMessage());
            throw new DbEntityException( "EventEntity:update - update failed", e );
        }
        finally {
            try {            
                logger.log( Level.INFO, "Closing jdbc objects in EventEntity" );
                if( stmt != null ) stmt.close();
                if( aConnection != null ) aConnection.close();
            }
            catch( SQLException e ) {
                logger.severe("EventEntity:update: failed to close connection");
                throw new DbEntityException( "EventEntity:update failed to close connection", e );
            }
        }
    }
}
