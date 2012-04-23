package com.pinartek.dbentities;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * EntityTemplate is the base class for all DB access.  The model is simple
 * so the plan is to use simple JDBC to connect.  When the product
 * takes off, we can grow this to an enterprise scale later.
 * 
 * @author Mike Williamson
 */
public abstract class EntityTemplate implements Serializable {


    static final Logger logger = Logger.getLogger("com.pinartek.dbentities.EntityTemplate");
    public static final Integer SUCCESS = new Integer( 0 );
    public static final Integer FAIL = new Integer ( 1 );
    public static final Integer ZERO = new Integer ( 0 );
    public static final String ACTIVE_STATUS = "ACTIVE";
    public static final String INACTIVE_STATUS = "INACTIVE";
    
    private static DataSource aDS = null;

    /**
     * getConnection - Looks up configured Connection Pool and fetches
     * a connection from it.
     * 
     * @return JDBC Connection
     */
    Connection getConnection()
            throws DbEntityException
    {
        Connection aConnection = null;
        try {
            if( aDS == null ) {
                Context initialContext = (Context) new InitialContext();
                aDS = (DataSource) initialContext.lookup ("jdbc/myDatasource");
            }
            aConnection = aDS.getConnection();
        }
        catch( NamingException e ) {
            logger.log(Level.SEVERE, "Failed to get a database Connection: {0}", e.getMessage());
            throw new DbEntityException( "EntityTempalte:getConnection failed to get Connection", e );
        }
        catch( SQLException e ) {
            logger.log(Level.SEVERE, "Failed to get a database Connection: {1}", e.getMessage());
            throw new DbEntityException( "EntityTempalte:getConnection failed to get Connection", e );
        }
        return aConnection;
    }
    // Methods
    
    /**
     * insert - Takes the object and creates a new instance of it
     * in the database.  Subclasses must do the work but this provides 
     * a common foot print
     *
     * @return - 0 on success
     *           1 on object exists already
     * @throws DbEntityException when anything else happens
     */
    public abstract Integer insert()
            throws DbEntityException;
    
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
    public abstract Integer update( Integer key )
            throws DbEntityException;

    /**
     * findOne - Takes the key and tries to find the instance in the
     * database.  If it finds it, the object is filled in with the
     * data from the database.
     * 
     * @param key - Unique key to find the object
     * @return - SUCCESS on success
     *           FAIL on failure
     * @throws DbEntityException when anything else happens
     */
    public abstract Integer findOne( Integer key )
            throws DbEntityException;

    /**
     * findList - Takes the where clause and tries to find a list of objects in
     * the database. 
     * 
     * @param where - Unique key to find the object
     * @return - EntityTemplate on success
     *           null if the key was not found
     * @throws DbEntityException when anything else happens
     */
    public abstract List<EntityTemplate> findList( String where )
            throws DbEntityException;
    
    /**
     * removeOne - finds this object in the database and does the work of removing
     * it.  Sometimes, this will mean setting a status to INACTIVE, sometimes
     * this will mean removing the instance from the database altogether.
     * 
     * @return SUCCESS when it works
     *         FAIL when something goes bad.
     * 
     * @throws DbEntityException 
     */
    public abstract Integer removeOne()
            throws DbEntityException;
}
