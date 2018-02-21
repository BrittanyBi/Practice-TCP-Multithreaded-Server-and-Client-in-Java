/*
 * Client App upon TCP
 * Model by Weiying Zhu
 * Project modifications by Brittany Bianco
 *
 */ 

import java.io.*;
import java.net.*;

public class HW3Client {
   static String[][] quickTests = new String[][]{
      {"GIT", "/CS3700.htm", "1.1", "Firefox"},  // 400 Bad Request
      {"GET", "/CS370.htm", "1.1", "Firefox"},   // 404 Not Found
      {"GIT", "/CS370.htm", "1.1", "Firefox"},   // 400 Bad Request (not both)
      {"GET", "/CS3700.htm", "1.1", "Firefox"}   // 200 OK
   };
   static int testCounter = 0;
   
   public static void main(String[] args) throws IOException {

      Socket tcpSocket = null;
      PrintWriter socketOut = null,
                  outFile = null;
      BufferedReader socketIn = null;
      BufferedReader sysIn = new BufferedReader(new InputStreamReader(System.in));
      
      System.out.println("Please enter the DNS or IP address: ");
      String IP = sysIn.readLine();
      long start, finish;
      
      try {
         start = System.nanoTime();
         tcpSocket = new Socket(IP, 5050);
         socketOut = new PrintWriter(tcpSocket.getOutputStream(), true);
         socketIn = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));
         finish = System.nanoTime();
         System.out.println("Connection established in " + (finish-start)/1000000.0 + " ms\n");
         
      } catch (UnknownHostException e) {
         System.err.println("Don't know about host: " + IP);
         System.exit(1);
      } catch (IOException e) {
         System.err.println("Couldn't get I/O for the connection to: "  + IP);
         System.exit(1);
      }

      String fromServer;
      String fromUser;
      
      String command, fileName, version, userAgent;
      int countNewlines;

      do {
         // Quick Testing:
         if(args.length > 0 && args[0].equals("true")){
            // Communicate with server:
            start = System.nanoTime();
            
            if(testCounter < 4){
               socketOut.println(buildRequest(IP, quickTests[testCounter][0], 
                                                  quickTests[testCounter][1], 
                                                  quickTests[testCounter][2], 
                                                  quickTests[testCounter][3]));
            }
            outFile = new PrintWriter(new File(quickTests[testCounter][1].substring(1)));
            testCounter++;
         } else {
            // Get information:
            System.out.println("Please enter the HTTP method type: ");
            command = sysIn.readLine();
            System.out.println("Please enter the HTML file name: ");
            fileName = sysIn.readLine();
            System.out.println("Please enter the HTTP version: ");
            version = sysIn.readLine();
            System.out.println("Please enter the User-Agent: ");
            userAgent = sysIn.readLine();
            
            outFile = new PrintWriter(new File(fileName));
            
            // Communicate with server:
            start = System.nanoTime();
            socketOut.println(buildRequest(IP, command, "/" + fileName, version, userAgent));
         }
			fromServer = socketIn.readLine();  // header
         finish = System.nanoTime();
         
         // Print header and status lines: 
         System.out.println("\n" + fromServer);
         // Warning - Do not write status and header lines to output file.
         for (int i = 0; i < 5; i++) {  // Date, Status, blank, Case
            System.out.println(socketIn.readLine());
         }
         
         
         // Receive remaining response, writing all to output file:
         countNewlines = 0;
         while ((fromServer = socketIn.readLine()) != null && countNewlines < 3) {
            System.out.println(fromServer);
            outFile.println(fromServer);
            
            if (fromServer.length() == 0) ++countNewlines;
            else                          countNewlines = 0;  // must be consecutive
         }
         String lastLine = socketIn.readLine();
         System.out.println(lastLine);  // In '200 OK' case, should be "\r\n</body></html>". 
                                                   // In any other case, should be "\r\n".
         outFile.println(lastLine);
         
         System.out.println("Response received in " + (finish-start)/1000000.0 + " ms");
         
         
         System.out.println("Do you want to continue?");
      } while ((fromUser = sysIn.readLine()).toLowerCase().equals("yes"));
      
      socketOut.close();
      socketIn.close();
      sysIn.close();
      tcpSocket.close();
      outFile.close();
   }
   
   public static String buildRequest(String ip, String c, String f, String v, String u) {
      return (c + " " + f + " HTTP/" + v + "\r\n"
              + "Host: " + ip            + "\r\n"
              + "User-Agent: " + u       + "\r\n"
                                         + "\r\n\r\n");
   }
}