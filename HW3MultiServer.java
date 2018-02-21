/*
 * Server App upon TCP
 * A thread is created for each connection request from a client
 * So it can handle Multiple Client Connections at the same time
 * Model by Weiying Zhu
 * Project modifications by Brittany Bianco
 * 
 */ 

import java.net.*;
import java.io.*;

public class HW3MultiServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverTCPSocket = null;
        boolean listening = true;

        try {
            serverTCPSocket = new ServerSocket(5050);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 5050.");
            System.exit(-1);
        }

        while (listening){
	    		new HW3MultiServerThread(serverTCPSocket.accept()).start();
		  }
			
        serverTCPSocket.close();
    }
}