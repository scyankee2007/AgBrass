/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pinartek.channel;

import com.pinartek.dbentities.EventEntity;
import com.pinartek.dbentities.MessageEntity;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author Family
 */
public abstract class ChannelConnectorTemplate {
    
    static final Logger logger = Logger.getLogger("com.pinartek.channel.ChannelConnectorTemplate");
    public static final Integer SUCCESS = new Integer( 0 );
    public static final Integer FAIL = new Integer ( 1 );

    public abstract Integer sendMessage( List to, MessageEntity msg ) throws ChannelConnectorException;
    public abstract Integer sendEvent( List to, EventEntity event ) throws ChannelConnectorException;
    
}
