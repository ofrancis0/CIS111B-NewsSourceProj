import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import java.sql.Connection;

/**
 * <h1>SQL Read/Writer</h1>
 * The SQLReadWriter Class contains methods for establishing a connection to an SQL Database,
 * reading data from the table, and writing data to it. It is designed to be used in conjuction
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
	// Connection Method
	//------------------------------------------------------------------------------------------------------------
	
	/**
	 * The connect method establishes a connection to the SQL Database based on the current databaseAddress field
	 * and returns a Connection object associated with the java.sql.Connection class.
	 * 
	 * @return A Connection object from java.sql, for use with Read and Write methods.
	 */
	
	public Connection connect()
	{
		Connection conn = null;
		
		//Code to Connect to SQL
		
		return conn;
	}
	
	//------------------------------------------------------------------------------------------------------------
	// Read/Write Methods
	//------------------------------------------------------------------------------------------------------------
	
	/**
	 * The readArticle method reads a line from the SQL Database table and returns an Article object with fields
	 * initialized to the data from the table.
	 * 
	 * @return An Article Object representing the article from the table.
	 */
	
	public Article readArticle()
	{
		//Call to connect method
		
		//Code to create SQL Statement Objects
		
		//Code that executes SQL Statements to receive data
		
		//Code to Create Article Object with relevant fields
		//Can we return an SQL Object that can be passed as a singular argument to an Article Constructor?
		Article returnArticle = new Article(SQL Data);
		
		//Close SQL Connection
		
		return returnArticle;
	}
	
	/**
	 * The writeArticle method accepts an Article Object argument and writes its data to the database.
	 */
	
	public void writeArticle(Article article)
	{
		//Call to connect method
		
		//Code to Get an Article's Fields
		
		//Code to Convert Article's Fields to acceptable SQL Format
		
		//Code to create SQL Statement Objects in Java
		
		//Code that executes SQL Statements
		
		//Code that closes SQL Connection
	}
	
	/**
	 * The writeArticleWithTopic method accepts an Article Object argument and writes its data to the
	 * database. It differs from the writeArticle method in that it can also write a Topic field to
	 * the database (for the Matched Articles table).
	 */
	
	public void writeArticleWithTopic(Article article)
	{
		//Call to connect method
		
		//Code to get an Article's Fields
		
		//Code to Convert Article's Fields to acceptable SQL Format
		
		//Code to create SQL Statement Objects
		
		//Code that executes SQL Statements
		
		//Code that closes SQL Connection
	}
	
}