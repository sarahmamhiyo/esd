/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tinashe
 */
public class GetMacAddress {
    static ArrayList<NetworkInterface> existing_cards = new ArrayList();
    static int loop =0;
    public static void main(String[] args) {
        get_net_info();
        
        //get_net_info();
    }
    
    static void get_net_info(){
        try {
            Enumeration<NetworkInterface> cards = NetworkInterface.getNetworkInterfaces();
            while(cards.hasMoreElements()){
                NetworkInterface current_interface = cards.nextElement();
                if(!current_interface.isLoopback() &&
                        !current_interface.isPointToPoint() && 
                        !current_interface.isVirtual() &&
                        (current_interface.getHardwareAddress() != null)
                        ){
                    if(loop == 0){
                        existing_cards.add(current_interface);
                        System.out.println("Interface: " + current_interface);
                    }
                    /*
                    if(current_interface.equals(existing_cards.get(0))){
                        System.out.println();
                        System.out.println("Existing Interface: " + existing_cards.get(0));
                        System.out.println("Discovered Interface: " + current_interface);
                    }
                            */
                    
                }
                
            }
        } catch (SocketException ex) {
            Logger.getLogger(GetMacAddress.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    static void getMac1() {
        InetAddress ip;
        try {

            ip = InetAddress.getLocalHost();
            System.out.println("Current IP address : " + ip.getHostAddress());

            NetworkInterface network = NetworkInterface.getByInetAddress(ip);

            byte[] mac = network.getHardwareAddress();

            System.out.print("Current MAC address : ");

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }
            System.out.println(sb.toString());

        } catch (UnknownHostException e) {

            e.printStackTrace();

        } catch (SocketException e) {

            e.printStackTrace();

        }
    }
}
