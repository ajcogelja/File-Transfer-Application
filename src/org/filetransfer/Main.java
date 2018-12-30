package org.filetransfer;

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

/*
Author: Alex Cogelja
Date: 12/29/2018
Purpose: A program which can get and transfer files
 */

public class Main extends Application {

    //Initialized parts of javafx window
    private Scene scene;
    private Pane pane;
    private Button inputPath;
    private TextArea textbox;
    private String fileList;
    private FolderGetter fg;
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
        textbox.setLayoutX(50);
        textbox.setLayoutY(60);
        textbox.setEditable(false);

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

        //add all of the objects
        pane.getChildren().add(inputArea);
        pane.getChildren().add(textbox);
        pane.getChildren().add(path);
        pane.getChildren().add(inputPath);

        scene = new Scene(pane, 600, 600);
        scene.setFill(Color.BEIGE);

        //set the scene
        primaryStage.setScene(scene);
        primaryStage.setTitle("File Transfer Client");
        primaryStage.show();
    }
}
