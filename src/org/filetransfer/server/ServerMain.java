package org.filetransfer.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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
    HashMap<String, File> map;

    public ServerMain(int port){
        this.port = port;
        map = new HashMap<>();
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

        public void read(String name){//works with absolute path
            File file = new File("src/org/filetransfer/server/contents/" +  name);
            FileOutputStream fout;
            try {
                if (!file.exists()) {
                    file.createNewFile();
                }
                fout = new FileOutputStream(file);
                int data;
                while((data = fromClient.readInt()) != -1){
                    fout.write(data);
                }
                fout.close();
                map.put(file.getName(), file);
            } catch (Exception e){
                System.out.println("Incomplete read, deleting file");
                file.delete();
            }
        }

        @Override
        public void run() {
            boolean running = true;
            while(running){
                try {
                    Thread.sleep(2);
                    int command = fromClient.readInt();
                    String name;
                    switch (command){
                        case 1: //get file
                            System.out.println("1 Received");
                            name = fromClient.readUTF();
                            System.out.println(name);
                            read(name);
                            break;
                        case -1:
                            System.out.println("End of File Received");
                            break;
                        case -2:
                            running = false;
                            System.out.println("Closing Client Thread");
                            break;

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
