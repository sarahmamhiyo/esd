/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dbutils;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author Tinashe
 */
public class Db_Connector {
    public static void main(String[] args) {
	Db_Connector obj_DB_Connection=new Db_Connector();
	Connection connection=null;
	connection=obj_DB_Connection.get_connection();
	System.out.println(connection);
     }
     public Connection get_connection(){
	Connection connection=null;
	try{
		Class.forName("com.mysql.jdbc.Driver");
		connection=DriverManager.getConnection("jdbc:mysql://173.255.201.135:3306/mgicozw_carldb","mgicozw_tin","Kujoka@2020");
	}catch (Exception e) {
		System.out.println(e);
	}
	return connection;
     }
}
