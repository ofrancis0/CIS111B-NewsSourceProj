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
	
	protected final String USERNAME = "XXXXX";
	protected final String PASSWORD = "XXXXX";
	protected final String DRIVERNAME="com.mysql.jdbc.Driver";
	protected String databaseName, databaseAddress;
	protected Connection sqlConnection;
	protected int connectionCounter;
	
	//------------------------------------------------------------------------------------------------------------
	// Constructor
	//------------------------------------------------------------------------------------------------------------
	
	/**
	 * The SQLReadWriter Constructor accepts a String argument for the SQL Database Name and Address, 
	 * and initializes the respective fields with those arguments. It also instantiates a Connection object
	 * with the connect method, and initializes the connectionCounter field to 0.
	 * 
	 * @param databaseName A String containing the SQL Database's name.
	 * @param databaseAddress A String containing the SQL Database's address.
	 */
	
	public SQLReadWriter(String databaseName, String databaseAddress)
	{
		this.databaseName = databaseName;
		this.databaseAddress = databaseAddress;
		this.connect();
		this.connectionCounter = 0;
	}
	
	//------------------------------------------------------------------------------------------------------------
	// Connection Method: Local
	//------------------------------------------------------------------------------------------------------------
	
	/**
	 * The connect method establishes a connection to a local SQL database based on the current databaseAddress 
	 * field. It closes any open connection and reinstantiates a SQL Connection object, assigning it to the 
	 * sqlConnection field.
	 */
	
	public void connect()
	{	
		//Initialize Datebase URL/Name String
		String databaseURL = databaseAddress + "/" + databaseName;

		//Connect to the database
		//Use Try/Catch to handle possible exception
		try
		{
			//Close Connection object if already open
			if(this.sqlConnection != null)
				this.sqlConnection.close();
			
			//Establish New Connection
			Class.forName(DRIVERNAME).newInstance();
			this.sqlConnection = DriverManager.getConnection(databaseURL, USERNAME, PASSWORD);
		} 
		//Return null if exception is thrown
		catch(Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	//------------------------------------------------------------------------------------------------------------
	// Read Methods
	//------------------------------------------------------------------------------------------------------------
	
	/**
	 * The readArticle method reads a line from the SQL Database table and returns an Article object with fields
	 * initialized to the data from the table.
	 * 
	 * @param tableName A String containing the name of the SQL Table
	 * 
	 * @return An ArrayList of Article Objects representing the articles from the table.
	 */
	
	public ArrayList<Article> readArticles(String tableName)
	{
		//Call Connect Method if Connection has been used too many times
		if(connectionCounter > 50)
		{
			this.connect();
			connectionCounter = 0;
		}
		
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
			
			//Close PreparedStatement and ResultSet
			sqlStatement.close();
			sqlResults.close();
			
		//Return Empty ArrayList if Exception is thrown
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return new ArrayList<>();
		}

		//Iterate connectionCounter to handle Memory Usage
		connectionCounter++;
		
		//Return ArrayList of Articles if no exception is thrown
		return articleArrayList;
	}
	
	/**
	 * The getMatches method accepts an Article Object argument and runs an SQL query for matches on its title
	 * and description, returning an ArrayList of article IDs that scored appropriate matches from the SQL query.
	 * 
	 * @param article An Article Object to compare against the Database for matches
	 * @param tableName The name of the SQL Table to Query
	 * 
	 * @return An ArrayList of Article IDs that were matched with the Article parameter.
	 */
	
	public ArrayList<Integer> getMatches(Article article, String tableName)
	{
		//Call Connect Method if Connection has been used too many times
		if(connectionCounter > 50)
		{
			this.connect();
			connectionCounter = 0;
		}
		
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
				
				//If matchScore >= 40, Add ID to ArrayList
				if(matchScore >= 30.0)
					returnArray.add(sqlResults.getInt("id"));
				//Otherwise, end iterations, because Score is returned in descending order
				else
					break;
			}
			
			//Close PreparedStatement and ResultSet
			sqlStatement.close();
			sqlResults.close();
		}
		//If exception is thrown, return empty ArrayList
		catch(Exception e)
		{
			e.printStackTrace();
			return new ArrayList<Integer>();
		}
		
		//Increment connectionCounter
		connectionCounter++;
		
		//If no exception is thrown, return returnArray
		return returnArray;
	}
	
	//------------------------------------------------------------------------------------------------------------
	// Write Methods
	//------------------------------------------------------------------------------------------------------------
	
	/**
	 * The writeArticle method accepts an Article Object argument and writes its data to the database,
	 * accepting a String argument for the SQL table name.
	 * 
	 * @param article An Article Object to write to the database
	 * @param tableName A String containing the name of the SQL table to write to.
	 */
	
	public void writeArticle(Article article, String tableName) throws Exception
	{
		//Call Connect Method if Connection has been used too many times
		if(connectionCounter > 50)
		{
			this.connect();
			connectionCounter = 0;
		}
		
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
		sqlStatement.setTimestamp(3, article.getTimeStamp());
		sqlStatement.setString(4, article.getDescription());
		sqlStatement.setString(5, article.getURL());

		//Execute and close the Prepared Statement
		sqlStatement.execute();
		sqlStatement.close();
		
		//Increment connectionCounter
		connectionCounter++;
	}
	
	/**
	 * The addArticleTopic method allows you to amend the topic field of an existing SQL Table 
	 * Row (representing an Article)
	 * 
	 * @param article An Article object to read the topic field from.
	 * @param tableName Name of the table in the database
	 */
	public void addArticleTopic (Article article, String tableName)
	{
		//Call Connect Method if Connection has been used too many times
		if(connectionCounter > 50)
		{
			this.connect();
			connectionCounter = 0;
		}
		
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
			
			//Increment connectionCounter
			connectionCounter++;
		}
		//Catch a potential exception and PrintStackTrace if caught
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}