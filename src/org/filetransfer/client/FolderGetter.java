package org.filetransfer.client;

/*
Author: Alex Cogelja
Date: 12/29/2018
Purpose: An object which takes input and returns the files in the directory
 */

import java.io.File;
import java.util.HashMap;

public class FolderGetter {
    //initialization of input and output requirements
    private String path; //path to a folder
    private StringBuilder output; //output format of all files
    private HashMap<Integer, File> map; //hashmap of all files in folder for efficient navigation
    private File folder;

    //Constructor
    public FolderGetter(String path){
        this.path = path;
        map = new HashMap<>();
        output = new StringBuilder();
    }

    //Returns true is the path is a folder, false if a file
    public boolean openFolder(){
        if (path == null){
            System.out.println("Invalid Path");
            return false;
        }
        try{
            folder = new File(path);
            if (folder.isDirectory()){
                for (File f:folder.listFiles()) {
                    map.put(hash(f.getName()), f);
                }
            } else {
                System.out.println("Path does not point to a directory");
                return false;
            }

        } catch (Exception e){
            e.printStackTrace();
        }

        return true;
    }

    //Gets a list of all the files in the folder
    public String listFiles(){
        if (!folder.isDirectory()){
            System.out.println("Path not a valid directory");
            return null;
        }
        for (File f: folder.listFiles()) {
            output.append(f.getName() + "\n");
        }
        return output.toString();
    }

    public int hash(String key){
        int hashVal;
        hashVal = key.hashCode();
        hashVal = hashVal % (map.size() + 1);
        return hashVal;
    }

    //get a file from the folder
    public File getFile(String name){
        return map.get(hash(name));
    }

}
