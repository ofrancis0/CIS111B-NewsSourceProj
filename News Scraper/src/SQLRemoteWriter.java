import java.sql.*;

import com.jcraft.jsch.*;

/**
 * The SQLRemoteWriter Class extends the SQLReadWriter class, inheriting all the same methods,
 * except that the connect method is instead designed for a remote, rather than a local, 
 * connection to a SQL database.
 * 
 * This class is not used in our final project design, but was created as a fallback in the case
 * that we could not get the java package running server-side. In theory, it could be combined with
 * the SQLReadWriter class to generically read and write from SQL databases. Polymorphic combinations
 * of these two classes would allow a mix of local and remote databases to be used.
 * 
 * @author Aaron Wile, Chrissa LaPorte, Oriel Francis
 * @since 2 May 2018
 */

public class SQLRemoteWriter extends SQLReadWriter
{
	//------------------------------------------------------------------------------------------------------------
	// Instance Variables
	//------------------------------------------------------------------------------------------------------------
	
	private int localPort, remotePort;
	private String remoteHost, remoteIP, username, password;
	
	//------------------------------------------------------------------------------------------------------------
	// Constructor
	//------------------------------------------------------------------------------------------------------------
	
	/**
	 * The SQLRemoteWriter constructor calls super to the SQLReadWriter constructor, and then additionally
	 * accepts arguments for the lPort, rPort, host, rHost, username, and password fields to use for SSH 
	 * tunneling and port forwarding.
	 * 
	 * @param localPort An int containing the local port number to use for SSH tunneling.
	 * @param remotePort An int containing the remote port number to connect to.
	 * @param remoteHost A String containing the name of the remote Host to connect to.
	 * @param remoteIP A String containing the IP of the remote host to connect to.
	 * @param username A String containing the username required to connect to the remote host.
	 * @param password A String containing the password required to connect to the remote host.
	 * @param databaseName A String containing the name of the SQL database
	 * @param databaseAddress A String containing the address of the SQL database on the remote host.
	 */
	
	public SQLRemoteWriter(int localPort, int remotePort, String remoteHost, String remoteIP, String username, 
							String password, String databaseName, String databaseAddress)
	{
		//Call Super to SQLReadWriter
		super(databaseName, databaseAddress);
		
		//Instantiate Instance Variables with passed arguments
		this.localPort = localPort;
		this.remotePort = remotePort;
		this.remoteHost = remoteHost;
		this.remoteIP = remoteIP;
		this.username = username;
		this.password = password;
		
		//Call Connect method to ready this object's SQL Connection
		this.connect();
	}
		
	//------------------------------------------------------------------------------------------------------------
	// Remote Connection
	//------------------------------------------------------------------------------------------------------------
	
	/**
	 * The connect method establishes a connection to the SQL Database on a remote server through SSH
	 * using port forwarding, based on the current databaseAddress field.
	 * It overrides the SQLReadWriter class's connect method.
	 * 
	 * @author Pankaj@JournalDev (adapted from)
	 */
	
	@Override
	public void connect()
	{
		//Use Try/Catch to handle potential exception
		try{
			
			//Close Connection object if it already exists
			if(sqlConnection != null)
				sqlConnection.close();
			
			//Set StrictHostKeyChecking property to no to avoid UnknownHostKey issue
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			
			//Instantiate JSch and Session Objects
			JSch jsch = new JSch();
			Session session;
			
			//Prepare Session Object for Connection, then Connect
			session = jsch.getSession(username, remoteHost, 22);
			session.setPassword(password);
			session.setConfig(config);
			session.connect();
			System.out.println("Connected");
			
			//Setup Port Forwarding and Indicate Success
			int assignedPort = session.setPortForwardingL(localPort, remoteHost, remotePort);
			System.out.println("localhost: " + assignedPort + " -> " + remoteHost + ":"+ remotePort);
			System.out.println("Port Forwarded");

			//Create String containing connection URL
			String url = "jdbc:mysql://127.0.0.1:"+ localPort + databaseName;
			
			//Connect to the database, initializing inherited sqlConnection field
			Class.forName(DRIVERNAME).newInstance();
			this.sqlConnection = DriverManager.getConnection(url, USERNAME, PASSWORD);
			System.out.println ("Database connection established");
		}
		catch(Exception e) 
		{
			e.printStackTrace();
		}

	}

}
