import java.io.*;
import java.net.*;
import java.util.*;
import java.text.SimpleDateFormat;

import com.google.gson.*;

/**
 * <h1>News API Connector</h1>
 * The NewsAPIConnector class contains constructors and methods for connecting to NewsAPI
 * and returning arrays of JSON Elements that represent individual news articles. 
 *
 *@author Aaron Wile, Chrissa LaPorte, Oriel Francis
 *@since 13 April 2018
 */

public class NewsAPIConnector
{
	//------------------------------------------------------------------------------------------------------------
	// Instance Variables
	//------------------------------------------------------------------------------------------------------------

	private final String API_KEY = "19f78480cff94fc3bcebcdc57d3c5c70";
	private final String URL_BASE = "http://newsapi.org/v2/everything?language=en&sources=";
	
	private final String[] SOURCES = {"abc-news", "associated-press", "bbc-news", "bloomberg", "breitbart-news", 
									  "business-insider", "cnbc", "cnn", "fortune", "fox-news", 
									  "msnbc", "newsweek", "politico", "reuters", "the-economist", 
									  "the-huffington-post", "the-new-york-times", "the-wall-street-journal", 
									  "the-washington-post", "usa-today"};
	
	private String url, currentTimeStamp, previousTimeStamp;
	private int pageNumber;
	
	//------------------------------------------------------------------------------------------------------------
	// Constructor
	//------------------------------------------------------------------------------------------------------------
	
	/**
	 * The default NewsAPIConnector constructor creates a NewsAPIConnector object with currentTimeStamp
	 * set relative to the time at which the object is instantiated. Additionally, previousTimeStamp is set to
	 * to midnight of the current day, and pageNumber is set to 1. The constructor then combines these fields
	 * into a URL for connecting to newsAPI. This is accomplished through the class's updateTime and updateURL
	 * methods.
	 */
	
	public NewsAPIConnector()
	{
		//Initialize pageNumber to 1
		pageNumber = 1;
		
		//Use updateTime method to generate current and previous Time Stamps
		this.updateTimeStamp();
		
		//Use updateURL method to generate complete URL with current fields
		this.updateURL();
		
	}
	
	//------------------------------------------------------------------------------------------------------------
	// Field Update Methods
	//------------------------------------------------------------------------------------------------------------
	
	/**
	 * The updateTimeStamp method resets the currentTimeStamp String to the current Time and Date,
	 * formatted in ISO 8601 for NewsAPI, and replaces the previousTimeStamp String with
	 * the previous currentTimeStamp String. If the currentTimeStamp string is null (as in the
	 * case of an initial instantiation), previousTimeStamp is set to midnight of the current
	 * day.
	 */
	
	public void updateTimeStamp()
	{
		//Create a SimpleDateFormat object with formatting settings for ISO 8601
		//Format: "2018-04-13T06:23:00" 
		//Set the TimeZone to UTC with TimeZone class
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
		TimeZone tz = TimeZone.getTimeZone("UTC");
		dateFormat.setTimeZone(tz);
		
		//Set previousTimeStamp to currentTimeStamp if the latter is not null.
		if(currentTimeStamp != null)
			previousTimeStamp = currentTimeStamp;
		
		//Create a Date Object with the default constructor, which represents the current time and date.
		Date dateObj = new Date();
				
		//Parse a NewsAPI relevant currentTimeStamp String from the Date Object using SimpleDateFormat. 
		currentTimeStamp = dateFormat.format(dateObj).toString(); 
				
		//If previousTimeStamp is still null, set to midnight of the current day
		if(previousTimeStamp == null)
		{
			//Set Date Object to midnight of current day.
			dateObj.setHours(0);
			dateObj.setMinutes(0);
			dateObj.setSeconds(0);
			
			//Parse a NewsAPI relevant previousTimeStamp String from the Date Object using SimpleDateFormat
			previousTimeStamp = dateFormat.format(dateObj).toString();
		}
	}
	
	/**
	 * The updateURL method generates a new URL to request newsAPI's JSON output, based on the current
	 * state of the NewsAPIConnector's fields.
	 */
	
	public void updateURL()
	{
		//Set url String to base of newsAPI request url
		url = URL_BASE;
		
		//Add sources list from array of sources
		for(int source = 0; source < SOURCES.length; source++)
		{
			url += SOURCES[source];
			
			//Add comma if not last entry
			if(source < SOURCES.length - 1)
				url += ",";
		}
		
		//Add max page size for request
		url += "&pageSize=100";
		
		//Add current PageNumber of Json Output
		url += "&page=" + pageNumber;
		
		//Add From and To as previousTimeStamp and currentTimeStamp, respectively
		url += "&from=" + previousTimeStamp;
		url += "&to=" + currentTimeStamp;
		
		//Add API_KEY for authentication
		url += "&apiKey=" + API_KEY;
	}
	
	//------------------------------------------------------------------------------------------------------------
	// Connection and Input Reader Methods
	//------------------------------------------------------------------------------------------------------------
	
	/**
	 * The connect method establishes a connection to newsAPI based on the current url field
	 * and returns an InputStreamReader to read newsAPI's Json output.
	 * 
	 * @return An InputStreamReader to read from the current url field.
	 */
	
	public InputStreamReader connect()
	{	
		//Use Try/Catch to Handle Possible IOException
		try
		{
			//Open Connection
			URL newsURL  = new URL(url);
			URLConnection newsConnection = newsURL.openConnection();
						
			//Initialize InputStreamReader
			InputStreamReader newsReader = new InputStreamReader(newsConnection.getInputStream());
			
			return newsReader;
		}
		catch(IOException e)
		{
			//Return null if Exception is thrown
			return null;
		}
	}
	
	/**
	 * The getArticleArray method returns a JsonArray of Articles based on the NewsAPIConnection
	 * object's current url field.
	 *
	 *@return A JsonArray populated by articles from newsAPI based on the current url field.
	 */
	
	public Article[] getArticleArray()
	{		
		//Create InputStreamReader from current url with connect method
		InputStreamReader newsReader = this.connect();
		
		//Use Gson classes to read JSON from source
		//Use connect Method to receive InputStreamReader from current url field
		JsonParser newsParser = new JsonParser();
		JsonObject jsonObject = newsParser.parse(newsReader).getAsJsonObject();
		
		//Close InputStreamReader
		try
		{
		newsReader.close();
		}
		catch(IOException e){}
		
		//Parse "Total Results" to determine how many page "flips" are necessary for this query
		//PageFlips = Number of Pages at 100 Results Each. Round Up for Integer Division
		int totalResults = jsonObject.get("totalResults").getAsInt();
		int pageFlips;
		if(totalResults % 100 == 0)
			pageFlips = totalResults / 100;
		else
			pageFlips = totalResults / 100 + 1;
		
		//Create JsonArray of Articles from first Page
		JsonArray jsonArray = jsonObject.get("articles").getAsJsonArray();
		

		//Add each Page of JSON output from NewsAPI to jsonArray
		for(pageNumber = 2; pageNumber <= pageFlips; pageNumber++)
		{
			//Get JsonObject of current page
			//Use udpateURL method to update pageNumber in url, and connect to renew connection
			this.updateURL();
			InputStreamReader loopReader = this.connect();
			JsonObject loopObject = newsParser.parse(loopReader).getAsJsonObject();
			
			//Create new JsonArray from new JsonObject's Articles
			JsonArray loopArray = loopObject.get("articles").getAsJsonArray();
			
			//Add all JsonElements from current iteration to original JsonArray
			jsonArray.addAll(loopArray);
			
			//Close this iteration's InputStreamReader
			try
			{
			loopReader.close();
			}
			catch(IOException e){}
		}

		//Reset pageNumber to 1 for next method call.
		pageNumber = 1;
		
		//Instantiate Article Array to hold each Article
		Article[] returnArray = new Article[totalResults];
		
		//Use Article class's JsonElement arg constructor to instantiate Article objects
		//Populate returnArray with said Article objects
		for(int index = 0; index < totalResults; index++)
			returnArray[index] = new Article(jsonArray.get(index));
		
		//Return resulting array of Article Objects
		return returnArray;
	}
		
}
		
		
