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

import java.util.UUID;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.Topic;
import org.apache.activemq.ActiveMQConnectionFactory;

public class TopicListener {

    private final long TIMEOUT = 10000;

    private final ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
    private Connection connection;
    private Session session;
    private Topic eventBus;
    
    public MessageConsumer registerDurableSubscriber(String selector) throws Exception {
        connection = connectionFactory.createConnection();
        connection.setClientID(UUID.randomUUID().toString());
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        eventBus = session.createTopic("EventBus");
        return session.createDurableSubscriber(eventBus, "TestSubscriber", selector, true);
    }
    
    public Message listenOnEventBusWithSubscriber(MessageConsumer messageConsumer) throws Exception {
        return messageConsumer.receive(TIMEOUT);
    }
    
    public void close() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }
}
