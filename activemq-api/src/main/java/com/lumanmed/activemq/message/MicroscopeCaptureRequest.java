package com.lumanmed.activemq.message;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import com.lumanmed.activemq.api.Strings;

public class MicroscopeCaptureRequest extends BaseMessageAdaptor {
    
    public static final String TYPE = "MICRO_1";
    
    

    @Override
    public Message toMessage(Session session) throws JMSException {
        Message message = session.createMessage();
        setMessageProperties(message);
        message.setJMSType(TYPE);
        return message;
    }

    @Override
    public void fromMessage(Message message) throws JMSException {
        this.setId(message.getStringProperty(Strings.ID));
    }
}
