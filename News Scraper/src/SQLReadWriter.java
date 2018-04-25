import java.sql.*;
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
	
	private final String USERNAME = "username";
	private final String PASSWORD = "password";
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
	 * The connect method establishes a connection to a local SQL database based on the current databaseAddress field
	 * and returns a Connection object associated with the java.sql.Connection class.
	 * 
	 * @return A Connection object from java.sql, for use with Read and Write methods.
	 */
	
	public Connection connect()
	{
		//Initialize SQL Connection object and Datebase URL/Name String
		Connection sqlConnection;
		String databaseURL = databaseAddress + "/" + databaseName;

		//Connect to the database
		//Use Try/Catch to handle possible exception
		try
		{
			Class.forName(DRIVERNAME).newInstance();
			sqlConnection = DriverManager.getConnection(databaseURL, USERNAME, PASSWORD);
		} 
		//Return null if exception is thrown
		catch(Exception e) 
		{
			e.printStackTrace();
			return null;
		}
		
		//Return Connection Object if no exception is thrown
		return sqlConnection;
	}
	
	//------------------------------------------------------------------------------------------------------------
	// Read Methods
	//------------------------------------------------------------------------------------------------------------
	
	/**
	 * The readArticle method reads a line from the SQL Database table and returns an Article object with fields
	 * initialized to the data from the table.
	 * 
	 * @param tableName A String containing the name of the SQL Table
	 * @param sqlConnection A Connection object associated with java.sql, containing the connection to read from
	 * 
	 * @return An ArrayList of Article Objects representing the articles from the table.
	 */
	
	public ArrayList<Article> readArticles(String tableName, Connection sqlConnection)
	{

		// create a list of Article objects to hold each of the articles from the database
		ArrayList<Article> articleArrayList = new ArrayList<>();

		//Create Statements using Connection object Argument
		//Use Try/Catch to Handle Potential Exceptions
		try
		{
			//Create Statement Object Associated with java.sql
			Statement sqlStatement = sqlConnection.createStatement();
			
			//Create a ResultSet Object (to hold the results from the query) and execute the query.
			//In this instance the query retrieves all of the columns and rows from the table
			ResultSet sqlResults = sqlStatement.executeQuery("SELECT * FROM " + tableName);

			//Iterate over the contents of the result set, reading each line from the table in order
			while(sqlResults.next())
			{
				//Use Default Article Constructor to instantiate Article Objects
				//Use Mutator Methods to Set Article Fields
				Article article = new Article();
				article.setID(sqlResults.getInt("id"));
				article.setTitle(sqlResults.getString("title"));
				article.setSource(sqlResults.getString("sources"));
				article.setDate(sqlResults.getString("pubdate"));
				article.setDescription(sqlResults.getString("description"));
				article.setURL(sqlResults.getString("url"));
				
				//Add the new article object to the ArrayList of articles
				articleArrayList.add(article);
			}
		//Return Empty ArrayList if Exception is thrown
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return new ArrayList<>();
		}

		//Return ArrayList of Articles if no exception is thrown
		return articleArrayList;
	}
	
	/**
	 * The getMatches method accepts an Article Object argument and runs an SQL query for matches on its title
	 * and description, returning an ArrayList of URLs that scored appropriate matches from the SQL query.
	 * 
	 * @param article An Article Object to compare against the Database for matches
	 * @param tableName The name of the SQL Table to Query
	 * @param sqlConnection An SQL Connection object representing the connection to the relevant database.
	 */
	
	public ArrayList<Integer> getMatches(Article article, String tableName, Connection sqlConnection)
	{
		//Declare an ArrayList to store URLs of Article's that match
		ArrayList<Integer> returnArray = new ArrayList<>();
		
		//Use Try/Catch to Handle Potential Exceptions
		try
		{
			//Create a String to Hold SQL Statement and Initialize Properly
			//This String calls for Matched Scores against a description ordered descending
			String queryString = "SELECT id, MATCH (title, description) AGAINST " +
								 "(? IN NATURAL LANGUAGE MODE) AS score FROM " + tableName +
								 " ORDER BY score DESC";
			
			//Declare and Initialize a PreparedStatement with the Article Object's Description
			PreparedStatement sqlStatement = sqlConnection.prepareStatement(queryString);
			sqlStatement.setString(1, article.getDescription());
			
			//Execute Query and Receive Result Set
			ResultSet sqlResults = sqlStatement.executeQuery();
			
			//Iterate Over the Result Set, Adding Article URL to ArrayList if Score is High Enough
			while(sqlResults.next())
			{
				//Get Match Score of Current Line
				double matchScore = sqlResults.getDouble("score");
				
				//If matchScore >= .5, Add URL to ArrayList
				if(matchScore >= 12.0)
					returnArray.add(sqlResults.getInt("id"));
				//Otherwise, end iterations, because Score is returned in descending order
				else
					break;
			}
		}
		//If exception is thrown, return empty ArrayList
		catch(Exception e)
		{
			e.printStackTrace();
			return new ArrayList<Integer>();
		}
		
		//If no exception is thrown, return returnArray
		return returnArray;
	}
	
	//------------------------------------------------------------------------------------------------------------
	// Write Methods
	//------------------------------------------------------------------------------------------------------------
	
	/**
	 * The writeArticle method accepts an Article Object argument and writes its data to the database,
	 * accepting a String argument for the SQL table name and Connection argument for the SQL connection.
	 * 
	 * @param article An Article Object to write to the database
	 * @param tableName A String containing the name of the SQL table to write to.
	 * @param sqlConnection A Connection object from java.sql representing the connection to the
	 * SQL database to write to.
	 */
	
	public void writeArticle(Article article, String tableName, Connection sqlConnection)
	{
		//Use Try/Catch for Exception Handling
		try
		{
			//Declare a PreparedStatement object to statements to MySQL
			PreparedStatement sqlStatement;
			
			//Define the string for the insert ignore query
			//Use ? to indicate the parameters to be inserted (total of 5 parameters)
			String statementString = "INSERT IGNORE INTO " + tableName +
								     "(title, sources, pubdate, description, url) " +
					                 "Values" + "(?,?,?,?,?)";

			//Prepare the Statement with prepareStatement method
			sqlStatement = sqlConnection.prepareStatement(statementString);

			//Set each of the parameters for the sqlStatement
			sqlStatement.setString(1, article.getTitle());
			sqlStatement.setString(2, article.getSource());
			sqlStatement.setString(3, article.getDate());
			sqlStatement.setString(4, article.getDescription());
			sqlStatement.setString(5, article.getURL());

			//Execute and close the Prepared Statement
			sqlStatement.execute();
			sqlStatement.close();
		}
		//Catch a potential exception and PrintStackTrace if caught
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * The addArticleTopic method allows you to amend the topic field of an existing SQL Table 
	 * Row (representing an Article)
	 * 
	 * @param article An Article object to read the topic field from.
	 * @param tableName Name of the table in the database
	 * @param sqlConnection The SQL Connection Object representing the connection to the relevant DB.
	 */
	public void addArticleTopic (Article article, String tableName, Connection sqlConnection)
	{
		//Use Try/Catch to Handle Possible Exception
		try
		{	
			//Declare a PreparedStatement object to send statement to MySQL
			PreparedStatement sqlStatement;
			
			//Define the string for the Update Query
			//Use ? to indicate the parameters
			String statementString = "UPDATE " + tableName + " SET topic = ? WHERE id = ? ";

			//Prepare the Statement with the prepareStatement method
			sqlStatement = sqlConnection.prepareStatement(statementString);

			//Set each of the parameters for the sqlStatement
			sqlStatement.setString(1, article.getTopic());
			sqlStatement.setInt(2, article.getID());

			//Execute and close the Prepared Statement
			sqlStatement.execute();
			sqlStatement.close();
		}
		//Catch a potential exception and PrintStackTrace if caught
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}



}