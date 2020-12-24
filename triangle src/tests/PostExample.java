/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author Tinashe
 */
public class PostExample {

    public static void main(String[] args) throws Exception {
        URL url = new URL("http://mgi.co.zw/register.php");
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("name", "Triangle Limited");
        params.put("OS_Type", "Win 8.1");
        params.put("UUID", "4weragahagat77899990");
        
        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (postData.length() != 0) {
                postData.append('&');
            }
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.setDoOutput(true);
        conn.getOutputStream().write(postDataBytes);

        Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        StringBuffer sb = new StringBuffer();
        for (int c; (c = in.read()) >= 0;) {
            sb.append((char) c);
            //System.out.print((char) c);
        }
        
        //System.out.print("Full response: " + sb);
        JSONParser parser = new JSONParser();
        JSONObject responseObj = (JSONObject) parser.parse(sb.toString());
        System.out.println("name: " + responseObj.get("name"));
        System.out.println("OS_Type: " + responseObj.get("OS_Type"));
        System.out.println("UUID: " + responseObj.get("UUID"));
        System.out.println("Status: " + responseObj.get("msg"));
        
        
    }
}
