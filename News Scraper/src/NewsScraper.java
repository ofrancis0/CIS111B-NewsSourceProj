import java.util.*;

/**
 * <h1>News Scraper</h1>
 * The News Scraper class is responsible for collecting raw news data from newsAPI
 * and writing it to the Raw News SQL Database. It then makes SQL Match queries, using
 * the SQLReadWriter class, to match articles and generate a topic line for them. It is
 * designed to be used with Ubuntu's Chron, which allows it to run once per hour.
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
		
		//Instantiate SQLReadWriter
		SQLReadWriter sqlWriter = new SQLReadWriter("news_test", "jdbc:mysql://localhost:3306");
		
		//----------------------------------------------------------------------------------------------------
		// NewsAPI to DB
		//----------------------------------------------------------------------------------------------------
		
		System.out.println("Getting Articles from NewsAPI...");
		
		//Get Array of Articles from NewsAPI
		ArrayList<Article> articleArray = newsConn.getArticleArray();
		
		System.out.println("Adding " + articleArray.size() + " Articles to Database...");
		
		//Iterate over Article Array, using SQLReadWriter class to write each Article to the article table
		for(Article article : articleArray)
		{
			try
			{
				sqlWriter.writeArticle(article, "article");
			}
			catch(Exception e)
			{
				e.printStackTrace();
				continue;
			}
		}
		
		//----------------------------------------------------------------------------------------------------
		// Article Comparisons
		//----------------------------------------------------------------------------------------------------
		
		System.out.println("Getting All Articles from Database...");
		
		//Reset articleArray to ArrayList of Articles from SQLReadWriter readArticles Method
		articleArray = sqlWriter.readArticles("article");
		
		//Instantiate an Iterator for Comparisons
		int compareIndex = 0;
		
		System.out.println("Comparing " + articleArray.size() + " Articles...");
		
		//While the Iterator is less than the Number of Remaining Articles
		while(compareIndex < articleArray.size())
		{
			//Get SQL IDs of Matching Articles using getMatches method
			ArrayList<Integer> matchIDs = sqlWriter.getMatches(articleArray.get(compareIndex), "article");
			
			//Declare an ArrayList to hold ArrayList indices of matched Article Objectss
			ArrayList<Integer> matchIndices = new ArrayList<>();
			
			//Iterate over ArrayList of IDs and Populate ArrayList of indices with matched IDs
			for(int id : matchIDs)
				for(int i = compareIndex; i < articleArray.size(); i++)
					if(articleArray.get(i).getID() == id)
						matchIndices.add(i);
			
			//If there are 2 or More Matches...
			if(matchIndices.size() > 2)
			{
				//Generate a Topic with the generateTopic method, then write that Topic to the DB
				articleArray.get(compareIndex).generateTopic();
				sqlWriter.addArticleTopic(articleArray.get(compareIndex), "article");
				
				//Sort the MatchIndices ArrayList in descending order
				Collections.sort(matchIndices);
				Collections.reverse(matchIndices);
				
				//Iterate over MatchIndices ArrayList
				for(int i : matchIndices)
				{
					//For each Matched Article, Set the Topic to the Generated Topic
					//Write that topic to the Appropriate Article (by ID) in the Database
					articleArray.get(i).setTopic(articleArray.get(compareIndex).getTopic());
					sqlWriter.addArticleTopic(articleArray.get(i), "article");
					
					//Remove that Article from the ArrayList of articles to speed up later iterations
					articleArray.remove(i);
				}
			}
			//If not enough matches are found, iterate CompareIndex (to ignore this Article in later comparisons)
			else
				compareIndex++;
			
		}//End of While Loop
		
		System.out.println("Finished.");
	}
}
