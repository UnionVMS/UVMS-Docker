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
package eu.europa.ec.fisheries.uvms.docker.validation.system.helper;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPMessage;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import xeu.connector_bridge.v1.PostMsgType;

public class FLUXEndpoint implements Closeable {

    public static int ENDPOINT_PORT = 29001;
    
    private Server server;
    private static PostMsgType message;
    private static Map<String, String> headers;
    
    public FLUXEndpoint() throws Exception {
        this(ENDPOINT_PORT);
    }
    
    public FLUXEndpoint(int port) throws Exception {
        server = new Server(port);
       
        ServletHandler servletHandler = new ServletHandler();
        servletHandler.addServletWithMapping(FLUXServlet.class, "/*");
        server.setHandler(servletHandler);
        // Start Server
        server.start();
    }
    
    public PostMsgType getFLUXMessage() {
        return message;
    }
    
    public PostMsgType getMessage(int timeoutInMillis) throws InterruptedException {
        while (message == null && timeoutInMillis > 0) {
            TimeUnit.MILLISECONDS.sleep(100);
            timeoutInMillis -= 100;
        }
        PostMsgType returnMessage = message;
        message = null;
        return returnMessage;
    }
    
    public Map<String, String> getHeaders(int timeoutInMillis) throws InterruptedException {
        while (message == null && timeoutInMillis > 0) {
            TimeUnit.MILLISECONDS.sleep(100);
            timeoutInMillis -= 100;
        }
        Map<String, String> returnMessage = headers;
        headers = null;
        return returnMessage;
    }

    @Override
    public void close() throws IOException {
        if (server != null) {
            try {
                server.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    @SuppressWarnings("serial")
    public static class FLUXServlet extends HttpServlet {
        
        @Override
        protected void doPost(HttpServletRequest httpRequest, HttpServletResponse response) throws ServletException, IOException {
            try {
                SOAPMessage soapMessage = MessageFactory.newInstance().createMessage(new MimeHeaders(), httpRequest.getInputStream());
                Unmarshaller unmarshaller = JAXBContext.newInstance(PostMsgType.class).createUnmarshaller();
                JAXBElement<PostMsgType> postMsg = unmarshaller.unmarshal(soapMessage.getSOAPBody().extractContentAsDocument(), PostMsgType.class);
                message = postMsg.getValue();
                headers = Collections
                        .list(httpRequest.getHeaderNames())
                        .stream()
                        .collect(Collectors.toMap(h -> h, httpRequest::getHeader));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
