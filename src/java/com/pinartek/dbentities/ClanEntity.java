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

/**
 *
 * @author Family
 */
public class ClanEntity extends EntityTemplate {
    
    public final static String MANAGER_QUERY = "MANAGED";
    public final static String MEMBER_QUERY = "MEMBER";
    
    // Link to user table
    private Integer idUser = null;
    private Integer idMember = null;
    private Integer idClan = null;
    private String name = null;
    private String address1 = null;
    private String address2 = null;
    private String city = null;
    private String state = null;
    private String zip = null;
    private String status = null;
    private String secretCode = null;
    private Timestamp timeCreated = null;
    private Timestamp timeModified = null;

    public Integer getIdUser() {
        return idUser;
    }

    public void setIdUser(Integer idUser) {
        this.idUser = idUser;
    }

    public Integer getIdMember() {
        return idMember;
    }

    public void setIdMember(Integer idMember) {
        this.idMember = idMember;
    }
    
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

    public Integer getIdClan() {
        return idClan;
    }

    public void setIdClan(Integer idClan) {
        this.idClan = idClan;
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

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getSecretCode() {
        return secretCode;
    }

    public void setSecretCode(String secretCode) {
        this.secretCode = secretCode;
    }

    
    
    private static final String INSERT_SQL = "insert into silvertrumpets.clan( name, address1, address2, "
            + "city, state, zip, status, secretCode) values(?,?,?,?,?,?,?,?)";
    private static final String INSERT_JOIN_SQL = "insert into silvertrumpets.clan_has_user( Clan_idClan, User_idUser ) "
            + "values( ?, ? )";

    /**
     * insert - inserts a row into Clan table and the corresponding row
     * into Clan_has_User table.
     * 
     * @return 0 - Success
     *         1 - idUser is not set or it doesn't have a name
     * @throws DbEntityException 
     */
    @Override
    public Integer insert() throws DbEntityException {
  
        logger.log( Level.INFO, "ClanEntity insert called" );

        if( this.idUser == null ) {
            logger.severe("ClanEntity:insert: idUser is null which is bad");
            return FAIL;
        }
        
        if( this.name == null ) {
            logger.severe("ClanEntity:insert: idUser is null which is bad");
            return FAIL;
        }
        Connection aConnection = null;
        
        
        this.setIdClan(ZERO);
        this.setStatus(ACTIVE_STATUS);
        try {
            
            aConnection = getConnection();
            PreparedStatement stmt = aConnection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
            stmt.setString( 1, this.name );
            stmt.setString( 2, this.address1 );
            stmt.setString( 3, this.address2 );
            stmt.setString( 4, this.city );
            stmt.setString( 5, this.state );
            stmt.setString( 6, this.zip );
            stmt.setString( 7, this.status );
            stmt.setString( 8, this.secretCode );
            
            // Note that timeCreated and timeModified are set by triggers in the database
            stmt.execute();
            
            // Now, fetch the row we just inserted and grab the idClan generated
            // by the database and add this to Clan_has_User table
            
            int key = -1;
            ResultSet rs = stmt.getGeneratedKeys();
            if( rs.next() ) {
                key = rs.getInt(1);
                this.idClan = new Integer( key );
            }
            
            stmt.close();
            
            // Now that I have the idClan and the idUser, insert that into the 
            // join table Clan_has_User
            
            stmt = aConnection.prepareCall(INSERT_JOIN_SQL);
            stmt.setInt( 1, this.idClan.intValue());
            stmt.setInt(2, this.idUser.intValue());
            stmt.execute();
            
        }
        catch( SQLException e ) {
            logger.log(Level.SEVERE, "ClanEntity:insert - insert failed:{0}", e.getMessage());
            throw new DbEntityException( "ClanEntity:insert - insert failed", e );
        }
        finally {
            try {            
                if( aConnection != null ) aConnection.close();
            }
            catch( SQLException e ) {
                logger.severe("ClanEntity:insert failed to close connection");
                throw new DbEntityException( "ClanEntity:insert failed to close connection", e );
            }
        }
        return SUCCESS;
    }

    private static final String UPDATE_CLAN_SQL = "update silvertrumpets.clan "
       + "set name = ?, address1 = ?, address2 = ?, city = ?, state = ?, zip = ?, status = ?, secretCode = ? "
       + "where idClan = ?";

    /**
     * update - Takes the current object and uses the key passed in
     * and sets the values in the database for that key to these values.
     * 
     * @param key - idClan in database which will be updated
     * @return FAIL if something bad happened or SUCCESS if it worked.
     * @throws DbEntityException 
     */
    @Override
    public Integer update(Integer key) throws DbEntityException {

        logger.log(Level.INFO, "ClanEntity:update - Inside update" );

        // Check to see that this object contains something connected to the
        // database.
        if( key == null ) {
            logger.log(Level.SEVERE, "ClanEntity:update - idClan not set" );
            return FAIL;
        }

        // Do the work
        Connection aConnection = null;
        PreparedStatement stmt = null;
        int ret;
        try {
            aConnection = getConnection();
            stmt = aConnection.prepareStatement( UPDATE_CLAN_SQL );
            stmt.setString( 1, this.getName() );
            stmt.setString( 2, this.getAddress1() );
            stmt.setString( 3, this.getAddress2() );
            stmt.setString( 4, this.getCity() );
            stmt.setString( 5, this.getState() );
            stmt.setString( 6, this.getZip() );
            stmt.setString( 7, this.getStatus() );
            stmt.setString( 8, this.getSecretCode() );
            stmt.setInt( 9, key );

            if( (ret = stmt.executeUpdate()) < 1 ) {
                logger.log(Level.SEVERE, "ClanEntity:update - update returned zero rows {0}", ret );
                return FAIL;
            }
            
            logger.log(Level.INFO, "ClanEntity:update - update modified {0} rows", ret );
            return SUCCESS;
        }
        catch( SQLException e ) {
            logger.log(Level.SEVERE, "ClanEntity:update - update failed:{0}", e.getMessage());
            throw new DbEntityException( "ClanEntity:update - update failed", e );
        }
        finally {
            try {            
                logger.log( Level.INFO, "Closing jdbc objects in ClanEntity" );
                if( stmt != null ) stmt.close();
                if( aConnection != null ) aConnection.close();
            }
            catch( SQLException e ) {
                logger.severe("ClanEntity:update: failed to close connection");
                throw new DbEntityException( "ClanEntity:update failed to close connection", e );
            }
        }
    }
    
    private final static String MEMBER_ADD_SQL = "insert into silvertrumpets.Clan_has_Member( Clan_idClan, Member_idMember) values(?,?)";

    /**
     * addClanMember - adds a member to the clan in the database
     * 
     * @return SUCCESS if successful
     *         FAIL if something went wrong
     * @throws DbEntityException 
     */
    public Integer addClanMember() throws DbEntityException {
       
        logger.log(Level.INFO, "ClanEntity:addClanMember - Inside " );

        // Check to see that this object contains an idClan and an idMember
        if( this.idClan == null ) {
            logger.log(Level.SEVERE, "ClanEntity:addClanMember - idClan is null this is bad " );
            return FAIL;
        }
        
        if( this.idMember == null ) {
            logger.log(Level.SEVERE, "ClanEntity:addClanMember - idMember is null this is bad " );
            return FAIL;
        }
        
        // Do the work
        Connection aConnection = null;
        PreparedStatement stmt = null;
        int ret;
        try {
            aConnection = getConnection();
            stmt = aConnection.prepareStatement( MEMBER_ADD_SQL );
            stmt.setInt( 1, this.getIdClan() );
            stmt.setInt( 2, this.getIdMember() );

            if( (ret = stmt.executeUpdate()) < 1 ) {
                logger.log(Level.SEVERE, "ClanEntity:addClanMember - update returned zero rows {0}", ret );
                return FAIL;
            }
            
            logger.log(Level.INFO, "ClanEntity:addClanMember - update modified {0} rows", ret );
            return SUCCESS;
        }
        catch( SQLException e ) {
            logger.log(Level.SEVERE, "ClanEntity:addClanMember - update failed:{0}", e.getMessage());
            throw new DbEntityException( "ClanEntity:addClanMember - update failed", e );
        }
        finally {
            try {            
                logger.log( Level.INFO, "Closing jdbc objects in ClanEntity" );
                if( stmt != null ) stmt.close();
                if( aConnection != null ) aConnection.close();
            }
            catch( SQLException e ) {
                logger.severe("ClanEntity:addClanMember: failed to close connection");
                throw new DbEntityException( "ClanEntity:addClanMember failed to close connection", e );
            }
        }
    }
    
    private static final String FIND_BY_SECRET_CODE_SQL = "select c.idClan, "
           + "c.name, c.address1, c.address2, c.city, c.state, c.zip, c.status, c.secretCode, "
           + "c.timeCreated, c.timeModified "
           + "from silvertrumpets.clan c "
           + "where status = '" + ACTIVE_STATUS + "' "
           + "and c.secretCode = ?";
    

    public static final Integer FIND_BY_SECRET_CODE = new Integer( -99 );
    
    /**
     * findOne - If key is FIND_BY_SECRET_CODE, we look up the secret
     * code in this object to see if it is in the database and active.
     * 
     * @param key - FIND_BY_SECRET_CODE
     * 
     * @return SUCCESS - if we found one
     *          FAIL - if we can not find the clan by the secret code
     * @throws DbEntityException 
     */
    @Override
    public Integer findOne(Integer key) throws DbEntityException {
    
        String sql = FIND_BY_SECRET_CODE_SQL;

        if( key == FIND_BY_SECRET_CODE ) {
            logger.log( Level.INFO, "ClanEntity:findOne called for SECRET CODE" );
            if( this.secretCode == null ) {
                logger.severe("ClanEntity:findOne: secretCode is null which is bad");
                return FAIL;
            }
        }
        else {
            logger.log( Level.INFO, "ClanEntity:findOne called without a valid key" );
        }
        
        Connection aConnection = null;
        ResultSet rs = null;
        PreparedStatement stmt = null;
        try {
            
            aConnection = getConnection();
            stmt = aConnection.prepareStatement(sql);
            stmt.setString(1, this.secretCode );
            logger.log( Level.INFO, stmt.toString() );
            rs = stmt.executeQuery();
            if ( rs != null && rs.next() ) {
                
                this.setIdClan( new Integer( rs.getInt("idClan")));
                this.setName( rs.getString("name") );
                this.setAddress1( rs.getString("address1"));
                this.setAddress2( rs.getString("address2"));
                this.setCity(rs.getString("city"));
                this.setState(rs.getString("state"));
                this.setZip(rs.getString("zip"));
                this.setStatus(rs.getString("status"));
                this.setSecretCode(rs.getString("secretCode"));
                this.setTimeCreated( rs.getTimestamp("timeCreated") );
                this.setTimeModified( rs.getTimestamp("timeModified") );
            }

            return SUCCESS;
        }
        catch( SQLException e ) {
            logger.log(Level.SEVERE, "ClanEntity:findOne - fetch failed:{0}", e.getMessage());
            throw new DbEntityException( "ClanEntity:findOne - fetch failed", e );
        }
        finally {
            try {            
                logger.log( Level.INFO, "Closing jdbc objects in ClanEntity" );
                if( rs != null ) rs.close();
                if( stmt != null ) stmt.close();
                if( aConnection != null ) aConnection.close();
            }
            catch( SQLException e ) {
                logger.severe("ClanEntity:findOne failed to close connection");
                throw new DbEntityException( "ClanEntity:findOne failed to close connection", e );
            }
        }
    }

    /**
     * removeOne - sets the status of this current clan to INACTIVE
     * 
     * @return  FAIL if something bad happened or SUCCESS if it worked
     * 
     * @throws DbEntityException 
     */
    @Override
    public Integer removeOne() throws DbEntityException {
        this.setStatus( INACTIVE_STATUS );
        return this.update( this.getIdClan() );
    }
    
    
    private static final String FIND_SQL_MANAGED_WHERE = "select chu.User_idUser, c.idClan, "
           + "c.name, c.address1, c.address2, c.city, c.state, c.zip, c.status, c.secretCode, "
           + "c.timeCreated, c.timeModified "
           + "from silvertrumpets.clan_has_user chu, silvertrumpets.clan c "
           + "where chu.clan_idClan = c.idClan "
           + "and c.status = '" + ACTIVE_STATUS + "' "
           + "and chu.User_idUser = ?";

    private static final String FIND_SQL_MEMBER_WHERE = "select chm.Member_idMember, c.idClan, "
           + "c.name, c.address1, c.address2, c.city, c.state, c.zip, c.status, c.secretCode, "
           + "c.timeCreated, c.timeModified "
           + "from silvertrumpets.clan_has_member chm, silvertrumpets.clan c "
           + "where chm.clan_idClan = c.idClan "
           + "and c.status = '" + ACTIVE_STATUS + "' "
           + "and chm.Member_idMember = ?";
    
    /**
     * findList - fetches a list of ClanEntity objects from the database.  
     *      * 
     * @param where - The WHERE parameter which can only have two values
     *
     * MANAGER - Which pulls all of the clans that this user manages
     * MEMBER - Which pulls all of the clans that this user is a member of

     * @return  Collection of ClanEntity Objects in a List
     *
     * @throws DbEntityException 
     */
    @Override
    public List<EntityTemplate> findList(String where) throws DbEntityException {
    
        String sql;
        if( where.equalsIgnoreCase(MANAGER_QUERY)) {
            logger.log( Level.INFO, "ClanEntity:findList called for User query" );
            sql = FIND_SQL_MANAGED_WHERE;
            if( this.idUser == null ) {
                logger.severe("ClanEntity:findList: idUser is null which is bad");
                return null;
            }
        }
        else {
            logger.log( Level.INFO, "ClanEntity:findList called for Member query" );
            sql = FIND_SQL_MEMBER_WHERE;
            if( this.idMember == null ) {
                logger.severe("ClanEntity:findList: idMember is null which is bad");
                return null;
            }
        }
        
        List theList = new ArrayList();
        
        Connection aConnection = null;
        ResultSet rs = null;
        PreparedStatement stmt = null;
        try {
            
            aConnection = getConnection();
            stmt = aConnection.prepareStatement(sql);
            if( where.equalsIgnoreCase(MANAGER_QUERY)) {
                stmt.setInt(1, this.idUser.intValue() );
            }
            else {
                stmt.setInt(1, this.idMember.intValue() );
            }
            logger.log( Level.INFO, stmt.toString() );
            rs = stmt.executeQuery();
            while ( rs != null && rs.next() ) {
                
                ClanEntity aClan = new ClanEntity();
                if( where.equalsIgnoreCase(MANAGER_QUERY)) {
                    aClan.setIdUser( this.idUser );
                    aClan.setIdMember(null);
                }
                else {
                    aClan.setIdMember( this.idMember );
                    aClan.setIdUser(null);
                }
                aClan.setIdClan( new Integer( rs.getInt("idClan")));
                aClan.setName( rs.getString("name") );
                aClan.setAddress1( rs.getString("address1"));
                aClan.setAddress2( rs.getString("address2"));
                aClan.setCity(rs.getString("city"));
                aClan.setState(rs.getString("state"));
                aClan.setZip(rs.getString("zip"));
                aClan.setStatus(rs.getString("status"));
                aClan.setSecretCode(rs.getString("secretCode"));
                aClan.setTimeCreated( rs.getTimestamp("timeCreated") );
                aClan.setTimeModified( rs.getTimestamp("timeModified") );
                theList.add( aClan );
                logger.log( Level.FINE, "Found a Clan");
            }

            return (List<EntityTemplate>) theList;
        }
        catch( SQLException e ) {
            logger.log(Level.SEVERE, "ClanEntity:findList - fetch failed:{0}", e.getMessage());
            throw new DbEntityException( "ClanEntity:findList - fetch failed", e );
        }
        finally {
            try {            
                logger.log( Level.INFO, "Closing jdbc objects in ClanEntity" );
                if( rs != null ) rs.close();
                if( stmt != null ) stmt.close();
                if( aConnection != null ) aConnection.close();
            }
            catch( SQLException e ) {
                logger.severe("ClanEntity:findList failed to close connection");
                throw new DbEntityException( "ClanEntity:findList failed to close connection", e );
            }
        }
    }

    private static final String DELETE_MANAGE_CLAN_SQL = "delete from silvertrumpets.clan_has_user "
       + "where Clan_idClan = ? "
       + "and User_idUser = ?";
    

    private static final String FIND_CLAN_USERS_SQL = "select * from silvertrumpets.clan_has_user "
       + "where Clan_idClan = ? ";
    
    private static final String DELETE_MEMBERS_SQL = "delete from silvertrumpets.clan_has_member "
       + "where Clan_idClan = ? ";

    /**
     * removeFromManagedClans - Removes this user from the managed clans.  If
     * there are no more users managing this clan, it deactivates the clan
     * @return
     * @throws DbEntityException 
     */
    public Integer removeFromManagedClans() throws DbEntityException {

        logger.log(Level.INFO, "ClanEntity:removeFromManagedClans - Inside" );

        // Check to see that this object contains something connected to the
        // database.
        if( this.idClan == null ) {
            logger.log(Level.SEVERE, "ClanEntity:removeFromManagedClans - idClan not set" );
            return FAIL;
        }

        if( this.idUser == null ) {
            logger.log(Level.SEVERE, "ClanEntity:removeFromManagedClans - idUser not set" );
            return FAIL;
        }

        
        // Do the work
        Connection aConnection = null;
        PreparedStatement stmt = null;
        PreparedStatement wackStmt = null;
        ResultSet rs = null;
        try {
            aConnection = getConnection();
            stmt = aConnection.prepareStatement( DELETE_MANAGE_CLAN_SQL );
            stmt.setInt( 1, this.getIdClan() );
            stmt.setInt( 2, this.getIdUser() );
            stmt.executeUpdate();
            
            // Check to see if this clan is managed by someone else
            stmt.close();
            stmt = aConnection.prepareStatement( FIND_CLAN_USERS_SQL );
            stmt.setInt( 1, this.getIdClan() );
            rs = stmt.executeQuery();
            if( rs.next()) {
                // We still have users managing this clan, we are done
                return SUCCESS;
            }
            else {
                // No more users are managing this clan, remove all
                // members and inactivate the clan
                wackStmt = aConnection.prepareStatement( DELETE_MEMBERS_SQL );
                wackStmt.setInt( 1, this.getIdClan() );
                wackStmt.executeUpdate();

                // This will inactivate the clan
                return this.removeOne();
            } 
        }
        catch( SQLException e ) {
            logger.log(Level.SEVERE, "ClanEntity:removeFromManagedClans - delete failed:{0}", e.getMessage());
            throw new DbEntityException( "ClanEntity:removeFromManagedClans - delete failed", e );
        }
        finally {
            try {            
                logger.log( Level.INFO, "Closing jdbc objects in ClanEntity" );
                if( stmt != null ) stmt.close();
                if( wackStmt != null ) wackStmt.close();
                if( aConnection != null ) aConnection.close();
                if( rs != null ) rs.close();
            }
            catch( SQLException e ) {
                logger.severe("ClanEntity:removeFromManagedClans: failed to close connection");
                throw new DbEntityException( "ClanEntity:removeFromManagedClans failed to close connection", e );
            }
        }
    }

    private static final String DELETE_THIS_MEMBER_SQL = "delete from silvertrumpets.clan_has_member "
       + "where Clan_idClan = ? and Member_idMember = ?";

    
    /**
     * removeFromMemberClans - Removes this member from the list of people
     * who are part of this clan.
     * 
     * @return SUCCESS if all is well
     *         FAIL if there is a problem
     * 
     * @throws DbEntityException 
     */
    public Integer removeFromMemberClans() throws DbEntityException {
        
        logger.log(Level.INFO, "ClanEntity:removeFromMemberClans - Inside" );

        // Check to see that this object contains something connected to the
        // database.
        if( this.idClan == null ) {
            logger.log(Level.SEVERE, "ClanEntity:removeFromMemberClans - idClan not set" );
            return FAIL;
        }

        if( this.idMember == null ) {
            logger.log(Level.SEVERE, "ClanEntity:removeFromMemberClans - idMember not set" );
            return FAIL;
        }

        
        // Do the work
        Connection aConnection = null;
        PreparedStatement stmt = null;
        PreparedStatement wackStmt = null;
        ResultSet rs = null;
        try {
            aConnection = getConnection();
            stmt = aConnection.prepareStatement( DELETE_THIS_MEMBER_SQL );
            stmt.setInt( 1, this.getIdClan() );
            stmt.setInt( 2, this.getIdMember() );
            stmt.executeUpdate();
        }
        catch( SQLException e ) {
            logger.log(Level.SEVERE, "ClanEntity:removeFromMemberClans - delete failed:{0}", e.getMessage());
            throw new DbEntityException( "ClanEntity:removeFromMemberClans - delete failed", e );
        }
        finally {
            try {            
                logger.log( Level.INFO, "Closing jdbc objects in ClanEntity" );
                if( stmt != null ) stmt.close();
                if( wackStmt != null ) wackStmt.close();
                if( aConnection != null ) aConnection.close();
                if( rs != null ) rs.close();
            }
            catch( SQLException e ) {
                logger.severe("ClanEntity:removeFromMemberClans: failed to close connection");
                throw new DbEntityException( "ClanEntity:removeFromMemberClans failed to close connection", e );
            }
        }

        return SUCCESS;
    }
}
