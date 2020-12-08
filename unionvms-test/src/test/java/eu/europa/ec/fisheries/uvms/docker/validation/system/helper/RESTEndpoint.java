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

import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import un.unece.uncefact.data.standard.fluxvesselpositionmessage._4.FLUXVesselPositionMessage;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

public class RESTEndpoint implements Closeable {

    public static int ENDPOINT_PORT = 29001;
    
    private Server server;
    private static FLUXVesselPositionMessage message;
    
    public RESTEndpoint() throws Exception {
        this(ENDPOINT_PORT);
    }
    
    public RESTEndpoint(int port) throws Exception {
        server = new Server(port);
       
        ServletHandler servletHandler = new ServletHandler();
        servletHandler.addServletWithMapping(RestServlet.class, "/*");
        server.setHandler(servletHandler);
        // Start Server
        server.start();
    }
    
    public FLUXVesselPositionMessage getFLUXMessage() {
        return message;
    }
    
    public FLUXVesselPositionMessage getMessage(int timeoutInMillis) throws InterruptedException {
        while (message == null && timeoutInMillis > 0) {
            TimeUnit.MILLISECONDS.sleep(100);
            timeoutInMillis -= 100;
        }
        FLUXVesselPositionMessage returnMessage = message;
        message = null;
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
    public static class RestServlet extends HttpServlet {
        
        @Override
        protected void doPost(HttpServletRequest httpRequest, HttpServletResponse response) throws ServletException, IOException {
            try {
                StringBuffer output = new StringBuffer();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpRequest.getInputStream()));
                String line;
                while ((line = bufferedReader.readLine()) != null) {  
                    output.append(line);
                }
                message = JAXBMarshaller.unmarshallString(output.toString(), FLUXVesselPositionMessage.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
