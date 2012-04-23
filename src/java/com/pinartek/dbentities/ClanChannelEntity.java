/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pinartek.dbentities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 *
 * @author Family
 */
public class ClanChannelEntity extends EntityTemplate {

    private Integer idClanChannel = null;
    private Integer idClan = null;
    private Integer idChannelType = null;
    private String electronicAddress1 = null;
    private String electronicAddress2 = null;
    private String password = null;
    private Timestamp timeCreated = null;
    private Timestamp timeModified = null;

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

    public void setIdChannelType(Integer idChannelType) {
        this.idChannelType = idChannelType;
    }

    public Integer getIdClan() {
        return idClan;
    }

    public void setIdClan(Integer idClan) {
        this.idClan = idClan;
    }

    public Integer getIdClanChannel() {
        return idClanChannel;
    }

    public void setIdClanChannel(Integer idClanChannel) {
        this.idClanChannel = idClanChannel;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
    
    /**
     * loadChannelType - loads the type object from the id
     * 
     * @param idChannelType
     * @return 
     */
    public Channeltype loadChannelType( Integer idChannelType ) {
        
        if( idChannelType == null ) {
            logger.log(Level.SEVERE, "agClanChannelBean:loadChannel - Channeltype passed in is NULL - BAD THING" );
            return null;
        }
        
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("AgBrassPU");
        EntityManager em = emf.createEntityManager();
        Query q = em.createNamedQuery("Channeltype.findByIdChannelType");
        q.setParameter("idChannelType", idChannelType );
        Channeltype ct = (Channeltype) q.getSingleResult();
        return ct;
    }
    
    public Channeltype getChannelType() {
        return loadChannelType( idChannelType );
    }

    private static final String CLAN_CHANNELS_SQL = "select idClanChannel, Clan_idClan, ChannelType_idChannelType, electronicAddress1, "
           + "electronicAddress2, password, timeCreated, timeModified "
           + "from silvertrumpets.ClanChannel "
           + "where Clan_idClan = ?";

    
    @Override
    public List<EntityTemplate> findList(String where) throws DbEntityException {
        String sql = CLAN_CHANNELS_SQL;
        
        if( this.idClan == null ) {
            logger.severe("ClanChannelEntity:findList: idClan is null which is bad");
            return null;
        }


        if( where != null ) {
            logger.severe("ClanChannelEntity:findList: where is NOT null - this should not be");
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
                ClanChannelEntity mc = new ClanChannelEntity();
                mc.setIdClanChannel( rs.getInt("idClanChannel"));
                mc.setIdClan( rs.getInt("Clan_idClan"));
                mc.setIdChannelType( rs.getInt("ChannelType_idChannelType"));
                mc.setElectronicAddress1( rs.getString("electronicAddress1"));
                mc.setElectronicAddress2( rs.getString("electronicAddress2"));
                mc.setPassword( rs.getString("password"));
                mc.setTimeCreated( rs.getTimestamp("timeCreated") );
                mc.setTimeModified( rs.getTimestamp("timeModified") );
                
                theList.add( mc );
                logger.log( Level.FINE, "Found an ClanChannel");
            }

            return (List<EntityTemplate>) theList;
        }
        catch( SQLException e ) {
            logger.log(Level.SEVERE, "ClanChannelEntity:findList - fetch failed:{0}", e.getMessage());
            throw new DbEntityException( "ClanChannelEntity:findList - fetch failed", e );
        }
        finally {
            try {            
                logger.log( Level.INFO, "Closing jdbc objects in ClanChannelEntity" );
                if( rs != null ) rs.close();
                if( stmt != null ) stmt.close();
                if( aConnection != null ) aConnection.close();
            }
            catch( SQLException e ) {
                logger.severe("ClanChannelEntity:findList failed to close connection");
                throw new DbEntityException( "MembeChannelEntity:findList failed to close connection", e );
            }
        }
    }

    @Override
    public Integer findOne(Integer key) throws DbEntityException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private static final String INSERT_SQL = "insert into silvertrumpets.ClanChannel" 
        + "( Clan_idClan, ChannelType_idChannelType, electronicAddress1, electronicAddress2, password ) "
        + "values(?,?,?,?,?)";

    @Override
    public Integer insert() throws DbEntityException {
        logger.log( Level.INFO, "ClanChannelEntity insert called" );

        if( this.idClan == null ) {
            logger.severe("ClanChannelEntity:insert: idMember is null which is bad");
            return FAIL;
        }
        
        Connection aConnection = null;
                
        this.setIdClanChannel(ZERO);
        try {
            
            aConnection = getConnection();
            PreparedStatement stmt = aConnection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt( 1, this.idClan );
            stmt.setInt( 2, this.idChannelType );
            stmt.setString( 3, this.electronicAddress1 );
            stmt.setString( 4, this.electronicAddress2 );
            stmt.setString( 5, this.password );
            
            // Note that timeCreated and timeModified are set by triggers in the database
            stmt.execute();
            
            // Now, fetch the row we just inserted and grab the idMessage generated
            // by the database and add this to Clan_has_User table
            
            int key = -1;
            ResultSet rs = stmt.getGeneratedKeys();
            if( rs.next() ) {
                key = rs.getInt(1);
                this.idClanChannel = new Integer( key );
            }
            
            stmt.close();
           
        }
        catch( SQLException e ) {
            logger.log(Level.SEVERE, "ClanChannelEntity:insert - insert failed:{0}", e.getMessage());
            throw new DbEntityException( "ClanChannelEntity:insert - insert failed", e );
        }
        finally {
            try {            
                if( aConnection != null ) aConnection.close();
            }
            catch( SQLException e ) {
                logger.severe("ClanChannelEntity:insert failed to close connection");
                throw new DbEntityException( "ClanChannelEntity:insert failed to close connection", e );
            }
        }
        return SUCCESS;
    }

    private static final String DELETE_CLAN_CHANNEL_SQL = "delete from silvertrumpets.clanchannel "
    + "where idClanChannel = ?";

    /**
     * removeOne - Removes the instance of the clan channel based upon
     * the id
     * 
     * @return SUCCESS if all is well
     *         FAIL is a bad thing happened
     * 
     * @throws DbEntityException 
     */
    @Override
    public Integer removeOne() throws DbEntityException {

        // Do the work
        Connection aConnection = null;
        PreparedStatement stmt = null;
        try {
            aConnection = getConnection();
            stmt = aConnection.prepareStatement( DELETE_CLAN_CHANNEL_SQL );
            stmt.setInt( 1, this.getIdClanChannel() );
            stmt.execute();

            logger.log(Level.INFO, "ClanChannelEntity:removeOne - delete did not throw an exception" );
            return SUCCESS;
        }
        catch( SQLException e ) {
            logger.log(Level.SEVERE, "ClanChannelEntity:removeOne - delete update failed:{0}", e.getMessage());
            throw new DbEntityException( "ClanChannelEntity:removeOne - delete failed", e );
        }
        finally {
            try {            
                logger.log( Level.INFO, "Closing jdbc objects in ClanChannelEntity" );
                if( stmt != null ) stmt.close();
                if( aConnection != null ) aConnection.close();
            }
            catch( SQLException e ) {
                logger.severe("ClanChannelEntity:removeOne: failed to close connection");
                throw new DbEntityException( "ClanChannelEntity:removeOne failed to close connection", e );
            }
        }
    }

    private static final String UPDATE_CLANCHANNEL_SQL = "update silvertrumpets.ClanChannel "
       + "set electronicAddress1 = ?, electronicAddress2 = ?, password = ?, ChannelType_idChannelType = ? "
       + "where idClanChannel = ?";

    @Override
    public Integer update(Integer key) throws DbEntityException {
    
        logger.log(Level.INFO, "ClanChannelEntity:update - Inside update" );

        // Check to see that this object contains something connected to the
        // database.
        if( key == null ) {
            logger.log(Level.SEVERE, "ClanChannelEntity:update - idMemberChannel not set" );
            return FAIL;
        }

        // Do the work
        Connection aConnection = null;
        PreparedStatement stmt = null;
        int ret;
        try {
            aConnection = getConnection();
            stmt = aConnection.prepareStatement( UPDATE_CLANCHANNEL_SQL );
            stmt.setString( 1, this.getElectronicAddress1() );
            stmt.setString( 2, this.getElectronicAddress2() );
            stmt.setString( 3, this.getPassword() );
            stmt.setInt( 4, this.getIdChannelType() );
            stmt.setInt( 5, key );

            if( (ret = stmt.executeUpdate()) < 1 ) {
                logger.log(Level.SEVERE, "ClanChannelEntity:update - update returned zero rows {0}", ret );
                return FAIL;
            }
            
            logger.log(Level.INFO, "ClanChannelEntity:update - update modified {0} rows", ret );
            return SUCCESS;
        }
        catch( SQLException e ) {
            logger.log(Level.SEVERE, "ClanChannelEntity:update - update failed:{0}", e.getMessage());
            throw new DbEntityException( "ClanChannelEntity:update - update failed", e );
        }
        finally {
            try {            
                logger.log( Level.INFO, "Closing jdbc objects in ClanChannelEntity" );
                if( stmt != null ) stmt.close();
                if( aConnection != null ) aConnection.close();
            }
            catch( SQLException e ) {
                logger.severe("ClanChannelEntity:update: failed to close connection");
                throw new DbEntityException( "ClanChannelEntity:update failed to close connection", e );
            }
        }
    }
 }
