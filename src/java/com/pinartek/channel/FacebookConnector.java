/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pinartek.channel;

import com.pinartek.dbentities.EventEntity;
import com.pinartek.dbentities.MessageEntity;
import java.util.List;

/**
 *
 * @author Family
 */
public class FacebookConnector extends ChannelConnectorTemplate {

    @Override
    public Integer sendEvent(List to, EventEntity event) throws ChannelConnectorException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Integer sendMessage(List to, MessageEntity msg) throws ChannelConnectorException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
    
}
