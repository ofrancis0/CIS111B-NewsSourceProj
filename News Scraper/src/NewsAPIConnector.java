import com.google.gson.*;

import java.io.InputStreamReader;
import java.net.*;
import java.util.*;
import java.text.SimpleDateFormat;

/**
 * <h1>News API Connector</h1>
 * The NewsAPIConnector class contains constructors and methods for connecting to NewsAPI
 * and returning arrays of JSON Elements that represent individual news articles. 
 *
 *@author Aaron Wile
 *@since 13 April 2018
 */

public class NewsAPIConnector
{
	//------------------------------------------------------------------------------------------------------------
	// Instance Variables
	//------------------------------------------------------------------------------------------------------------
	
	private final String API_KEY = "19f78480cff94fc3bcebcdc57d3c5c70";
	private final String URL_BASE = "http://newsapi.org/v2/everything?sources=";
	
	private final String[] SOURCES = {"abc-news", "associated-press", "bbc-news",
							  "breitbart-news", "business-insider", "cbs-news",
							  "cnbc", "cnn", "fox-news", "msnbc", "nbc-news",
							  "newsweek", "politico", "the-economist",
							  "the-huffington-post", "the-new-york-times",
							  "the-wall-street-journal", "the-washington-post", "time" };
	
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
	// Accessor and Mutator Methods
	//------------------------------------------------------------------------------------------------------------
	
	/**
	 * The getPageNumber method returns an int containing the current value of the pageNumber field, which is
	 * intended to track what page of JSON output the class is currently calling.
	 *
	 *@return An int containing the value of the current pageNumber field.
	 */
	
	public int getPageNumber()
	{
		return pageNumber;
	}
	
	/**
	 * The setPageNumber method accepts an int argument, and replaces the current value of the pageNumber field,
	 * which is intended to track what page of JSON output the class is currently calling.
	 *
	 *@param pn The new page number for JSON output.
	 */
	
	public void setPageNumber(int pn)
	{
		pageNumber = pn;
	}
	
	//------------------------------------------------------------------------------------------------------------
	// Helper Methods
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
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
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
	
	/**
	 * The getArticleArray method returns a JsonArray of Articles based on the NewsAPIConnection
	 * object's current url field.
	 *
	 *@return A JsonArray populated by articles from newsAPI based on the current url field.
	 */
	
	public JsonArray getArticleArray() throws Exception
	{
		//This method needs to be updated to assess and handle connection status
		//Also to "flip" JSON pages, which will replace getPageNumber and setPageNumber
		//Should also return array of article objects, not jsonArray
		
		//Open Connection
		URL newsURL  = new URL(url);
		URLConnection newsConnection = newsURL.openConnection();
				
		//Initialize InputStreamReader
		InputStreamReader newsReader = new InputStreamReader(newsConnection.getInputStream());
				
		//Use Gson classes to read json from source
		JsonParser newsParser = new JsonParser();
		JsonObject jsObject = newsParser.parse(newsReader).getAsJsonObject();
				
		//Create Array using Gson of Articles
		JsonArray returnArray = jsObject.get("articles").getAsJsonArray();
		
		//Return JsonArray
		return returnArray;
	}
		
}
		
		
