package org.filetransfer.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*
Author: Alex Cogelja
Date: 12/29/2018
Purpose: The server which files are sent to, and retrieved from
 */

public class ServerMain{

    private int port = 1582; //port server runs on
    private ServerSocket serverSocket; //socket indicating server
    private Socket socket; //socket client connects to
    private int id = 0;
    private List<ClientThread> clients = new ArrayList<ClientThread>();

    HashMap<Integer, File> map;

    public ServerMain(int port){
        this.port = port;
    }

    private void startServer(){
        try{
            serverSocket = new ServerSocket(port);
            while(true){
                socket = serverSocket.accept();
                System.out.println("Client Connected");
                Runnable run = new ClientThread(socket, id++);
                Thread thread = new Thread(run);
                clients.add((ClientThread) run);
                thread.start();
                Thread.sleep(5);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ServerMain serverMain = new ServerMain(1582);
        serverMain.startServer();
    }

    private final class ClientThread implements Runnable{

        Socket socket; //socket client;
        DataInputStream fromClient; //thread to read data from client
        DataOutputStream toClient; //thread to write data to client
        int id;

        private ClientThread(Socket socket, int id){
            this.id = id;
            this.socket = socket;

            try{
                toClient = new DataOutputStream(socket.getOutputStream());
                fromClient = new DataInputStream(socket.getInputStream());
            }catch (Exception e){

            }
        }

        @Override
        public void run() {
            while(true){
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
