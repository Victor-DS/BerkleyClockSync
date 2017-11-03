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
package sincronizaçãorelogios;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import sincronizaçãorelogios.model.Berkley;
import sincronizaçãorelogios.model.Machine;
import sincronizaçãorelogios.utils.Command;
import sincronizaçãorelogios.utils.DateUtils;

/**
 *
 * @author Victor Santiago
 */
public class Master {
    
    final static Berkley berkley = new Berkley();
    
    static Map<Machine, Date> slaves;
    
    public static void main(String args[]) throws Exception {
        DatagramSocket serverSocket = new DatagramSocket(Config.MASTER_PORT);
        byte[] receiveData = new byte[1024];
        byte[] sendData = new byte[1024];
        slaves = new HashMap<Machine, Date>() {
            @Override
            public Date get(Object key) {
                return super.get(key) == null ? new Date() : super.get(key);
            }
            
        };
        
        long nextSync = System.currentTimeMillis();
        while (true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, 
                    receiveData.length);
            serverSocket.receive(receivePacket);

            String command = new String(receivePacket.getData());
            if (command.equals(Command.INITIALIZE)) {
                System.out.println("Adding slave to list...");
                slaves.put(new Machine(receivePacket.getAddress(), 
                        receivePacket.getPort()), new Date());
            }
            
            if (System.currentTimeMillis() < nextSync) {
                continue;
            }
            
            System.out.println("Time to sync...");
            
            sendData = Command.SEND_TIME.getBytes();
            
            System.out.println("Sending command to slaves...");
            for (Machine slave : slaves.keySet()) {
                DatagramPacket sendPacket
                        = new DatagramPacket(sendData, sendData.length, 
                                slave.getAddress(), slave.getPort());
                serverSocket.send(sendPacket);
            }
            
            long windowEndingTime = System.currentTimeMillis() + Config.SYNC_WINDOW;
            List<Date> times = new ArrayList<>();
            Machine m;
            while (System.currentTimeMillis() < windowEndingTime) {
                serverSocket.receive(receivePacket);
    
                if (receivePacket.getData().length > 0) {
                    System.out.println("Received data from slave...");
                    String receivedTime = new String(receivePacket.getData());
                    m = new Machine(receivePacket.getAddress(), receivePacket.getPort());
                    times.add(DateUtils.toDate(receivedTime));
                }
            }
            
            Date adjustedDate;
            System.out.println("Sending adjusted dates...");
            for (Machine slave : slaves.keySet()) {
                adjustedDate = berkley.getNewDate(times, slaves.get(slave), new Date());
                DatagramPacket sendPacket = new DatagramPacket(sendData, 
                        sendData.length, slave.getAddress(), slave.getPort());
                serverSocket.send(sendPacket);
            }
                        
            nextSync = System.currentTimeMillis() + Config.SYNC_INTERVAL;
        }
    }

}
