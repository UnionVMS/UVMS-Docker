/*
﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
© European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
redistribute it and/or modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */
package eu.europa.ec.fisheries.uvms.docker.validation.common;

import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;
import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.api.jms.ActiveMQJMSClient;
import org.apache.activemq.artemis.api.jms.JMSFactoryType;
import org.apache.activemq.artemis.core.remoting.impl.netty.NettyConnectorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TopicListener implements Closeable {

    public static final String EVENT_BUS = "jms.topic.EventBus";
    public static final String EVENT_STREAM = "jms.topic.EventStream";
    public static final String CONFIG_STATUS = "jms.topic.ConfigStatus";
    
    private static final Logger log = LoggerFactory.getLogger(TopicListener.class.getSimpleName());
    private Connection connection;
    private Session session;
    private MessageConsumer subscriber;
    
    public TopicListener(String selector) throws Exception {
        this(EVENT_BUS, selector);
    }

    public TopicListener(String topic, String selector) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("host", "localhost");
        params.put("port", 5445);
        TransportConfiguration transportConfiguration = new TransportConfiguration(NettyConnectorFactory.class.getName(), params);
        ConnectionFactory connectionFactory = ActiveMQJMSClient.createConnectionFactoryWithoutHA(JMSFactoryType.CF, transportConfiguration);
        connection = connectionFactory.createConnection("test", "test");
        connection.start();
        registerSubscriber(topic, selector);
    }
    
    private void registerSubscriber(String topic, String selector) throws Exception {
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Topic eventBus = session.createTopic(topic);
        subscriber = session.createConsumer(eventBus, selector, true);
    }
    
    public Message listenOnEventBus() throws Exception {
        long TIMEOUT = 10000;
        return subscriber.receive(TIMEOUT);
    }

    public <T> T listenOnEventBusForSpecificMessage(Class<T> messageType) throws Exception {
        int retries = 0;
        int maxRetries = 3;
        while (retries < maxRetries) {
            TextMessage message = (TextMessage) listenOnEventBus();
            try {
                return JAXBMarshaller.unmarshallString(message.getText(), messageType);
            } catch (Exception e) {
                retries++;
            }
        }
        return null;
    }

    @Override
    public void close() throws IOException {
        try {
            subscriber.close();
            if (session != null) {
                session.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (JMSException e) {
            log.error("Error occurred while closing resources.", e);
        }
    }
}
