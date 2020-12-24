/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdfutils;

/**
 *
 * @author Tinashe
 */
public class DataExtractor {

    public String extract_data(String pageText, String start_tag, String end_tag) {
        //Remove extra spaces
        start_tag = start_tag.trim();
        end_tag = end_tag.trim();
        //Check if the tags exist in the pageText
        int start_tag_index = pageText.indexOf(start_tag);
        if (start_tag_index != -1) {
            if (end_tag.equalsIgnoreCase("Space")) {
                //End tag is the \n character
                //Get start_tag length
                int start_tag_len = start_tag.length();
                int end_tag_index = pageText.indexOf("\n", (start_tag_index + start_tag_len));
                String extracted_data = pageText.substring((start_tag_index + start_tag_len), end_tag_index);
                if (extracted_data != null) {
                    //Data extracted successfully
                    return extracted_data;
                } else {
                    //Failed to extract data from page text.
                    return "Error #4";
                }
            } else if (end_tag.equalsIgnoreCase("End")) {
                //Get data between start tag until end of pageText string
                //Get start_tag length
                int start_tag_len = start_tag.length();
                String extracted_data = pageText.substring((start_tag_index + start_tag_len));
                if (extracted_data != null) {
                    //Data extracted successfully
                    return extracted_data;
                } else {
                    //Failed to extract data from page text.
                    return "Error #4";
                }
            } else {
                //End tag is a normal tag, search for it
                int end_tag_index = pageText.indexOf(end_tag);
                if (end_tag_index != -1) {
                    //Check if start_tag occurs before end_tag
                    if (start_tag_index < end_tag_index) {
                        //Tags are correctly matched
                        //Get start_tag length
                        int start_tag_len = start_tag.length();
                        String extracted_data = pageText.substring((start_tag_index + start_tag_len), end_tag_index);
                        if (extracted_data != null) {
                            //Data extracted successfully
                            return extracted_data;
                        } else {
                            //Failed to extract data from page text.
                            return "Error #4";
                        }
                    } else {
                        //Tag mismatch, end_tag occurs before start_tag
                        return "Error #3";
                    }
                } else {
                    //End tag index not found
                    return "Error #2";
                }
            }
        } else {
            //Start tag index not found
            return "Error #1";
        }
    }

}
