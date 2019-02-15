package org.filetransfer.client;

import javafx.application.*;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

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
    private Button upload;
    private TextArea serverList;
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
        path.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && path.getText().length() > 0){
                fg = new FolderGetter(path.getText());
                fg.openFolder();
                fileList = fg.listFiles();
                textbox.setText(fileList);
            }
        });

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
        inputPath.setOnMouseClicked(event -> {
            System.out.println("Clicked");
            if (path.getText().length() > 0 ){
                fg = new FolderGetter(path.getText());
                fg.openFolder();
                fileList = fg.listFiles();
                textbox.setText(fileList);
            }
        });

        connect = new Button("Connect to Server");
        connect.setLayoutX(textbox.getLayoutX() + 250);
        connect.setLayoutY(textbox.getLayoutY() + 10);
        connect.setOnMouseClicked(event -> {
            if (!connected){
                client = new Client("127.0.0.1", 1582);
                client.openConnection();
                if(client != null && client.isConnected()) {
                    System.out.println("Connected!");
                    connected = true;
                }
            } else {
                System.out.println("Already Connected to a Server");
            }
        });

        disconnect = new Button("Disconnect");
        disconnect.setLayoutX(connect.getLayoutX() + 160);
        disconnect.setLayoutY(connect.getLayoutY());
        disconnect.setOnMouseClicked(event -> {
            if (connected){
                client.disconnect();
            } else {
                System.out.println("Not Connected");
            }
        });

        upload = new Button("Upload File");
        upload.setLayoutX(connect.getLayoutX());
        upload.setLayoutY(connect.getLayoutY() + 40);

        TextField selectFile = new TextField();
        selectFile.setLayoutX(disconnect.getLayoutX() - 40);
        selectFile.setLayoutY(upload.getLayoutY());

        upload.setOnMouseClicked(event -> {
            if (connected && selectFile.getText().length() > 0){
                String filename = selectFile.getText();
                File file = fg.getFile(filename);
                System.out.println(file.getName());
                client.upload(fg.getFile(selectFile.getText()));
            } else {

            }
        });

        serverList = new TextArea();
        serverList.setLayoutY(upload.getLayoutY() + 40);
        serverList.setLayoutX(upload.getLayoutX());
        serverList.setMaxWidth(200);
        serverList.setMinWidth(150);
        serverList.setMinHeight(350);
        serverList.setMaxHeight(350);
        serverList.setEditable(false);

        //add all of the objects
        pane.getChildren().add(inputArea);
        pane.getChildren().add(textbox);
        pane.getChildren().add(path);
        pane.getChildren().add(inputPath);
        pane.getChildren().add(connect);
        pane.getChildren().add(disconnect);
        pane.getChildren().add(files);
        pane.getChildren().add(upload);
        pane.getChildren().add(selectFile);
        pane.getChildren().add(serverList);

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

        public boolean isConnected(){
            if (socket != null && socket.isConnected()){
                return true;
            } else {
                return false;
            }
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
                    if (connected) {
                        serverList.clear();
                        List<String> sFiles = getServerFiles();
                        if(sFiles != null) {
                            for (String s : sFiles) {
                                serverList.appendText(s + "\n");
                            }
                        }
                    }

                    while(true){ //this is what we want to listen for
                        try {
                            sleep(200);
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
                    toServer.writeInt(-2);
                    socket.close();
                    fromServer.close();
                    toServer.close();
                    connected = false;
                    System.out.println("Disconnected Successfully");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }

        public List<String> getServerFiles(){
            if(connected){
                List<String> serverFiles = new ArrayList<>();
                try {
                    toServer.writeInt(2);
                    int reading;
                    while((reading = fromServer.readInt()) != -1){
                        String input = fromServer.readUTF();
                        serverFiles.add(input);
                    }
                    toServer.writeInt(-1);
                    return serverFiles;
                } catch (Exception e){
                    e.printStackTrace();
                }

            } else {
                System.out.println("Cannot retrieve list, no connection to Server");
            }
            return null;
        }

        public void retrieve(File file){

        }

        public boolean upload(File file){
            if (file != null && connected){
                try {
                    FileInputStream getter = new FileInputStream(file);
                    //Need to indicate server should read a file
                    //IT WORKS!!!
                    System.out.println("Starting sending data");
                    toServer.writeInt(1);
                    toServer.writeUTF(file.getName());
                    int data;
                    while((data = getter.read()) != -1) {
                        toServer.writeInt(data);
                    }
                    toServer.writeInt(-1); //-1 indicates end of trans.
                    System.out.println("Finished sending data");
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return false;
        }

    }

}
