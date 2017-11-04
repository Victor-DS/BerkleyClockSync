/*
 * The MIT License
 *
 * Copyright 2017 Victor Santiago.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package sincronizaçãorelogios.utils;

import sincronizaçãorelogios.Config;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Collection of helper methods do send messages between Master-Slave.
 *
 * @author Victor Santiago
 */
public class SyncUtils {

    /**
     * Initializes the slave by sending a message to the Master
     * so it can have its address.
     *
     * @param socket Server socket.
     * @throws UnknownHostException In case the host is not found.
     * @throws IOException In case there's a connection problem.
     */
    public static void initialize(DatagramSocket socket)
            throws IOException {
        System.out.println("Initializing slave...");

        byte[] data = Command.INITIALIZE.getBytes();

        send(socket, data, Config.MASTER_PORT);
    }

    /**
     *
     * @param socket The socket that will be used to send the data.
     * @param data
     * @param port
     * @throws IOException
     */
    public static void send(DatagramSocket socket, byte[] data, int port) throws IOException {
        InetAddress IPAddress = InetAddress.getByName("localhost");

        DatagramPacket sendPacket = new DatagramPacket(data, data.length,
                IPAddress, port);

        socket.send(sendPacket);
    }

}
