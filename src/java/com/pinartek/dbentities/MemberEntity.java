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

/**
 *
 * @author Family
 */
public class MemberEntity extends EntityTemplate {

    private Integer idUser = null;
    private Integer idMember = null;
    private Timestamp timeCreated = null;
    private Timestamp timeModified = null;

    public Integer getIdMember() {
        return idMember;
    }

    public void setIdMember(Integer idMember) {
        this.idMember = idMember;
    }

    public Integer getIdUser() {
        return idUser;
    }

    public void setIdUser(Integer idUser) {
        this.idUser = idUser;
    }

    public Timestamp getTimeCreated() {
        return timeCreated;
    }

    public Timestamp getTimeModified() {
        return timeModified;
    }
   

    private static final String INSERT_SQL = "insert into silvertrumpets.member( User_idUser ) values( ? )";
    
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
        logger.info( "MemberEntity insert called" );

        if( this.idUser == null ) {
            logger.severe("MemberEntity:insert: idUser is null which is bad");
            return FAIL;
        }
        
        Connection aConnection = null;
        
        this.setIdMember(ZERO);
        try {
            
            aConnection = getConnection();
            PreparedStatement stmt = aConnection.prepareCall(INSERT_SQL);
            stmt.setInt( 1, this.idUser.intValue() );
            
            // Note that timeCreated and timeModified are set by triggers in the database
            stmt.execute();
        }
        catch( SQLException e ) {
            logger.log(Level.SEVERE, "MemberEntity:insert - insert failed:{0}", e.getMessage());
            throw new DbEntityException( "MemberEntity:insert - insert failed", e );
        }
        finally {
            try {            
                if( aConnection != null ) aConnection.close();
            }
            catch( SQLException e ) {
                logger.severe("MemberEntity:insert failed to close connection");
                throw new DbEntityException( "MemberEntity:insert failed to close connection", e );
            }
        }
        return SUCCESS;
    }
    

    @Override
    public Integer update(Integer key) throws DbEntityException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Integer findOne(Integer key) throws DbEntityException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<EntityTemplate> findList(String where) throws DbEntityException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Integer removeOne() throws DbEntityException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    
    private static final String FIND_BY_IDUSER_SQL = "select User_idUser, idMember, timeCreated, timeModified from silvertrumpets.member where User_idUser = ?";

    /**
     * findOneByIdUser - Takes the idUser in the bean and searches for it in the
     * the database
     * 
     * @return -  SUCCESS on success
     *            FAIL on failure
     * 
     * @throws DbEntityException when anything else happens
     */
    public Integer findOneByIdUser()
            throws DbEntityException
    {
        logger.info( "MemberEntity findOneByIdUser called" );
        Connection aConnection = null;
        ResultSet rs = null;
        PreparedStatement stmt = null;
        try {
            
            aConnection = getConnection();
            stmt = aConnection.prepareCall(FIND_BY_IDUSER_SQL);
            stmt.setInt( 1, this.idUser.intValue() );
            rs = stmt.executeQuery();
            if( rs != null && rs.first() ) {
                this.idUser = new Integer( rs.getInt("User_idUser") );
                this.idMember = new Integer( rs.getInt("idMember"));
                this.timeCreated = rs.getTimestamp("timeCreated");
                this.timeModified = rs.getTimestamp("timeModified");
                logger.log(Level.INFO,"Found the member successfully");
                return SUCCESS;
            }
            
            return FAIL;
        }
        catch( SQLException e ) {
            logger.log(Level.SEVERE, "MemberEntity:findOneByIdUser - fetch failed:{0}", e.getMessage());
            throw new DbEntityException( "MemberEntity:findOneByIdUser - fetch failed", e );
        }
        finally {
            try {            
                logger.info("Closing jdbc objects in MemberEntity" );
                if( rs != null ) rs.close();
                if( stmt != null ) stmt.close();
                if( aConnection != null ) aConnection.close();
            }
            catch( SQLException e ) {
                logger.severe("MemberEntity:findOneByIdUser failed to close connection");
                throw new DbEntityException( "MemberEntity:findOneByIdUser failed to close connection", e );
            }
        }
    }
}
