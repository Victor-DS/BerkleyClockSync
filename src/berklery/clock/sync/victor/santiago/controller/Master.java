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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import berklery.clock.sync.victor.santiago.utils.Config;
import berklery.clock.sync.victor.santiago.model.Berkley;
import berklery.clock.sync.victor.santiago.model.Machine;
import berklery.clock.sync.victor.santiago.utils.Command;
import berklery.clock.sync.victor.santiago.utils.DateUtils;
import berklery.clock.sync.victor.santiago.utils.SyncUtils;

/**
 * Master Controller.
 *
 * @author Victor Santiago
 */
public class Master {
    
    private final Berkley berkley;
    private Map<Machine, Date> slaves;
    DatagramSocket serverSocket;

    private int nSlaves;

    public static void main(String args[]) throws Exception {
        Master master = new Master(2);
        master.execute();
    }

    public Master(int nSlaves) throws SocketException {
        berkley = new Berkley();

        slaves = new HashMap<Machine, Date>() {
            @Override
            public Date get(Object key) {
                return super.get(key) == null ? new Date() : super.get(key);
            }
        };

        serverSocket = new DatagramSocket(Config.MASTER_PORT);

        this.nSlaves = nSlaves;
    }

    public void execute() throws IOException, ParseException {
        long nextSync = System.currentTimeMillis();
        DatagramPacket receivePacket = null;
        byte[] receiveData = new byte[1024];

        while (true) {
            getAllSlaves();

            if (System.currentTimeMillis() < nextSync) {
                continue;
            }

            System.out.println("Time to sync! Sending command to slaves...");

            for (Machine slave : slaves.keySet()) {
                System.out.println("Sending to slave at port " + slave.getPort());
                SyncUtils.send(serverSocket, Command.SEND_TIME.getBytes(), slave.getPort());
            }

            long windowEndingTime = System.currentTimeMillis() + Config.SYNC_WINDOW;

            receivePacket = new DatagramPacket(receiveData,
                    receiveData.length);

            System.out.println("Messages sent. Waiting for dates...");

            List<Date> times = new ArrayList<>();
            nSlaves = slaves.size();
            String receivedTime;
            Machine m;
            while (nSlaves > 0 && System.currentTimeMillis() < windowEndingTime) {
                serverSocket.receive(receivePacket);

                if (receivePacket.getData().length > 0) {
                    System.out.println("Received data from slave...");
                    try {
                        receivedTime = new String(receivePacket.getData()).trim();
                        m = new Machine(receivePacket.getAddress(), receivePacket.getPort());
                        times.add(DateUtils.toDate(receivedTime));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                nSlaves--;
            }

            nSlaves = 0;

            System.out.println("Sending adjusted dates...");

            String adjustedDate;
            long dateDiff;
            for (Machine slave : slaves.keySet()) {
                adjustedDate = DateUtils.toString(berkley.getNewDate(times, slaves.get(slave), new Date()));
                dateDiff = DateUtils.getDifference(slaves.get(slave), DateUtils.toDate(adjustedDate));
                SyncUtils.send(serverSocket, (dateDiff+"").getBytes(), slave.getPort());
            }

            nextSync = System.currentTimeMillis() + Config.SYNC_INTERVAL;
        }
    }

    /**
     * Receives the information for the number os slaves specified and saves on the map.
     *
     * @throws IOException In case there's no connection.
     */
    private void getAllSlaves() throws IOException {
        if (nSlaves > 0) {
            System.out.println("Waiting for slaves...");
        }

        DatagramPacket receivePacket = null;
        byte[] receiveData = new byte[1024];

        while (nSlaves > 0) {
            receivePacket = new DatagramPacket(receiveData,
                    receiveData.length);
            serverSocket.receive(receivePacket);

            String command = new String(receivePacket.getData()).trim();
            if (command.equals(Command.INITIALIZE)) {
                System.out.println("Adding slave to list...");
                slaves.put(new Machine(receivePacket.getAddress(),
                        receivePacket.getPort()), new Date());
            }

            nSlaves--;
        }
    }

}
