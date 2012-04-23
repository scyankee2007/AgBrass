/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pinartek.dbentities;

/**
 *
 * @author Family
 */
public class DbEntityException extends Exception {

    /**
     * Creates a new instance of <code>DbEntityException</code> without detail message.
     */
    public DbEntityException() {
    }

    /**
     * Constructs an instance of <code>DbEntityException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public DbEntityException(String msg) {
        super(msg);
    }
    
    public DbEntityException( String msg, Throwable cause ) {
        super( msg, cause );
    }
}
