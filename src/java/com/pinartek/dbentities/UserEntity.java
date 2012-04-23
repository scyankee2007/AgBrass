/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pinartek.dbentities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.logging.Level;
import org.jasypt.util.password.BasicPasswordEncryptor;


/**
 * UserEntity - Access class for the user concept in database.  Provides
 * functions for getting and setting the database up.
 * 
 * @author Mike Williamson
 */
public class UserEntity extends EntityTemplate {
    
    private Integer idUser = new Integer( 0 );
    private String emailAddress = null;
    private String password = null;
    private String firstName = null;
    private String lastName = null;
    private Timestamp timeCreated = null;
    private Timestamp timeModified = null;
    private String status = null;

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public void setPassword(String userPassword) {
        BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
        String encryptedPassword = passwordEncryptor.encryptPassword(userPassword);
        this.password = encryptedPassword;
    }
    
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Integer getIdUser() {
        return idUser;
    }

    public void setIdUser(Integer idUser) {
        this.idUser = idUser;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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
    
    
    private static final String INSERT_SQL = "insert into silvertrumpets.user( emailAddress, password, firstName, lastName, status) values(?,?,?,?,?)";
    
    /**
     * insert - Takes the object and creates a new instance of it
     * in the database.  Subclasses must do the work but this provides 
     * a common foot print
     * 
     * @return - 0 on success
     *           1 if emailAddress is null - must have an email address
     * 
     * @throws DbEntityException when anything bad happens
     */
    @Override
    public Integer insert()
            throws DbEntityException
    {
        logger.info( "UserEntity insert called" );

        if( this.emailAddress == null ) {
            logger.severe("UserEntity:insert: emailAddress is null which is bad");
            return FAIL;
        }
        
        if( this.firstName == null ) {
            logger.severe("UserEntity:insert: firstName is null which is bad");
            return FAIL;
        }

        Connection aConnection = null;
        
        this.setIdUser(ZERO);
        this.setStatus(ACTIVE_STATUS);
        try {
            
            aConnection = getConnection();
            PreparedStatement stmt = aConnection.prepareCall(INSERT_SQL);
            stmt.setString( 1, this.emailAddress );
            stmt.setString( 2, this.password );
            stmt.setString( 3, this.firstName );
            stmt.setString( 4, this.lastName );
            stmt.setString( 5, this.status );
            
            // Note that timeCreated and timeModified are set by triggers in the database
            stmt.execute();
        }
        catch( SQLException e ) {
            logger.log(Level.SEVERE, "UserEntity:insert - insert failed:{0}", e.getMessage());
            throw new DbEntityException( "UserEntity:insert - insert failed", e );
        }
        finally {
            try {            
                if( aConnection != null ) aConnection.close();
            }
            catch( SQLException e ) {
                logger.severe("UserEntity:insert failed to close connection");
                throw new DbEntityException( "UserEntity:insert failed to close connection", e );
            }
        }
        return SUCCESS;
    }
    
    /**
     * update - Takes the key and updates that record in the db
     * with the new information in object.  Subclasses must do the work but 
     * this provides a common foot print
     * 
     * @param obj - Object data to be updated
     *        key - Key to find the original object
     * @return - 0 on success
     *           1 if the key is not found
     * @throws DbEntityException when anything else happens
     */
    @Override
    public Integer update( Integer key )
            throws DbEntityException
    {
        return SUCCESS;
    }

    /**
     * findOne - Takes the key and tries to find the instance in the
     * database. 
     * 
     * @param key - Unique key to find the object
     * @return - EntityTemplate on success
     *           null if the key was not found
     * @throws DbEntityException when anything else happens
     */
    @Override
    public Integer findOne( Integer key )
            throws DbEntityException
    {
        return null;
    }

    /**
     * findList - Takes the where clause and tries to find a list of objects in
     * the database. 
     * 
     * @param where - Unique key to find the object
     * @return - EntityTemplate on success
     *           null if the key was not found
     * @throws DbEntityException when anything else happens
     */
    @Override
    public List<EntityTemplate> findList( String where )
            throws DbEntityException
    {
        return null;
    }

    @Override
    public Integer removeOne() throws DbEntityException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    
    
    private static final String FIND_BY_EMAIL_SQL = "select idUser, emailAddress, password, firstName, lastName, status, timeCreated, timeModified from silvertrumpets.user where emailAddress = ?";

    /**
     * findOne - Takes the emailAddress in the bean and searches for it in the
     * the database
     * 
     * @return -  SUCCESS on success
     *            FAIL on failure
     * 
     * Note, the database should prevent multiple instances of the
     * same email address so it would be strange if there were
     * more than one row.
     * 
     * @throws DbEntityException when anything else happens
     */
    public Integer findOneByEmail()
            throws DbEntityException
    {
        logger.info( "UserEntity findOneByEmail called" );
        Connection aConnection = null;
        ResultSet rs = null;
        PreparedStatement stmt = null;
        try {
            
            aConnection = getConnection();
            stmt = aConnection.prepareCall(FIND_BY_EMAIL_SQL);
            stmt.setString( 1, this.getEmailAddress() );
            rs = stmt.executeQuery();
            if( rs != null && rs.first() ) {
                this.idUser = new Integer( rs.getInt("idUser") );
                this.emailAddress = rs.getString("emailAddress");
                this.password = rs.getString("password");
                this.firstName = rs.getString("firstName");
                this.lastName = rs.getString( "lastName");
                this.timeCreated = rs.getTimestamp("timeCreated");
                this.timeModified = rs.getTimestamp("timeModified");
                return SUCCESS;
            }
            
            return FAIL;
        }
        catch( SQLException e ) {
            logger.log(Level.SEVERE, "UserEntity:findOneByEmail - fetch failed:{0}", e.getMessage());
            throw new DbEntityException( "UserEntity:findOneByEmail - fetch failed", e );
        }
        finally {
            try {            
                logger.info("Closing jdbc objects in UserEntity" );
                if( rs != null ) rs.close();
                if( stmt != null ) stmt.close();
                if( aConnection != null ) aConnection.close();
            }
            catch( SQLException e ) {
                logger.severe("UserEntity:findOneByEmail failed to close connection");
                throw new DbEntityException( "UserEntity:findOneByEmail failed to close connection", e );
            }
        }
    }
    
    /**
     * validPassword - checks to see that the password passed in
     * matches the password stored in this bean.
     * 
     * @param checkPw - Password to check
     * @return - true if the password is valid, false if not
     */
    public boolean validPassword( String userPassword ) {
        BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
        return passwordEncryptor.checkPassword( userPassword, this.password );
    }
}
