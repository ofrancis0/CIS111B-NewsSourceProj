import java.io.*;
import java.net.*;
import java.util.*;
import com.google.gson.*;

public class ComparisonTest
{
	public static void main(String[] args) throws Exception
	{	
		//-----------------------------------------------------------------------------------------------------
		//Declarations and Setup
		//-----------------------------------------------------------------------------------------------------
		
		//URL for newsAPI Link
		String newsLink = 
		"https://newsapi.org/v2/everything?from=2018-03-05&to=2018-04-02&pagesize=100&sources=fox-news,cnn&apiKey=19f78480cff94fc3bcebcdc57d3c5c70";
		
		//Use the getArticleArray method to create a JsonArray of articles from the newsAPI link: articleArray
		JsonArray articleArray = getArticleArray(newsLink);
		
		//Instantiate Hashmap to store Keyword Values: matchedArticles
		HashMap<String, ArrayList<String>> matchedArticles = new HashMap<String, ArrayList<String>>(100);
		
		//-----------------------------------------------------------------------------------------------------
		//Iterate Comparisons for Each Article
		//-----------------------------------------------------------------------------------------------------
		
		for(int articleIndex = 0; articleIndex < articleArray.size() - 1; articleIndex++)
		{	
			//Generate Keyword Set of Article with getKeywords method
			Set<String> keywordSet = getKeywords(articleArray.get(articleIndex));
			
			//Cross-Reference keywordSet against Current matchedArticles Map with keyExists method
			String key = keyExists(keywordSet, matchedArticles);
			
			//-------------------------------------------------------------------------------------------------
			//If Key Already Exists
			//-------------------------------------------------------------------------------------------------
			
			if(!(key.equals("No Match Found")))
			{
				//Get Article URL
				String addURL = articleArray.get(articleIndex).getAsJsonObject().get("url").toString();
				
				//If HashMap ArrayList Doesn't Already Contain URL...
				if(!matchedArticles.get(key).contains(addURL))
					matchedArticles.get(key).add(addURL);//...Add URL to HashMap ArrayList
				
				//Skip to next article for comparison
				continue;
			}
			//-------------------------------------------------------------------------------------------------
			//If Key does not exist, Cross Reference against remaining articles
			//-------------------------------------------------------------------------------------------------
			else
			{
				for(int crossRefIndex = articleIndex + 1; crossRefIndex < articleArray.size(); crossRefIndex++)
				{
				
					//Get Keyword Set of Cross-Referenced Article
					Set<String> crossRefSet = getKeywords(articleArray.get(crossRefIndex));
			
					//Compare Sets for Match using keywordMatch method
					if(keywordMatch(keywordSet, crossRefSet)) //If 2 or more Keywords Match
					{
						//Generate New Key with All Keywords of Matching Articles
						keywordSet.addAll(crossRefSet);
						String newKey = String.join(" ", keywordSet);
					
						//Add New ArrayList associated with New Key to Hashmap
						matchedArticles.put(newKey, new ArrayList<String>());
					
						//Add both URLs to ArrayList in HashMap
						matchedArticles.get(newKey).add(articleArray.get(articleIndex).getAsJsonObject().get("url").toString());
						matchedArticles.get(newKey).add(articleArray.get(crossRefIndex).getAsJsonObject().get("url").toString());
						
						break; //Break Out of Cross-Reference For Loop if Match Found, To Avoid Duplicates on Later Iterations
					}
				}
			}
		}
		
		//----------------------------------------------------------------------------------------------------
		//End of Comparisons
		//----------------------------------------------------------------------------------------------------
		
		//Output Resulting Hashmap
		for(String i : matchedArticles.keySet())
		{
			System.out.println(i);
			for(String j : matchedArticles.get(i))
				System.out.println(j);
		}
	}
	
	//End of Main Method
	
	/**
	 * The getArticleArray method takes a URL to a NewsAPI Json file and returns a JsonArray, from the Gson library,
	 * in which each individual element represents a separate article.
	 *
	 *@param url A String containing the NewsAPI url
	 *@return A JsonArray populated by articles from newsAPI
	 */
	
	public static JsonArray getArticleArray(String url) throws Exception
	{
		//Open Connection
		URL newsURL  = new URL(url);
		URLConnection newsConnection = newsURL.openConnection();
				
		//Initialize InputStreamReader
		InputStreamReader newsReader = new InputStreamReader(newsConnection.getInputStream());
				
		//Use Gson classes to read json from source
		JsonParser newsParser = new JsonParser();
		JsonObject jsObject = newsParser.parse(newsReader).getAsJsonObject();
				
		//Output Status and Total Articles by using Gson get method, referring to Json keys
		System.out.println(jsObject.get("status"));
		System.out.println(jsObject.get("totalResults"));
				
		//Create Array using Gson of Articles
		JsonArray returnArray = jsObject.get("articles").getAsJsonArray();
		
		//Return JsonArray
		return returnArray;
	}
	
	/**
	 * The getKeywords method accepts a JsonElement argument generated from the newsAPI
	 * link and returns a String Set containing all words from the article's title that
	 * are 4 characters or longer, sorted alphabetically.
	 * 
	 * @param jsArticle A JsonElement representing an individual article from newsAPI
	 * @return An alphabetically sorted Set of Strings containing all >3 char words from the article's title
	 */
	
	public static Set<String> getKeywords(JsonElement jsArticle)
	{
		//Split Title of Article into String Array of Words
		String[] titleArray = jsArticle.getAsJsonObject().get("title").toString().split("\\W");
		
		//Sort array alphabetically
		Arrays.sort(titleArray);
		
		//Convert String Array into Set to remove duplicates
		Set<String> titleSet = new HashSet<String>(Arrays.asList(titleArray));
		
		//Remove All Words <4 letters
		Set<String> keywordSet = new HashSet<String>(); //A set to hold all >3 char words from title
		for(String i : titleSet)
			if(i.length() >= 4)
				keywordSet.add(i); //Add each word of appropriate length to keywordSet
		
		return keywordSet;
	}
	
	/**
	 * The keyExists method accepts a String Set argument and a HashMap argument, and is
	 * designed to compare the String Set of keywords against the HashMap of matched articles
	 * to determine if a key for the article's keywords already exists in the HashMap. If it does,
	 * it returns the matching key.
	 *  
	 *  @param keywordSet A String Set containing an article's keywords.
	 *  @param matchedArticles The existing HashMap of matched articles for cross-reference
	 *  @return A String containing the existing key that matches.
	 */
	
	public static String keyExists(Set<String> keywordSet, HashMap<String, ArrayList<String>> matchedArticles)
	{
		//Check each String in the HashMap's keyset
		for(String key : matchedArticles.keySet())
		{
			//Iterator for keyword matches
			int matches = 0;
			
			//Cross-Reference keywordSet against current HashMap Key
			for(String keyword : keywordSet)
			{
				if(key.contains(keyword))
					matches++;//Increment matches if match found
			}	
			
			//If >1 Match, return true
			if(matches > 1)
			{
				return key;
			}
		}
		
		//Return "No Match Found" if no matching key found
		return "No Match Found";
	}
	
	/**
	 * The keywordMatch method checks two String Sets of article title keywords to determine if two
	 * or more matches exist.
	 * 
	 * @param keywordSet1 The first set of title keywords
	 * @param keywordSet2 The second set of title keywords
	 * @return True if 2 or more keywords match, False if not
	 */
	
	public static boolean keywordMatch(Set<String> keywordSet1, Set<String> keywordSet2)
	{
		//Create Intersection of both sets
		keywordSet1.retainAll(keywordSet2);
		
		//If two or more keywords remain in intersection, return true
		if(keywordSet1.size() >= 2)
			return true;
		else
			return false;
	}
}