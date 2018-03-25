/*
 Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.europa.ec.fisheries.uvms.docker.validation.plugins.fluxJMS;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;

import eu.europa.ec.fisheries.uvms.commons.date.XMLDateUtils;
import org.junit.Ignore;
import org.junit.Test;

public class FluxActivityJMSTestIT {

    private static final String QUERY = "src/test/resources/testData/faQueryMessage.xml";
    private static final String REPORT = "src/test/resources/testData/faReportMessage.xml";
    private static final String RESPONSE = "src/test/resources/testData/fluxResponseMessage.xml";
    private static final String MDR_RETURN_MSG = "src/test/resources/testData/mdrResponseMsg.xml";
    private static final String CONNECTOR_ID = "CONNECTOR_ID";
    private static final String CONNECTOR_ID_VAL = "JMS MDM Business AP1";
    private static final String FLUX_ENV_AD = "AD";
    private static final String FLUX_ENV_AD_VAL = "XEU";
    private static final String FLUX_ENV_DF = "DF";
    private static final String FLUX_ENV_DF_VAL = "urn:un:unece:uncefact:fisheries:FLUX:MDM:EU:2";
    private static final String FLUX_ENV_TODT = "TODT";
    private static final String FLUX_ENV_AR = "AR";
    private static final String FLUX_ENV_AR_VAL = "True";
    private static final String BUSINESS_UUID = "BUSINESS_UUID";
    private static final String ACTIVEMQ_JNDI_CONTEXT_FACTORY = "org.apache.activemq.jndi.ActiveMQInitialContextFactory";
    private static final String LOCALHOST_61616 = "tcp://localhost:61616";
    private static final String CONNECTION_FACTORY = "ConnectionFactory";
    private static final String FAPLUGIN_EVENT_QUEUE = "dynamicQueues/UVMSFAPluginEvent";
    private static final String MDRPLUGIN_EVENT_QUEUE = "dynamicQueues/UVMSMdrPluginEvent";

    private static PrintStream logger = System.out;

    @Test
    @Ignore
    public void sendReport(){
        sendMessageOnQueue(REPORT, FAPLUGIN_EVENT_QUEUE);
    }

    @Test
    @Ignore
    public void sendResponse(){
        sendMessageOnQueue(RESPONSE, FAPLUGIN_EVENT_QUEUE);
    }

    @Test
    @Ignore
    public void sendQuery(){
        sendMessageOnQueue(QUERY, FAPLUGIN_EVENT_QUEUE);
    }

    @Test
    @Ignore
    public void sendMdrReturnMessage(){
        sendMessageOnQueue(MDR_RETURN_MSG, MDRPLUGIN_EVENT_QUEUE);
    }

    public void sendMessageOnQueue(String filePath, String queue) {
        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;
        try {
            logger.println("Creating Context...");
            Properties props = new Properties();
            props.setProperty(Context.INITIAL_CONTEXT_FACTORY, ACTIVEMQ_JNDI_CONTEXT_FACTORY);
            props.setProperty(Context.PROVIDER_URL, LOCALHOST_61616);
            Context ctx = new InitialContext(props);
            logger.println("Context created..");
            ConnectionFactory connectionFactory = (ConnectionFactory) ctx.lookup(CONNECTION_FACTORY);
            logger.println("Connection factory created..");
            connection = connectionFactory.createConnection();
            connection.start();
            sendMessageToQueue(ctx, connection, queue, producer, session, filePath);
        } catch (Exception e) {
            logger.println("Exception: " +  e);
        } finally {
            disconnectQueue(connection, session, producer);
        }
    }

    private void sendMessageToQueue(Context ctx, Connection connection, String queue, MessageProducer producer, Session session, String filePath) throws NamingException, JMSException, IOException {
        Destination destination = (Destination) ctx.lookup(queue);
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        producer = session.createProducer(destination);
        TextMessage message = prepareMessage(readFile(filePath), session);
        logger.println("Sending xml message...");
        producer.send(message);
        producer.close();
        logger.println("Sent : " + message.getText());
    }

    private static TextMessage prepareMessage(String textMessage, Session session) throws JMSException {
        TextMessage fluxMsg = session.createTextMessage();
        fluxMsg.setText(textMessage);
        fluxMsg.setStringProperty(CONNECTOR_ID, CONNECTOR_ID_VAL);
        fluxMsg.setStringProperty(FLUX_ENV_AD, FLUX_ENV_AD_VAL);
        fluxMsg.setStringProperty(FLUX_ENV_DF, FLUX_ENV_DF_VAL);
        fluxMsg.setStringProperty("ON", "abc@abc.com");
        fluxMsg.setStringProperty(BUSINESS_UUID, UUID.randomUUID().toString());
        fluxMsg.setStringProperty(FLUX_ENV_TODT, XMLDateUtils.dateToXmlGregorian(new Date()).toString());
        fluxMsg.setStringProperty(FLUX_ENV_AR, FLUX_ENV_AR_VAL);
        fluxMsg.setStringProperty("FR", "XEU");
        return fluxMsg;
    }


    private static String readFile(String filePath) throws IOException {
        File xmlFile = new File(filePath);
        String xmlStrOutput = null;
        try (Reader fileReader = new FileReader(xmlFile)) {
            BufferedReader bufReader = new BufferedReader(fileReader);
            StringBuilder sb = new StringBuilder();
            String line = bufReader.readLine();
            while (line != null) {
                sb.append(line).append("\n");
                line = bufReader.readLine();
            }
            xmlStrOutput = sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return xmlStrOutput;
    }

    public static void disconnectQueue(final Connection connection, Session session, MessageProducer producer) {
        try {
            if (producer != null) {
                producer.close();
            }
            if (session != null) {
                session.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (final JMSException e) {
            logger.println("[ Error when closing JMS connection ] {}" + e.getMessage());
        }
    }

}
