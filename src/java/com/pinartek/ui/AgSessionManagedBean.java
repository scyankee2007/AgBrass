/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pinartek.ui;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * AgSessionManagedBean - The core session bean to store the data used by the
 * session.
 * 
 * @author Mike Williamson
 */
@Named(value = "agSessionBean")
@SessionScoped
public class AgSessionManagedBean implements Serializable {
    
    static final Logger logger = Logger.getLogger("com.pinartek.ui");    
    static final String STARTOVER = "startover";
    

    /** Creates a new instance of AgSessionManagedBean */
    public AgSessionManagedBean() {
        logger.log( Level.INFO, "Session Startup" );
    }


    /**
     * clearSession - called when it is time to invalid session
     * data
     */
    public String clearSession() {
       return "";
    }

   
    
    /**
     * startOver - Navigation function when the cancel button is hit and we
     * want to start again from the beginning
     * 
     * @return startover
     */
    public String startOverAction() {
    
        return STARTOVER;
    }

    
}
