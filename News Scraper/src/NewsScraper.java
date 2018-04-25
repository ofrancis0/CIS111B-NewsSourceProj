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
		
		//Instantiate NewsAPIConnector
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
			ArrayList<Article> inputArray = newsConn.getArticleArray();
			
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
			compareList = sqlWriter.readArticles("article", sqlConn);
			
			int compareIndex = 0;
			
			while(compareIndex < compareList.size())
			{
				//Get URLs of Matching Articles
				ArrayList<Integer> matchIDs = sqlWriter.getMatches(compareList.get(compareIndex), "article", sqlConn);
				
				ArrayList<Integer> matchIndices = new ArrayList<>();
				
				for(int id : matchIDs)
					for(int i = compareIndex; i < compareList.size(); i++)
						if(compareList.get(i).getID() == id)
							matchIndices.add(i);
				
				if(matchIndices.size() > 1)
				{
					compareList.get(compareIndex).generateTopic();
					sqlWriter.addArticleTopic(compareList.get(compareIndex), "article", sqlConn);
					
					Collections.sort(matchIndices);
					Collections.reverse(matchIndices);
					
					for(int i : matchIndices)
					{
						compareList.get(i).setTopic(compareList.get(compareIndex).getTopic());
						sqlWriter.addArticleTopic(compareList.get(i), "article", sqlConn);
						compareList.remove(i);
					}
				}
				else
					compareIndex++;
			}
			
			try
			{
			Thread.sleep(1000 * 60 * 60);
			}
			catch(InterruptedException e)
			{
				continue;
			}
		}
	}
}
