/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tinashe
 */
public class PatternMatching {

    String regex = "^(\\d+(?:,\\d{1,2})?).*";

    public static void main(String[] args) {
        //PatternMatcher pmatch = new PatternMatcher();
        NumberFormat format = NumberFormat.getNumberInstance();

        try {
            Object value = format.parse("223.92 COMMENTS:");
            System.out.println("Value: " + value);
        } catch (ParseException ex) {
            Logger.getLogger(PatternMatching.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
