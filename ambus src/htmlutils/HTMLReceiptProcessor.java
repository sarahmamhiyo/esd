/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package htmlutils;

import app.Config;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 *
 * @author Tinashe
 */
public class HTMLReceiptProcessor {
    /*
    public static void main(String[] args){
        String signature = extract_signature("0920009359");
        System.out.println(signature);
    }
    */
    
    public String extract_signature(String file_name) {
        String signature = read_receipt(file_name);
        return signature;
    }

    String read_receipt(String file_name) {
        String path_name = Config.HTML_RECEIPTS_PATH + file_name;
        String signature = null;
        try {
            File myObj = new File(path_name);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                if(data.startsWith("<p>Signature: <br />")){
                    //System.out.println(data);
                    signature = data.replace("<p>Signature: <br />", "").replace("</p>", "").trim();
                    break;
                }
            }
            myReader.close();
            return signature;
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
            return null;
        }
    }
}
