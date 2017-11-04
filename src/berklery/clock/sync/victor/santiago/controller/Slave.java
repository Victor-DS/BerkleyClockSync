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
package berklery.clock.sync.victor.santiago.controller;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import berklery.clock.sync.victor.santiago.utils.Config;
import berklery.clock.sync.victor.santiago.utils.Command;
import berklery.clock.sync.victor.santiago.utils.DateUtils;
import berklery.clock.sync.victor.santiago.utils.SyncUtils;

/**
 * Slave controller.
 *
 * @author Victor Santiago
 */
public class Slave {

    DatagramSocket clientSocket;

    public static void main(String args[]) throws Exception {
        Slave slave = new Slave(9800);
        slave.initialize();
        slave.execute();
    }

    public Slave(int port) throws SocketException, IllegalArgumentException {
        if (port == Config.MASTER_PORT) {
            throw new IllegalArgumentException("Port is the same as Master's!");
        }

        clientSocket = new DatagramSocket(port);
    }

    public void initialize() throws IOException {
        SyncUtils.initialize(clientSocket);
    }

    public void execute() throws IOException {
        DatagramPacket sendPacket;
        String time, command;
        byte[] receiveData;

        while (true) {
            receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData,
                    receiveData.length);

            System.out.println("Waiting for command...");
            clientSocket.receive(receivePacket);
            command = new String(receivePacket.getData()).trim();

            if (command.equals(Command.SEND_TIME)) {
                time = DateUtils.toString(DateUtils.getRandomTime());

                System.out.println("Sending " + time);

                SyncUtils.send(clientSocket, time.getBytes(), Config.MASTER_PORT);
            } else {
                System.out.println("Adjusting clock to " + new String(receivePacket.getData()).trim());
            }
        }
    }

    public void close() {
        clientSocket.close();
    }

}
