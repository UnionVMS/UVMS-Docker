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

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class LESMock implements Closeable {

    private CompletableFuture<List<String>> completableFuture;

    public LESMock(int port) {
        completableFuture = CompletableFuture.supplyAsync(() -> {
            try (ServerSocket server = new ServerSocket(port)) {
                Socket clientSocket = server.accept();
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                // Username & Password
                out.write("name:");
                out.flush();
                in.readLine();
                out.write("word:");
                out.flush();
                in.readLine();
                in.readLine();

                // Stop poll
                out.write(">");
                out.flush();

                String firstCommand = in.readLine();
                in.readLine();

                out.write("Text:");
                out.flush();
                in.readLine();
                in.readLine();
                out.write("ref number 1234");
                out.flush();

                // Config poll or QUIT if this is a Manual poll
                out.write(">");
                out.flush();

                String secondCommand = in.readLine();
                in.readLine();

                if ("quit".equalsIgnoreCase(secondCommand)) {
                    return Collections.singletonList(firstCommand);
                }

                out.write("Text:");
                out.flush();
                in.readLine();
                in.readLine();
                out.write("ref number 1234");
                out.flush();

                // Start poll
                out.write(">");
                out.flush();

                String thirdCommand = in.readLine();
                in.readLine();

                out.write("Text:");
                out.flush();
                in.readLine();
                in.readLine();
                out.write("ref number 1234");
                out.flush();

                // Config poll
                out.write(">");
                out.flush();

                in.readLine();

                return Arrays.asList(firstCommand, secondCommand, thirdCommand);
            } catch (Exception e) {
                return new ArrayList<>();
            }
        });
    }

    public List<String> getMessage(int timeoutInSeconds) {
        try {
            return completableFuture.get(timeoutInSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public void close() throws IOException {
        completableFuture.cancel(true);
    }
}