/*
 * Server App upon TCP
 * A thread is started to handle every client TCP connection to this server
 * Model by Weiying Zhu
 * Project modifications by Brittany Bianco
 * 
 */ 

import java.net.*;
import java.io.*;
import java.util.Date;
import java.util.Calendar;

public class HW3MultiServerThread extends Thread {
   private Socket clientTCPSocket = null;

   public HW3MultiServerThread(Socket socket) {
		super("HW3MultiServerThread");
		clientTCPSocket = socket;
   }
   
   //-------------------------------------------------
   
   public void run() {
      System.out.println("Beginning communication. \n");
      
		try {
	 	   PrintWriter cSocketOut = new PrintWriter(clientTCPSocket.getOutputStream(), true);
	  		BufferedReader cSocketIn = new BufferedReader(new InputStreamReader(clientTCPSocket.getInputStream()));
         
         System.out.println("Client connected. \n");

	      String fromClient, 
                toClient, 
                totalReceived;  // raw client input
         String[] lines,    // the client input split by newlines
                  command;  // the first line of input, split by spaces
         int countNewlines;  // counter to determine when input has ended
         
         try {
            do {
               // Reset reusable variables
               totalReceived = "";
               countNewlines = 0;
               
               // Take client input
      	 	   while ((fromClient = cSocketIn.readLine()) != null && countNewlines < 2) {
      				System.out.println(fromClient);
                  totalReceived += fromClient + "\r\n";  // because .readLine() strips the newline off the end
                  
                  if (fromClient.length() == 0) ++countNewlines;
                  else                          countNewlines = 0;  // must be consecutive
      	 	   }
               
               // Build response to client
               lines = totalReceived.split("\r\n");
               command = lines[0].split(" ");
               toClient = buildResponse(command[2]) + determineCase(command);
               
               // Send Response
               System.out.println("Communicating to client. \n");
      			cSocketOut.write(toClient);  // 4 blank lines inform the client the message is finished
               //cSocketOut.println("\r\n\r\n\r\n\r\n\r\n");
               cSocketOut.flush();
               System.out.println("Communication completed. \n");
               
               System.out.println();
            } while (fromClient != null);
            
         } catch (NullPointerException ne) {
            System.out.println("Client likely disconnected.");
         
         } catch (ArrayIndexOutOfBoundsException ae) {
            System.out.println("Client disconnected.");
         
         } catch (SocketException se) {
            System.out.println("Client was likely manually stopped by user.");
            
         } finally {
   		   cSocketOut.close();
   		   cSocketIn.close();
   		   clientTCPSocket.close();
         }
         
		} catch (IOException ie) {
		   ie.printStackTrace();
		}
    }
    
    //-------------------------------------------------
    
    public static String determineCase(String[] c) {
        if (c.length != 3) {  // this should never run
           System.out.println("Command length: " + c.length);
           return "Wrong command length.\r\n\r\n\r\n \r\n\r\n\r\n\r\n\r\n\r\n";
           // This number of blank lines is to signal to the client
           // when it is done reading content from the server.
           // Within the legitimate file, these blank lines are already present.
        }
        
        if (!c[0].equals("GET")) {
           System.out.println("400 Bad Request: " + c[0]);
           return "400 Bad Request\r\n\r\n\r\n \r\n\r\n\r\n\r\n\r\n\r\n";
        }
        
        BufferedReader inFile;
        String response = "";
        
        try { // opening the file given at c[1]
        
            // Remove slash prefix on file name
            char[] address = c[1].toCharArray();
            String fileName = "";
            for (int i = 1; i < address.length; i++) {
               fileName += address[i];
            }
            
            // Open file
            System.out.println("Opening file " + fileName + "...");
            inFile = new BufferedReader(new FileReader(fileName));
            
            response += "200 OK\r\n\r\n\r\n";  // 2 blank lines signal to the client the page's beginning
            String line = "";
            
            // Read and record file
            while ((line = inFile.readLine()) != null) {
                response += line + "\r\n";  // because .readLine() strips the newline off the end
            }
            response += "</body></html>";
            
            inFile.close();
        } catch (Exception e) {
            return "404 Not Found\r\n\r\n\r\n \r\n\r\n\r\n\r\n\r\n\r\n";
        }
        
        return response;
    }
    
    //-------------------------------------------------
    
    public static String buildResponse(String http) {
        Date today = Calendar.getInstance().getTime();
        String status = "5050 Connected";
        
        return (http + "\r\n"
                + "Date: " + today.toString() + "\r\n"
                + "Server: " + status + "\r\n"
                + "\r\n");
    }
}