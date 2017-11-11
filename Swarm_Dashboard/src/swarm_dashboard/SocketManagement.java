/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package swarm_dashboard;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;


/**
 *
 * @author Caleb
 * Sockets 5800-5810 are reserved for team use.
 * For 957, 5810 is reserved for the Arduino.
 * Socket 5800 is for receiving data from the Robot.
 * Socket 5801 is for sending data to the Robot, like Auto and light settings
 * 
 */
public class SocketManagement {
    
    Socket sSocket = null;
    Socket rSocket = null;
    DataOutputStream dout = null;
    DataInputStream din = null;
    boolean running = true;
    manageData manageData = new manageData();
    keepSocketsOnline keepSocketsOnline = new keepSocketsOnline();
    startSockets startSockets = new startSockets();
    String[] keys = null;
    String[] data = null;
    String ip;
    int socketR;
    int socketS;
    public void importKeys(String[] keys){
        this.keys = keys;
        data = new String[keys.length];
    }
    
    public void enableSockets(String ip, int socketR, int socketS){
        this.ip = ip;
        this.socketR = socketR;
        this.socketS = socketS;
        startSockets.start();
    }
    
    //Initalizes all sockets and data streams after making them null
    public class startSockets extends Thread{
        public void run(){
            running = true;
            sSocket = null;
            rSocket = null;
            dout = null;
            din = null;

            while(sSocket == null){
                try{
                    sSocket = new Socket(ip,socketS);
                }catch(Exception e){
                    sSocket = null;
                }
            }

            while(rSocket == null){
                try{
                    rSocket = new Socket(ip,socketR);
                }catch(Exception e){
                    rSocket = null;
                }
            }

            try {
                dout = new DataOutputStream(sSocket.getOutputStream());
                din = new DataInputStream(rSocket.getInputStream());
                dout.flush();
            } catch (IOException ex) {return;}

            manageData.start();
            keepSocketsOnline.start();
        }
    }
    
    //Overarching socket management, enables the main to grab data
    public class manageData extends Thread{
        public void run(){
            while(running){
                
                
              
                
            }
            running = true;
        }
    }
    
    //A thread meant to send sockets online after an unexpected disconnect
    //Re-launches socket communication after they have been shutdown completely
    public class keepSocketsOnline extends Thread{
        public void run(){
            boolean alive = true;
            while(alive){
                if(running == false){
                    while(running == false){}
                    running = true;
                    startSockets.start();
                    alive = false;
                }
            }
        }            
    }
    
    //A method to send strings via UDP
    public void sendString(String msg, DataOutputStream output){
        try{
           output.writeUTF(msg);  
           output.flush(); 
        }catch(IOException e){
            closeSockets();
        }
        return;
    }
    
    //A method to recive strings via UDP
    public String recieveString(){
        
        return "test";
    }
    
    //A method to close sockets in case of a disconnect
    public void closeSockets(){
        try {
            sSocket.close();
            rSocket.close();
            dout.close();
            din.close();
            running = false;
        } catch (IOException e) {
            running = false;
            return;      
        }      
    }
    
    public String requestData(String key){
        
        boolean match = false;
        int dataPoint = 0;
        String data = "NULL";
        if(data != null || keys != null){
            while(match = false){
                data = keys[dataPoint];
                if(data == key){
                    match = true;
                }
                if(dataPoint == keys.length || dataPoint > keys.length){
                    match = true;
                    data = "NULL";
                }
            } 
        }
        
        if(data == "NULL"){return "NULL";}else{return this.data[dataPoint];}    
    }
}
