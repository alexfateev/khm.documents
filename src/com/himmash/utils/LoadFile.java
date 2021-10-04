package com.himmash.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class LoadFile extends Thread {

    private List<File> sourceFiles;
    private String path;
    private Path pp;
    private boolean status = false;

    public LoadFile(String name) {
        super(name);
    }

    public void setPath(String path){
        this.path = path;
    }

    public void setPp(Path path){
        pp = path;
    }

    public void setSourceFiles(List<File> sourceFiles){
        this.sourceFiles = sourceFiles;
    }

    public boolean getStatus(){
        return status;
    }

    @Override
    public void run() {
        if (Files.notExists(Paths.get(path))){
            try {
                Files.createDirectories(Paths.get(path));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for(File file: sourceFiles){
            try {
                Files.copy(Paths.get(file.getAbsolutePath()),Paths.get(path+"\\"+file.getName()), StandardCopyOption.REPLACE_EXISTING);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Скопировано: "+sourceFiles.size());
        status = true;
    }


}
