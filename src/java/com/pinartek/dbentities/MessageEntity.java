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
 * Database class for getting messages from the system
 * 
 * @author Family
 */
public class MessageEntity extends EntityTemplate {
    
    private Integer idMessage = null;
    private Integer idClan = null;
    private String title = "";
    private String text = "";
    private String status = PENDING_STATUS;
    private Timestamp expired = null;
    private Timestamp timeCreated = null;
    private Timestamp timeModified = null;

    public Timestamp getExpired() {
        return expired;
    }

    public void setExpired(Timestamp expired) {
        this.expired = expired;
    }

    public Integer getIdClan() {
        return idClan;
    }

    public void setIdClan(Integer idClan) {
        this.idClan = idClan;
    }

    public Integer getIdMessage() {
        return idMessage;
    }

    public void setIdMessage(Integer idMessage) {
        this.idMessage = idMessage;
    }

    public static final String PUBLISHED_STATUS = "PUBLISHED";
    public static final String PENDING_STATUS = "PENDING" ;
    
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

    
    public static final String ALL_ACTIVE_CLAN_MESSAGES = "WHERE_ALL_ACTIVE_MESSAGES";
    public static final String ALL_CLAN_MESSAGES = "WHERE_ALL_MESSAGES";
    public static final String PUBLISHED_CLAN_MESSAGES = "WHERE_PUBLISHED_MESSAGES";
    public static final String PENDING_CLAN_MESSAGES = "WHERE_PENDING_MESSAGES";
    
    private static final String ALL_CLAN_MESSAGES_SQL = "select idMessage, title, text, status, expired, timeCreated, timeModified, "
            + "Clan_idClan from silvertrumpets.message "
            + "where Clan_idClan = ? "
            + "order by title";
    
    private static final String ALL_ACTIVE_CLAN_MESSAGES_SQL = "select idMessage, title, text, status, expired, timeCreated, timeModified, "
            + "Clan_idClan from silvertrumpets.message "
            + "where Clan_idClan = ? "
            + "and (expired is null or expired > now()) "
            + "order by title";    

    private static final String PUBLISHED_CLAN_MESSAGES_SQL = "select idMessage, title, text, status, expired, timeCreated, timeModified, "
            + "Clan_idClan from silvertrumpets.message "
            + "where Clan_idClan = ? and status = '" + PUBLISHED_STATUS + "' "
            + "and (expired is null or expired > now()) "
            + "order by title";    
            
    private static final String PENDING_CLAN_MESSAGES_SQL = "select idMessage, title, text, status, expired, timeCreated, timeModified, "
            + "Clan_idClan from silvertrumpets.message "
            + "where Clan_idClan = ? and status = '" + PENDING_STATUS + "' "
            + "and (expired is null or expired > now()) "
            + "order by title";    

    /**
     * findList - Grabs an ArrayList of Messages based upon the where clause parameter passed in.
     * 
     * @param where
     * @return
     * @throws DbEntityException 
     */
    @Override
    public List<EntityTemplate> findList(String where) throws DbEntityException {

        String sql = "";
        
        if( this.idClan == null ) {
            logger.severe("MessageEntity:findList: idClan is null which is bad");
            return null;
        }

        if( where.equalsIgnoreCase(ALL_CLAN_MESSAGES)) {
            logger.log( Level.INFO, "MessageEntity:findList called for ALL_CLAN_MESSAGES query" );
            sql = ALL_CLAN_MESSAGES_SQL;
        }
        else if( where.equalsIgnoreCase( ALL_ACTIVE_CLAN_MESSAGES ) ) {
            logger.log( Level.INFO, "MessageEntity:findList called for ACTIVE query" );
            sql = ALL_ACTIVE_CLAN_MESSAGES_SQL;
        }
        else if( where.equalsIgnoreCase( PUBLISHED_CLAN_MESSAGES ) ) {
            logger.log( Level.INFO, "MessageEntity:findList called for PUBLISHED query" );
            sql = PUBLISHED_CLAN_MESSAGES_SQL;
        }
        else if( where.equalsIgnoreCase( PENDING_CLAN_MESSAGES ) ) {
            logger.log( Level.INFO, "MessageEntity:findList called for PENDING query" );
            sql = PENDING_CLAN_MESSAGES_SQL;
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
                MessageEntity theMessage = new MessageEntity();
                theMessage.setIdClan( rs.getInt("Clan_idClan"));
                theMessage.setIdMessage( rs.getInt("idMessage"));
                theMessage.setStatus( rs.getString("status"));
                theMessage.setText( rs.getString("text"));
                theMessage.setTitle( rs.getString("title"));
                theMessage.setExpired( rs.getTimestamp("expired"));
                theMessage.setTimeCreated( rs.getTimestamp("timeCreated") );
                theMessage.setTimeModified( rs.getTimestamp("timeModified") );
                
                theList.add( theMessage );
                logger.log( Level.FINE, "Found a Message");
            }

            return (List<EntityTemplate>) theList;
        }
        catch( SQLException e ) {
            logger.log(Level.SEVERE, "MessageEntity:findList - fetch failed:{0}", e.getMessage());
            throw new DbEntityException( "MessageEntity:findList - fetch failed", e );
        }
        finally {
            try {            
                logger.log( Level.INFO, "Closing jdbc objects in MessageEntity" );
                if( rs != null ) rs.close();
                if( stmt != null ) stmt.close();
                if( aConnection != null ) aConnection.close();
            }
            catch( SQLException e ) {
                logger.severe("MessageEntity:findList failed to close connection");
                throw new DbEntityException( "MessageEntity:findList failed to close connection", e );
            }
        }
    }

    @Override
    public Integer findOne(Integer key) throws DbEntityException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private static final String INSERT_SQL = "insert into silvertrumpets.message" 
            + "( title, text, status, expired, Clan_idClan ) "
            + "values(?,?,?,?,?)";
    
    /**
     * insert - Adds a Message to the database
     * 
     * @return
     * @throws DbEntityException 
     */
    @Override
    public Integer insert() throws DbEntityException {
        logger.log( Level.INFO, "MessageEntity insert called" );

        if( this.idClan == null ) {
            logger.severe("MessageEntity:insert: idClan is null which is bad");
            return FAIL;
        }
        
        Connection aConnection = null;
        
        
        this.setIdMessage(ZERO);
        this.setStatus(PENDING_STATUS);
        try {
            
            aConnection = getConnection();
            PreparedStatement stmt = aConnection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
            stmt.setString( 1, this.title );
            stmt.setString( 2, this.text );
            stmt.setString( 3, this.status );
            stmt.setTimestamp( 4, expired);
            stmt.setInt( 5, this.idClan );
            
            // Note that timeCreated and timeModified are set by triggers in the database
            stmt.execute();
            
            // Now, fetch the row we just inserted and grab the idMessage generated
            // by the database and add this to Clan_has_User table
            
            int key = -1;
            ResultSet rs = stmt.getGeneratedKeys();
            if( rs.next() ) {
                key = rs.getInt(1);
                this.idMessage = new Integer( key );
            }
            
            stmt.close();
           
        }
        catch( SQLException e ) {
            logger.log(Level.SEVERE, "MessageEntity:insert - insert failed:{0}", e.getMessage());
            throw new DbEntityException( "MessageEntity:insert - insert failed", e );
        }
        finally {
            try {            
                if( aConnection != null ) aConnection.close();
            }
            catch( SQLException e ) {
                logger.severe("MessageEntity:insert failed to close connection");
                throw new DbEntityException( "MessageEntity:insert failed to close connection", e );
            }
        }
        return SUCCESS;
    }

    
    private static final String DELETE_MESSAGE_SQL = "delete from silvertrumpets.message "
       + "where idMessage = ?";

    
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
            this.expired = new Timestamp( now.getTime() );
            update( this.getIdMessage() );
        }
        else {
            // Do the work
            Connection aConnection = null;
            PreparedStatement stmt = null;
            int ret;
            try {
                aConnection = getConnection();
                stmt = aConnection.prepareStatement( DELETE_MESSAGE_SQL );
                stmt.setInt( 1, this.getIdMessage() );
                stmt.execute();
                
                logger.log(Level.INFO, "MessageEntity:removeOne - delete did not throw an exception" );
                return SUCCESS;
            }
            catch( SQLException e ) {
                logger.log(Level.SEVERE, "MessageEntity:removeOne - delete update failed:{0}", e.getMessage());
                throw new DbEntityException( "MessageEntity:removeOne - delete failed", e );
            }
            finally {
                try {            
                    logger.log( Level.INFO, "Closing jdbc objects in MessageEntity" );
                    if( stmt != null ) stmt.close();
                    if( aConnection != null ) aConnection.close();
                }
                catch( SQLException e ) {
                    logger.severe("MessageEntity:removeOne: failed to close connection");
                    throw new DbEntityException( "MessageEntity:removeOne failed to close connection", e );
                }
            }

        }
        
        return SUCCESS;
    }

    private static final String UPDATE_MESSAGE_SQL = "update silvertrumpets.message "
       + "set title = ?, text = ?, status = ?, expired = ?, Clan_idClan = ? "
       + "where idMessage = ?";

    
    
    /**
     * update - Updates the record in the database where idMessage
     * is the key
     * 
     * @param key
     * @return
     * @throws DbEntityException 
     */
    @Override
    public Integer update(Integer key) throws DbEntityException {

        logger.log(Level.INFO, "MessageEntity:update - Inside update" );

        // Check to see that this object contains something connected to the
        // database.
        if( key == null ) {
            logger.log(Level.SEVERE, "MessageEntity:update - idMessage not set" );
            return FAIL;
        }

        // Do the work
        Connection aConnection = null;
        PreparedStatement stmt = null;
        int ret;
        try {
            aConnection = getConnection();
            stmt = aConnection.prepareStatement( UPDATE_MESSAGE_SQL );
            stmt.setString( 1, this.getTitle() );
            stmt.setString( 2, this.getText() );
            stmt.setString( 3, this.getStatus() );
            stmt.setTimestamp( 4, this.getExpired() );
            stmt.setInt( 5, this.getIdClan() );
            stmt.setInt( 6, key );

            if( (ret = stmt.executeUpdate()) < 1 ) {
                logger.log(Level.SEVERE, "MessageEntity:update - update returned zero rows {0}", ret );
                return FAIL;
            }
            
            logger.log(Level.INFO, "MessageEntity:update - update modified {0} rows", ret );
            return SUCCESS;
        }
        catch( SQLException e ) {
            logger.log(Level.SEVERE, "MessageEntity:update - update failed:{0}", e.getMessage());
            throw new DbEntityException( "MessageEntity:update - update failed", e );
        }
        finally {
            try {            
                logger.log( Level.INFO, "Closing jdbc objects in MessageEntity" );
                if( stmt != null ) stmt.close();
                if( aConnection != null ) aConnection.close();
            }
            catch( SQLException e ) {
                logger.severe("MessageEntity:update: failed to close connection");
                throw new DbEntityException( "MessageEntity:update failed to close connection", e );
            }
        }
    }
}
