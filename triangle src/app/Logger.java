/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.logging.Level;
import pdfutils.PRNGenerator;

/**
 *
 * @author Tinashe
 */
public class Logger {
    String TAG = "Logger";
    
    public boolean writeLog(String file_name, ArrayList<String> info_lines) {
        file_name = file_name.replace(".pdf", "_error.txt").replace(".sen", "_error.txt").replace(".dup", "_error.txt");
        System.out.println(TAG + "Log file_name: " + file_name);
        PrintWriter writer = null;
        String file_path_name = Config.LOGS_DIR_PATH + "\\" + file_name;
        try {
            writer = new PrintWriter(file_path_name, "UTF-8");
            for (int i = 0; i < info_lines.size(); i++) {
                writer.println(info_lines.get(i) + "\n");
            }
            writer.close();
            return true;
        } catch (FileNotFoundException ex) {
            java.util.logging.Logger.getLogger(PRNGenerator.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (UnsupportedEncodingException ex) {
            java.util.logging.Logger.getLogger(PRNGenerator.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } finally {
            writer.close();
        }

    }
    
    public boolean deleteLog(String file_name){
        String file_path_name = Config.LOGS_DIR_PATH + "\\" + file_name;
        File logFile = new File(file_path_name);
        if(logFile.exists()){
            if(logFile.delete()){
                return true;
            }else{
                return false;
            }
        }else{
            return true;
        }
    }
    
}
