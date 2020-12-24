/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package netutils;

import app.Config;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Tinashe
 */
public class NetworkManager implements Callable<JSONObject> {
    String action=null;
    Map<String, Object> params=null;
    
    public NetworkManager(String action, Map<String, Object> params){
        this.action = action;
        this.params = params;
    }
    
    
    @Override
    public JSONObject call() throws Exception {
        return submit(action, params);
    }
    
    public JSONObject submit(String action, Map<String, Object> params) {
        try {
            URL url = new URL(Config.ACTIVATION_SERVER + action);
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
            return responseObj;
        } catch (MalformedURLException ex) {
            Logger.getLogger(NetworkManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(NetworkManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (IOException ex) {
            Logger.getLogger(NetworkManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (ParseException ex) {
            Logger.getLogger(NetworkManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

    

    
    
}
