package org.filetransfer.server;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerMain extends Application {

    //JavaFX objects
    private Scene scene;
    private Pane pane;
    private Button beginServer;
    private int port = 1582; //port server runs on
    private ServerSocket serverSocket; //socket indicating server
    private Socket socket; //socket client connects to
    private int id = 0;
    private List<ClientThread> clients = new ArrayList<ClientThread>();


    private void startServer(){
        try{
            serverSocket = new ServerSocket(port);
            while(true){
                Thread.sleep(5);
                socket = serverSocket.accept();
                Runnable run = new ClientThread(socket, id++);
                Thread thread = new Thread(run);
                clients.add((ClientThread) run);
                thread.start();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        //Initialize required parameters
        pane = new Pane();
        beginServer = new Button("Start Server");
        beginServer.setLayoutY(20);
        beginServer.setLayoutX(20);
        beginServer.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                startServer();
            }
        });

        //add all objects to the scene
        pane.getChildren().add(beginServer);

        //Opens and displays the window
        scene = new Scene(pane, 600, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
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
