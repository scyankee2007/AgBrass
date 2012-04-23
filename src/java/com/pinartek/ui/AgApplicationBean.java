/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pinartek.ui;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

/**
 *
 * @author Family
 */
@Named( value = "agApplicationBean")
@ApplicationScoped
public class AgApplicationBean implements Serializable {

    // Logger Properties File
    private static final String LOGGING_PROPERTIES = "/WEB-INF/resources/logging.properties";

    static final Logger logger = Logger.getLogger("com.pinartek.ui");    

    private Level logLevel = Level.INFO;
    
    public Level getLogLevel() {
        return logLevel;
    }
    
    public void setLogLevel( Level aLevel ) {        
        logLevel = aLevel;        
        LogManager.getLogManager().getLogger("").setLevel( logLevel );
    }
    /** Creates a new instance of AgApplicationBean */
    public AgApplicationBean() {
        
        LogManager.getLogManager().getLogger("").setLevel( logLevel );
        logger.severe("Starting Up Application Bean");
    }
}
