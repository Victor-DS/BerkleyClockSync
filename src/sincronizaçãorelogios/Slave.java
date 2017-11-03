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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import sincronizaçãorelogios.utils.Command;
import sincronizaçãorelogios.utils.DateUtils;

/**
 *
 * @author Victor Santiago
 */
public class Slave {

    public static void main(String args[]) throws Exception {
        DatagramSocket clientSocket = new DatagramSocket();
        byte[] sendData;
        String time;
        
        initialize(clientSocket);

        while (true) {
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, 
                    receiveData.length);
            clientSocket.receive(receivePacket);
            String command = new String(receivePacket.getData());
            
            if (command.equals(Command.SEND_TIME)) {
                time = DateUtils.toString(DateUtils.getRandomTime());
                System.out.println("Sending " + time);
                
                sendData = time.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, 
                        sendData.length, receivePacket.getAddress(), 
                        receivePacket.getPort());
                clientSocket.send(sendPacket);
            } else {
                System.out.println("Adjusting clock to " + new String(receivePacket.getData()));
            }
//            clientSocket.close();
        }
    }
    
    /**
     * Initializes the slave by sending a message to the Master 
     * so it can have its address.
     * 
     * @param socket Server socket.
     * @throws UnknownHostException In case the host is not found.
     * @throws IOException In case there's a connection problem.
     */
    public static void initialize(DatagramSocket socket) 
            throws UnknownHostException, IOException {
        System.out.println("Initializing slave...");
        
        byte[] data = Command.INITIALIZE.getBytes();
        
        InetAddress IPAddress = InetAddress.getByName("localhost");

        DatagramPacket sendPacket = new DatagramPacket(data, data.length, 
                IPAddress, Config.MASTER_PORT);
        
        socket.send(sendPacket);
    }

}
