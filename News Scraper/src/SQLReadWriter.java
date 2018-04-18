import java.sql.*;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.util.*;

/**
 * <h1>SQL Read/Writer</h1>
 * The SQLReadWriter Class contains methods for establishing a connection to an SQL Database,
 * reading data from the table, and writing data to it. It is designed to be used in conjunction
 * with the Article class, and thus has methods that work with Article Objects to write their data
 * to the relevant tables.
 * 
 *  @author Aaron Wile, Chrissa LaPorte, Oriel Francis
 *  @since 16 April 2018
 */

public class SQLReadWriter
{
	//------------------------------------------------------------------------------------------------------------
	// Instance Variables
	//------------------------------------------------------------------------------------------------------------
	
	private final String USERNAME = "Insert Username Here";
	private final String PASSWORD = "Insert Password Here";
	private final String DRIVERNAME="com.mysql.jdbc.Driver";
	private String databaseName, databaseAddress;
	
	//------------------------------------------------------------------------------------------------------------
	// Constructor
	//------------------------------------------------------------------------------------------------------------
	
	/**
	 * The SQLReadWriter Constructor accepts a String argument for the SQL Database Name and Address, 
	 * and initializes the respective fields with those arguments.
	 * 
	 * @param databaseName A String containing the SQL Database's name.
	 * @param databaseAddress A String containing the SQL Database's address.
	 */
	
	public SQLReadWriter(String databaseName, String databaseAddress)
	{
		this.databaseName = databaseName;
		this.databaseAddress = databaseAddress;
	}
	
	//------------------------------------------------------------------------------------------------------------
	// Connection Method: Local
	//------------------------------------------------------------------------------------------------------------
	
	/**
	 * The connect method establishes a connection to the SQL Database (locally) based on the current databaseAddress field
	 * and returns a Connection object associated with the java.sql.Connection class.
	 * 
	 * @return A Connection object from java.sql, for use with Read and Write methods.
	 */
	
	public Connection connect()
	{
		Connection conn = null;
		String DB_URL = databaseAddress + "/" + databaseName;

		// try connecting to the mySQL database
		try {
			Class.forName(DRIVERNAME).newInstance();
			conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return conn;
	}


	//------------------------------------------------------------------------------------------------------------
	// Connection Method: Remote
	//------------------------------------------------------------------------------------------------------------

	/**
	 * The connect method establishes a connection to the SQL Database on a remote server through SSH
	 * using port forwarding.
	 * The connection is made based on the current databaseAddress field.
	 * It returns a Connection object associated with the java.sql.Connection class.
	 *
	 * @return A Connection object from java.sql, for use with Read and Write methods.
	 * @author Pankaj@JournalDev (adapted from)
	 */

	public Connection remoteConnect()
	{

		int lport=1;
		String rhost="127.0.0.1";
		String host="digitalocean";
		int rport=1;
		String serverUser="XXXXX";
		String serverPassword="XXXXX";
		String url = "jdbc:mysql://127.0.0.1:"+lport+databaseName;

		Connection connRemote = null;
		Session session = null;

		try{
			//Set StrictHostKeyChecking property to no to avoid UnknownHostKey issue
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			JSch jsch = new JSch();
			session=jsch.getSession(serverUser, host, 22);
			session.setPassword(serverPassword);
			session.setConfig(config);
			session.connect();
			System.out.println("Connected");
			int assigned_port=session.setPortForwardingL(lport, rhost, rport);
			System.out.println("localhost:"+assigned_port+" -> "+rhost+":"+rport);
			System.out.println("Port Forwarded");

			//database connectivity
			Class.forName(DRIVERNAME).newInstance();
			connRemote = DriverManager.getConnection (url, USERNAME, PASSWORD);
			System.out.println ("Database connection established");
			System.out.println("DONE");
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		return connRemote;

		// for remote server connection will need to 1) close the connection & 2) disconnect from the session
	}

	
	//------------------------------------------------------------------------------------------------------------
	// Read/Write Methods
	//------------------------------------------------------------------------------------------------------------
	
	/**
	 * The readArticle method reads a line from the SQL Database table and returns an Article object with fields
	 * initialized to the data from the table.
	 * 
	 * @return A List of Article Objects representing the articles from the table.
	 */
	
	public ArrayList<Article> readArticle(String tableName, Connection conn)
	{

		// create a list of Article objects to hold each of the articles from the database
		ArrayList <Article> articlesInDB = new ArrayList<>();

		// connect to Database
		try {
			// create Statement Object
			Statement stmt = conn.createStatement();
			// Create a ResultSet Object (to hold the results from the query) and execute the query
			// in this instance the query retrieves all of the columns (SELECT *) and rows from the table
			ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName);

			// loop through the contents of the result set
			// this means that you go through each of the rows individually in the database
			while (rs.next())
			{
				// I acted as if there were a no-arg constructor in the Article class
				// and used the individual set methods for each of the data fields
				Article row = new Article();
				row.setTitle(rs.getString("title"));
				row.setSource( rs.getString("sources"));
				row.setDate( rs.getString("pubdate"));
				row.setURL( rs.getString("url"));
				// add the new Article object (row) to the List of articles
				articlesInDB.add(row);
			}


		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return articlesInDB;
	}
	
	/**
	 * The writeArticle method accepts an Article Object argument and writes its data to the database.
	 * It also accepts as a String the name of the table into which the data is to be inserted.
	 */
	
	public void writeArticle(Article article, String tableName, Connection conn)
	{
		try
		{

			// create a PreparedStatement object to send "insert row" statement to MySQL
			PreparedStatement insertRow = null;
			// define the string for the insert row query
			// use ? to indicate the parameters to be inserted (total of 4 parameters)
			// I plan to create a unique index on url so that it must be unique
			// "INSERT IGNORE" allows SQL to keep inserting rows (and just ignores duplicates)
			String sqlStatement = "INSERT IGNORE INTO" + " " + tableName +
					"(title, sources, pubdate, url) " +
					"Values"
					+ "(?,?,?,?)";

			// prepare the statement
			insertRow = conn.prepareStatement(sqlStatement);

			//Code to Convert Article's Fields to acceptable SQL Format
			//Code to Get an Article's Fields
			// set each of the parameters for the sqlStatement
			insertRow.setString(1, article.getTitle());
			insertRow.setString(2, article.getSource());
			insertRow.setString(3, article.getDate());
			insertRow.setString(4,article.getURL());

			// Execute the Prepared Statement
			insertRow.execute();

			// close the Prepared Statement
			insertRow.close();

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

	}
	
	/**
	 * The writeArticleWithTopic method accepts an Article Object argument and writes its data to the
	 * database. It differs from the writeArticle method in that it can also write a Topic field to
	 * the database (for the Matched Articles table).
	 *
	 */
	
	public void writeArticleWithTopic(Article article, String tableName, Connection conn)
	{
		try {
			// create a PreparedStatement object to send "insert row" statement to MySQL
			PreparedStatement insertRow = null;
			// define the string for the insert row query
			// use ? to indicate the parameters to be inserted (total of 5 parameters)
			String sqlStatement = "INSERT IGNORE INTO" + " " + tableName +
					"(title, sources, pubdate, url, topic) " +
						"Values"
						+ "(?,?,?,?,?)";

				// prepare the statement
				insertRow = conn.prepareStatement(sqlStatement);


				//Code to Convert Article's Fields to acceptable SQL Format
				//Code to Get an Article's Fields
				// set each of the parameters for the sqlStatement
				insertRow.setString(1, article.getTitle());
				insertRow.setString(2, article.getSource());
				insertRow.setString(3, article.getDate());
				insertRow.setString(4,article.getURL());
				insertRow.setString(5, article.getTopic());

				// Execute the Prepared Statement
				insertRow.execute();

				// close the Prepared Statement
				insertRow.close();

			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}

		}

	/**
	 * addArticleTopic method allows you to update a row in the table
	 * @param article Article object
	 * @param tableName Name of the table in the database
	 */
	public void addArticleTopic (Article article, String tableName, Connection conn)
	{
		try {
			
			// create a PreparedStatement object to send "insert row" statement to MySQL
			PreparedStatement updateRow = null;
			// define the string for the insert row query
			// use ? to indicate the parameters


			String sqlStatement = "UPDATE " + tableName +
					" SET topic = ? WHERE url = ? ";


			// prepare the statement
			updateRow = conn.prepareStatement(sqlStatement);


			//Code to Convert Article's Fields to acceptable SQL Format
			//Code to Get an Article's Fields
			// set each of the parameters for the sqlStatement
			updateRow.setString(1, article.getTopic());
			updateRow.setString(2, article.getURL());

			// Execute the Prepared Statement
			updateRow.execute();

			// close the Prepared Statement
			updateRow.close();

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

	}



}