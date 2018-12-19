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

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.Topic;
import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.api.jms.ActiveMQJMSClient;
import org.apache.activemq.artemis.api.jms.JMSFactoryType;
import org.apache.activemq.artemis.core.remoting.impl.netty.NettyConnectorFactory;

public class TopicListener implements Closeable {

    private final long TIMEOUT = 10000;

    private final ConnectionFactory connectionFactory;
    private Connection connection;
    private Session session;
    private Topic eventBus;
    private MessageConsumer durableSubscriber;
    
    public TopicListener(String selector) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("host", "localhost");
        params.put("port", 5445);
        TransportConfiguration transportConfiguration = new TransportConfiguration(NettyConnectorFactory.class.getName(), params);
        connectionFactory = ActiveMQJMSClient.createConnectionFactoryWithoutHA(JMSFactoryType.CF,transportConfiguration);
        connection = connectionFactory.createConnection("test", "test");
        connection.start();
        registerDurableSubscriber(selector);
    }
    
    public void registerDurableSubscriber(String selector) throws Exception {
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        eventBus = session.createTopic("EventBus");
        durableSubscriber = session.createDurableSubscriber(eventBus, "TestSubscriber", selector, true);
    }
    
    public Message listenOnEventBus() throws Exception {
        return durableSubscriber.receive(TIMEOUT);
    }

    @Override
    public void close() throws IOException {
        try {
            durableSubscriber.close();
            if (session != null) {
                session.unsubscribe("TestSubscriber");
                session.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (JMSException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
