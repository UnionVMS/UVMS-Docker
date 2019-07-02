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
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;

public class NafEndpoint implements Closeable {

    private Server server;
    private static String message;
    
    public NafEndpoint(int port) throws Exception {
        server = new Server(port);
       
        ServletHandler servletHandler = new ServletHandler();
        servletHandler.addServletWithMapping(NafServlet.class, "/*");
        server.setHandler(servletHandler);
        // Start Server
        server.start();
    }
    
    public String getNafMessage() {
        return message;
    }
    
    public String getMessage(int timeoutInMillis) throws InterruptedException {
        while (message == null && timeoutInMillis > 0) {
            TimeUnit.MILLISECONDS.sleep(100);
            timeoutInMillis -= 100;
        }
        return message;
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
    public static class NafServlet extends HttpServlet {
        
        @Override
        protected void doGet(HttpServletRequest httpRequest, HttpServletResponse response) throws ServletException, IOException {
            StringBuffer url = httpRequest.getRequestURL();
            String[] parts = url.toString().split("/");
            String urlMessage = parts[parts.length - 1];
            urlMessage = URLDecoder.decode(urlMessage, "iso-8859-1");
            message = new String(urlMessage.getBytes("ISO-8859-1"), "UTF-8");
            response.setStatus(200);
            PrintWriter out = response.getWriter();
            out.println("OK");
            out.close();
        }
    }
}