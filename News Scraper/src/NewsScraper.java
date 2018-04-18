import java.util.*;
import java.sql.*;

/**
 * <h1>News Scraper</h1>
 * The News Scraper class is responsible for collecting raw news data from newsAPI
 * and writing it to the Raw News SQL Database. It is one of two driver classes for
 * the program.
 */

public class NewsScraper
{
	public static void main(String[] args)
	{
		//--------------------------------------------------------------------------------------------------------
		// Initial Setup
		//--------------------------------------------------------------------------------------------------------
		
		//Instantiage NewsAPIConnector
		NewsAPIConnector newsConn = new NewsAPIConnector();
		
		//Instantiate SQLReadWriter and Connection
		SQLReadWriter sqlWriter = new SQLReadWriter("raw_news_table", "jdbc:mysql://localhost:3306");
		Connection sqlConn = sqlWriter.connect();
		
		//Declare Comparison ArrayList
		ArrayList<Article> compareList;
		
		//Infinite Loop Boolean
		boolean keepGoing = true;
		
		//Perform operations indefinitely
		while(keepGoing)
		{
			//----------------------------------------------------------------------------------------------------
			// NewsAPI to DB
			//----------------------------------------------------------------------------------------------------
			
			//Get Array of Articles from NewsAPI
			Article[] inputArray = newsConn.getArticleArray();
			
			//Iterate over Article Array, using SQLReadWriter class to write each Article to the article table
			for(Article article : inputArray)
				sqlWriter.writeArticle(article, "article", sqlConn);
			
			//Update NewsAPIConnector timeStamp for next iteration
			newsConn.updateTimeStamp();
			newsConn.updateURL();
			
			//----------------------------------------------------------------------------------------------------
			// Article Comparisons
			//----------------------------------------------------------------------------------------------------
			
			//Instantiate Comparison ArrayList using SQLReadWriter readArticle Method
			compareList = sqlWriter.readArticle("article", sqlConn);
			
			//Use nested for loop to run Comparisons
			for(int index = 0; index < compareList.size() - 1; index++)
			{
				for(int crossRefIndex = index + 1; crossRefIndex < compareList.size(); crossRefIndex++)
				{
					//Use Article Class compare method to determine if Articles match
					//Compare method automatically generates topic field to Article object in case of match
					boolean match = compareList.get(index).match(compareList.get(crossRefIndex));
					
					//If Articles Match, Add Topic Line to Both Articles on DB
					if(match)
					{
						sqlWriter.addArticleTopic(compareList.get(index), "article", sqlConn);
						sqlWriter.addArticleTopic(compareList.get(crossRefIndex), "article", sqlConn);
					}
				}
				System.out.println("Finished Comparison Pass");
			}
			
			System.out.println("Finished Iteration");
			
			//Pause for a set amount of time (5 Minutes) before repeating
			//Thread.sleep accepts a long argument in milliseconds
			//1000 milliseconds * 60 seconds * 5 minutes
			try
			{
				Thread.sleep((long)(1000 * 60 * 5));
			}
			catch(InterruptedException e)
			{
			}
		}
	}
}
