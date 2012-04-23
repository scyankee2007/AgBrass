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
 * MemberChannelEntity interfaces with the database MemberChannelEntity table.  This
 * table holds the channels that members want their information sent to.
 * 
 * @author Family
 */
public class MemberChannelEntity extends EntityTemplate {
    
    private Integer idMemberChannel = null;
    private Integer idMember = null;
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

    public Integer getIdMember() {
        return idMember;
    }

    public void setIdMember(Integer idMember) {
        this.idMember = idMember;
    }

    public Integer getIdMemberChannel() {
        return idMemberChannel;
    }

    public void setIdMemberChannel(Integer idMemberChannel) {
        this.idMemberChannel = idMemberChannel;
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
    private Channeltype loadChannelType( Integer idChannelType ) {
        
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

    
   private static final String MEMBER_CHANNELS_SQL = "select idMemberChannel, Member_idMember, ChannelType_idChannelType, electronicAddress1, "
           + "electronicAddress2, password, timeCreated, timeModified "
           + "from silvertrumpets.memberchannel "
           + "where Member_idMember = ?";
 

    @Override
    public List<EntityTemplate> findList(String where) throws DbEntityException {
        String sql = MEMBER_CHANNELS_SQL;
        
        if( this.idMember == null ) {
            logger.severe("MemberChannelEntity:findList: idMember is null which is bad");
            return null;
        }


        if( where != null ) {
            logger.severe("MemberChannelEntity:findList: where is NOT null - this should not be");
        }

        List theList = new ArrayList();
        
        Connection aConnection = null;
        ResultSet rs = null;
        PreparedStatement stmt = null;
        try {
            
            aConnection = getConnection();
            stmt = aConnection.prepareStatement(sql);
            stmt.setInt(1, this.idMember.intValue() );
            logger.log( Level.INFO, stmt.toString() );
            rs = stmt.executeQuery();
            while ( rs != null && rs.next() ) {
                MemberChannelEntity mc = new MemberChannelEntity();
                mc.setIdMemberChannel( rs.getInt("idMemberChannel"));
                mc.setIdMember( rs.getInt("Member_idMember"));
                mc.setIdChannelType( rs.getInt("ChannelType_idChannelType"));
                mc.setElectronicAddress1( rs.getString("electronicAddress1"));
                mc.setElectronicAddress2( rs.getString("electronicAddress2"));
                mc.setPassword( rs.getString("password"));
                mc.setTimeCreated( rs.getTimestamp("timeCreated") );
                mc.setTimeModified( rs.getTimestamp("timeModified") );
                
                theList.add( mc );
                logger.log( Level.FINE, "Found an MemberChannel");
            }

            return (List<EntityTemplate>) theList;
        }
        catch( SQLException e ) {
            logger.log(Level.SEVERE, "MemberChannelEntity:findList - fetch failed:{0}", e.getMessage());
            throw new DbEntityException( "MemberChannelEntity:findList - fetch failed", e );
        }
        finally {
            try {            
                logger.log( Level.INFO, "Closing jdbc objects in MemberChannelEntity" );
                if( rs != null ) rs.close();
                if( stmt != null ) stmt.close();
                if( aConnection != null ) aConnection.close();
            }
            catch( SQLException e ) {
                logger.severe("MemberChannelEntity:findList failed to close connection");
                throw new DbEntityException( "MembeChannelEntity:findList failed to close connection", e );
            }
        }
    }

    @Override
    public Integer findOne(Integer key) throws DbEntityException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    private static final String INSERT_SQL = "insert into silvertrumpets.MemberChannel" 
        + "( Member_idMember, ChannelType_idChannelType, electronicAddress1, electronicAddress2, password ) "
        + "values(?,?,?,?,?)";


    @Override
    public Integer insert() throws DbEntityException {
        logger.log( Level.INFO, "MemberChannelEntity insert called" );

        if( this.idMember == null ) {
            logger.severe("MemberChannelEntity:insert: idMember is null which is bad");
            return FAIL;
        }
        
        Connection aConnection = null;
                
        this.setIdMemberChannel(ZERO);
        try {
            
            aConnection = getConnection();
            PreparedStatement stmt = aConnection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt( 1, this.idMember );
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
                this.idMemberChannel = new Integer( key );
            }
            
            stmt.close();
           
        }
        catch( SQLException e ) {
            logger.log(Level.SEVERE, "MemberChannelEntity:insert - insert failed:{0}", e.getMessage());
            throw new DbEntityException( "MemberChannelEntity:insert - insert failed", e );
        }
        finally {
            try {            
                if( aConnection != null ) aConnection.close();
            }
            catch( SQLException e ) {
                logger.severe("MemberChannelEntity:insert failed to close connection");
                throw new DbEntityException( "MemberChannelEntity:insert failed to close connection", e );
            }
        }
        return SUCCESS;
    }

    
    private static final String DELETE_MEMBER_CHANNEL_SQL = "delete from silvertrumpets.memberchannel "
    + "where idMemberChannel = ?";
    
    /**
     * removeOne - remove the passed in channel link
     * @return
     * @throws DbEntityException 
     */
    @Override
    public Integer removeOne() throws DbEntityException {
        // Do the work
        Connection aConnection = null;
        PreparedStatement stmt = null;
        try {
            aConnection = getConnection();
            stmt = aConnection.prepareStatement( DELETE_MEMBER_CHANNEL_SQL );
            stmt.setInt( 1, this.getIdMemberChannel() );
            stmt.execute();

            logger.log(Level.INFO, "MemberChannelEntity:removeOne - delete did not throw an exception" );
            return SUCCESS;
        }
        catch( SQLException e ) {
            logger.log(Level.SEVERE, "MemberChannelEntity:removeOne - delete update failed:{0}", e.getMessage());
            throw new DbEntityException( "MemberChannelEntity:removeOne - delete failed", e );
        }
        finally {
            try {            
                logger.log( Level.INFO, "Closing jdbc objects in MemberChannelEntity" );
                if( stmt != null ) stmt.close();
                if( aConnection != null ) aConnection.close();
            }
            catch( SQLException e ) {
                logger.severe("MemberChannelEntity:removeOne: failed to close connection");
                throw new DbEntityException( "MemberChannelEntity:removeOne failed to close connection", e );
            }
        }
    }

    private static final String UPDATE_EVENT_SQL = "update silvertrumpets.MemberChannel "
       + "set electronicAddress1 = ?, electronicAddress2 = ?, password = ?, ChannelType_idChannelType = ? "
       + "where idMemberChannel = ?";


    /**
     * update - update the row represented by key in the database with
     * this objects values
     * 
     * @param key
     * @return
     * @throws DbEntityException 
     */
    @Override
    public Integer update(Integer key) throws DbEntityException {

        logger.log(Level.INFO, "MemberChannelEntity:update - Inside update" );

        // Check to see that this object contains something connected to the
        // database.
        if( key == null ) {
            logger.log(Level.SEVERE, "MemberChannelEntity:update - idMemberChannel not set" );
            return FAIL;
        }

        // Do the work
        Connection aConnection = null;
        PreparedStatement stmt = null;
        int ret;
        try {
            aConnection = getConnection();
            stmt = aConnection.prepareStatement( UPDATE_EVENT_SQL );
            stmt.setString( 1, this.getElectronicAddress1() );
            stmt.setString( 2, this.getElectronicAddress2() );
            stmt.setString( 3, this.getPassword() );
            stmt.setInt( 4, this.getIdChannelType() );
            stmt.setInt( 5, key );

            if( (ret = stmt.executeUpdate()) < 1 ) {
                logger.log(Level.SEVERE, "MemberChannelEntity:update - update returned zero rows {0}", ret );
                return FAIL;
            }
            
            logger.log(Level.INFO, "MemberChannelEntity:update - update modified {0} rows", ret );
            return SUCCESS;
        }
        catch( SQLException e ) {
            logger.log(Level.SEVERE, "MemberChannelEntity:update - update failed:{0}", e.getMessage());
            throw new DbEntityException( "MemberChannelEntity:update - update failed", e );
        }
        finally {
            try {            
                logger.log( Level.INFO, "Closing jdbc objects in MemberChannelEntity" );
                if( stmt != null ) stmt.close();
                if( aConnection != null ) aConnection.close();
            }
            catch( SQLException e ) {
                logger.severe("MemberChannelEntity:update: failed to close connection");
                throw new DbEntityException( "MemberChannelEntity:update failed to close connection", e );
            }
        }
    }
    
}
