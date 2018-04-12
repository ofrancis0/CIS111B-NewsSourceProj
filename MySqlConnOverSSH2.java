//package com.journaldev.java.ssh

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import java.sql.Connection;


public class MySqlConnOverSSH2 {

	/**
	 * Java Program to connect to remote database through SSH using port forwarding
	 * @author Pankaj@JournalDev
	 * @throws SQLException 
	 */
   public static void main(String[] args) throws SQLException {
   
      int lport=3305;
      String rhost="127.0.0.1";
      String host="104.131.100.240";
      int rport=3306;
      String user="chrissa";
      String password="LaP0r#e!";
      String dbuserName = "chrissa";
      String dbpassword = "LaP0r#e!";
      String url = "jdbc:mysql://127.0.0.1:"+lport+"/news_test";
      String driverName="com.mysql.jdbc.Driver";
      Connection conn = null;
      Session session= null;
      try{
       	//Set StrictHostKeyChecking property to no to avoid UnknownHostKey issue
         java.util.Properties config = new java.util.Properties(); 
         config.put("StrictHostKeyChecking", "no");
         JSch jsch = new JSch();
         session=jsch.getSession(user, host, 22);
         session.setPassword(password);
         session.setConfig(config);
         session.connect();
         System.out.println("Connected");
         int assinged_port=session.setPortForwardingL(lport, rhost, rport);
         System.out.println("localhost:"+assinged_port+" -> "+rhost+":"+rport);
         System.out.println("Port Forwarded");
       	
       	//mysql database connectivity
         Class.forName(driverName).newInstance();
         conn = DriverManager.getConnection (url, dbuserName, dbpassword);
         System.out.println ("Database connection established");
         System.out.println("DONE");
      }catch(Exception e){
         e.printStackTrace();
      }finally{
         if(conn != null && !conn.isClosed()){
            System.out.println("Closing Database Connection");
            conn.close();
         }
         if(session !=null && session.isConnected()){
            System.out.println("Closing SSH Connection");
            session.disconnect();
         }
      }
   }

}