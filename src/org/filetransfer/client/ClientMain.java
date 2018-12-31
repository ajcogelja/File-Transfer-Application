package org.filetransfer.client;

import javafx.application.*;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.*;
import java.io.*;
import java.net.Socket;

/*
Author: Alex Cogelja
Date: 12/29/2018
Purpose: A program which can get and transfer files
 */

public class ClientMain extends Application {

    //Initialized parts of javafx window
    private Scene scene;
    private Pane pane;
    private Button inputPath;
    private Button connect;
    private Button disconnect;
    private TextArea textbox;
    private String fileList;

    //Handles reading and getting directory contents
    private FolderGetter fg;

    //Client to connect to the server
    private Client client;
    private int id = 0;
    private boolean connected = false;


    //Starts the application when it is run
    public static void main(String[] args) {
            launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        //Make Additional Components
        Text inputArea = new Text("Input Path: ");
        inputArea.setLayoutX(10);
        inputArea.setLayoutY(35);
        inputArea.setFont(Font.font("Verdana", 30));

        TextField path = new TextField();
        path.setMinWidth(320);
        path.setLayoutX(inputArea.getLayoutX() + 180);
        path.setLayoutY(12);
        path.setFont(Font.font("Verdana", 13));

        //initialize the private components from above
        pane = new Pane();
        textbox = new TextArea();
        textbox.setWrapText(true);
        textbox.setMinWidth(200);
        textbox.setMaxWidth(200);
        textbox.setMinHeight(350);
        textbox.setLayoutX(50);
        textbox.setLayoutY(90);
        textbox.setEditable(false);

        Text files = new Text("Directory Contents");
        files.setFont(Font.font("Verdana", 16));
        files.setLayoutX(textbox.getLayoutX() + 18);
        files.setLayoutY(textbox.getLayoutY() - 16);

        inputPath = new Button("Search");
        inputPath.setLayoutY(10);
        inputPath.setMinHeight(30);
        inputPath.setMinWidth(80);
        inputPath.setLayoutX(path.getLayoutX() + path.getMinWidth() + 5);
        inputPath.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("Clicked");
                if (path.getText().length() > 0 ){
                    fg = new FolderGetter(path.getText());
                    fg.openFolder();
                    fileList = fg.listFiles();
                    textbox.setText(fileList);
                }
            }
        });

        connect = new Button("Connect to Server");
        connect.setLayoutX(textbox.getLayoutX() + 250);
        connect.setLayoutY(textbox.getLayoutY() + 10);
        connect.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (!connected){
                    client = new Client("127.0.0.1", 1582);
                    client.openConnection();
                    System.out.println("Connected!");
                    connected = true;
                } else {
                    System.out.println("Already Connected to a Server");
                }
            }
        });

        disconnect = new Button("Disconnect");
        disconnect.setLayoutX(connect.getLayoutX() + 160);
        disconnect.setLayoutY(connect.getLayoutY());
        disconnect.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (connected){
                    client.disconnect();
                    connected = false;
                    System.out.println("Disconnected!");
                } else {
                    System.out.println("Not Connected");
                }
            }
        });

        //add all of the objects
        pane.getChildren().add(inputArea);
        pane.getChildren().add(textbox);
        pane.getChildren().add(path);
        pane.getChildren().add(inputPath);
        pane.getChildren().add(connect);
        pane.getChildren().add(disconnect);
        pane.getChildren().add(files);

        scene = new Scene(pane, 600, 600);
        scene.setFill(Color.BEIGE);

        //set the scene
        primaryStage.setScene(scene);
        primaryStage.setTitle("File Transfer Client");
        primaryStage.show();
    }

    final class Client{

        private Socket socket; //connects to the server
        private DataInputStream fromServer; //Writing from server
        private DataOutputStream toServer; //Writing to server
        private byte[] buffer = new byte[1024]; //buffers holds a kilobyte at a time
        private final int port;//port of server to connect to
        private String server; //IP Address

        private Client(String server, int port){
            this.port = port;
            this.server = server;
        }

        private boolean openConnection(){
            try{
                socket = new Socket(server, port);
                fromServer = new DataInputStream(socket.getInputStream());
                toServer = new DataOutputStream(socket.getOutputStream());

            }catch (Exception e){
                e.printStackTrace();
            }
            Thread listener = new Thread(){
                @Override
                public void run() {
                    //this loop should write to the server what we want to do
                    //if we want to send a file write byte 00000001
                    //if nothing write byte 00000000

                    while(true){ //this is what we want to listen for
                        try {
                            sleep(100);
                            System.out.println("Connected to a server");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }
            };

            listener.start();

            return true;
        }

        public boolean disconnect(){
            if (socket.isConnected()){
                try {
                    socket.close();
                    fromServer.close();
                    toServer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }

    }

}