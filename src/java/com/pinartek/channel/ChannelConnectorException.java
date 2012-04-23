/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pinartek.channel;

/**
 *
 * @author Family
 */
public class ChannelConnectorException extends Exception {

    /**
     * Creates a new instance of <code>ChannelConnectorException</code> without detail message.
     */
    public ChannelConnectorException() {
    }

    /**
     * Constructs an instance of <code>ChannelConnectorException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public ChannelConnectorException(String msg) {
        super(msg);
    }
    
    public ChannelConnectorException( String msg, Throwable cause ) {
        super( msg, cause );
    }
}
