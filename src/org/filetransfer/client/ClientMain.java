package org.filetransfer.client;

import javafx.application.*;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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
    private Button getFile;
    private TextArea serverList;
    private TextArea textbox;
    private String fileList;

    //Handles reading and getting directory contents
    private FolderGetter fg;

    //Client to connect to the server
    private Client client;
    private int id = 0;
    private boolean connected = false;
    private final String DIRECTORYPATH = "src/org/filetransfer/client/contents/";

    public AtomicBoolean running = new AtomicBoolean(false);

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
        textbox.setLayoutX(60);
        textbox.setLayoutY(200);
        textbox.setEditable(false);

        Text direcContents = new Text("Directory Contents");
        direcContents.setFont(Font.font("Verdana", 16));
        direcContents.setLayoutX(textbox.getLayoutX() + 18);
        direcContents.setLayoutY(textbox.getLayoutY() - 16);

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
        connect.setFont(Font.font("Verdana", 16));
        connect.setLayoutX(inputArea.getLayoutX() + 30);
        connect.setLayoutY(inputArea.getLayoutY() + 25);
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
        disconnect.setFont(Font.font("Verdana", 16));
        disconnect.setLayoutX(connect.getLayoutX());
        disconnect.setLayoutY(connect.getLayoutY() + 50);
        disconnect.setOnMouseClicked(event -> {
            if (connected){
                client.disconnect();
            } else {
                System.out.println("Not Connected");
            }
        });

        upload = new Button("Upload File");
        upload.setFont(Font.font("Verdana", 16));
        upload.setLayoutX(connect.getLayoutX() + 240);
        upload.setLayoutY(connect.getLayoutY());

        TextField selectFile = new TextField();
        selectFile.setLayoutX(upload.getLayoutX() + 120);
        selectFile.setLayoutY(upload.getLayoutY());
        selectFile.setMinHeight(30);

        upload.setOnMouseClicked(event -> {
            if (connected && selectFile.getText().length() > 0){
                String filename = selectFile.getText();
                File file = fg.getFile(filename);
                System.out.println(file.getName());
                client.upload(fg.getFile(selectFile.getText()));
            } else {

            }
        });

        getFile = new Button("Get File");
        getFile.setFont(Font.font("Verdana", 16));
        getFile.setLayoutX(disconnect.getLayoutX() + 180);
        getFile.setLayoutY(disconnect.getLayoutY());

        TextField selectFromServer = new TextField();
        selectFromServer.setLayoutX(getFile.getLayoutX() + 100);
        selectFromServer.setLayoutY(getFile.getLayoutY());
        selectFromServer.setMinHeight(30);

        getFile.setOnMouseClicked(event -> {
            if (connected && selectFromServer.getText().length() > 0){
                String filename = selectFromServer.getText();
                client.retrieve(filename);
            } else {
                System.out.println("Not Connected or No Text Input");
            }
        });

        serverList = new TextArea();
        serverList.setLayoutY(textbox.getLayoutY());
        serverList.setLayoutX(textbox.getLayoutX() + textbox.getMinWidth() + 80);
        serverList.setMaxWidth(200);
        serverList.setMinWidth(200);
        serverList.setMinHeight(350);
        serverList.setMaxHeight(350);
        serverList.setEditable(false);

        Text serverContents = new Text("Server Contents");
        serverContents.setFont(Font.font("Verdana", 16));
        serverContents.setLayoutX(serverList.getLayoutX() + 24);
        serverContents.setLayoutY(serverList.getLayoutY() - 16);

        //add all of the objects
        pane.getChildren().add(inputArea);
        pane.getChildren().add(textbox);
        pane.getChildren().add(path);
        pane.getChildren().add(inputPath);
        pane.getChildren().add(connect);
        pane.getChildren().add(disconnect);
        pane.getChildren().add(direcContents);
        pane.getChildren().add(upload);
        pane.getChildren().add(selectFile);
        pane.getChildren().add(serverList);
        pane.getChildren().add(serverContents);
        pane.getChildren().add(getFile);
        pane.getChildren().add(selectFromServer);

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

        Thread listener = new Thread(){
            @Override
            public void run() {
                //this loop should write to the server what we want to do
                //if we want to send a file write byte 00000001
                //if nothing write byte 00000000

                while(true){ //this is what we want to listen for
                    try {
                        if (connected) {
                            serverList.clear();
                            List<String> sFiles = getServerFiles();
                            if(sFiles != null) {
                                for (String s : sFiles) {
                                    serverList.appendText(s + "\n");
                                }
                            }
                            sFiles.clear();
                        }
                        sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        };

        private boolean openConnection(){
            try{
                socket = new Socket(server, port);
                fromServer = new DataInputStream(socket.getInputStream());
                toServer = new DataOutputStream(socket.getOutputStream());

            }catch (Exception e){
                e.printStackTrace();
            }


            listener.start();

            return true;
        }

        public boolean disconnect(){
            while(running.get()); //infinite while loop while an application is running
            running.set(true);
            if (socket.isConnected()){
                try {
                    toServer.writeInt(-2);
                    socket.close();
                    fromServer.close();
                    toServer.close();
                    connected = false;
                    System.out.println("Disconnected Successfully");
                } catch (IOException e) {
                    running.set(false);
                    e.printStackTrace();
                }
            }
            running.set(false);
            return true;
        }

        public List<String> getServerFiles(){
            while(running.get());
            running.set(true);
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
                    running.set(false);
                    return serverFiles;
                } catch (Exception e){
                    running.set(false);
                    e.printStackTrace();
                }

            } else {
                System.out.println("Cannot retrieve list, no connection to Server");
            }
            running.set(false);
            return null;
        }

        public void retrieve(String name){ //pass the name of the file to retrieve
            if(name != null){
                File file = null;
                FileOutputStream fout = null;
                while (running.get());
                try {
                    toServer.writeInt(3); //indicate server usage
                    toServer.writeUTF(name); //name of file to be retrieved

                    //now read the file and store it locally
                    int data;
                    file = new File(DIRECTORYPATH + name);
                    if(!file.exists()){
                        file.createNewFile();
                    }
                    fout = new FileOutputStream(file);
                    while((data = fromServer.read()) != -1){
                        fout.write(data);
                    }
                    System.out.println("File Received");
                } catch (IOException e){
                    e.printStackTrace();
                    System.out.println("File Transmission Interrupted");
                    file.delete();
                    running.set(false);
                }
            }
            running.set(false);

        }

        public boolean upload(File file){
            if (file != null && connected){
                try {
                    FileInputStream getter = new FileInputStream(file);
                    //Need to indicate server should read a file
                    //IT WORKS!!!
                    System.out.println("Starting sending data");
                    while (running.get());
                    running.set(true);
                    toServer.writeInt(1);
                    toServer.writeUTF(file.getName());
                    int data;
                    while((data = getter.read()) != -1) {
                        toServer.writeInt(data);
                    }
                    toServer.writeInt(-1); //-1 indicates end of trans.
                    System.out.println("Finished sending data");
                    running.set(false);
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    running.set(false);
                }
            }
            running.set(false);
            return false;
        }

    }

}
