/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import app.Config;
import java.io.File;
import java.io.FilenameFilter;

/**
 *
 * @author Tinashe
 */
public class RenameFiles {
    
    public static void main(String[] args){
        rename_files(".sen", ".pdf");
    }

    static void rename_files(String from, String to) {
        File f = new File(Config.SAP_INVOICES_PATH);
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File f, String name) {
                // We want to find only .c files
                return name.endsWith(from);
            }
        };

        File[] files = f.listFiles(filter);
        //Rename files
        for(int i=0;i<files.length;i++){
            File current_file = files[i];
            String current_file_path_name = current_file.getAbsolutePath();
            //Change the extension
            String new_file_path_name = current_file_path_name.replace(from, to);
            //Create new file
            File new_file = new File(new_file_path_name);
            //Rename the old file to ne one
            if(current_file.renameTo(new_file)){
                System.out.println(current_file.getName() + " renamed to " + new_file.getName());
            }else{
                System.out.println("Failed tp rename " + current_file.getName());
            }
            
        }
    }
}
