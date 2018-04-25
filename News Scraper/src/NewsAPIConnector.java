import java.io.*;
import java.net.*;
import javax.net.ssl.HttpsURLConnection;
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

	private final String URL_BASE = "http://newsapi.org/v2/everything?language=en&sources=";
	
	private final String[] API_KEYS = {"19f78480cff94fc3bcebcdc57d3c5c70", "4e131f24ad254086a57351641b8ba21d",
									   "a513991c58ac4bc58031b3bc981c4d5b", "6c324fcf238649d7a2d4019cafb64b7e",
									   "21d4cdd3f4f24bb3a3db7c34664daa72"};
	
	private final String[] SOURCES = {"abc-news", "associated-press", "bbc-news", "bloomberg", "breitbart-news", 
									  "business-insider", "cnbc", "cnn", "fortune", "fox-news", 
									  "msnbc", "newsweek", "politico", "reuters", "the-economist", 
									  "the-huffington-post", "the-new-york-times", "the-wall-street-journal", 
									  "the-washington-post", "usa-today"};
	
	private String url, currentTimeStamp, previousTimeStamp;
	private int pageNumber, apiIndex;
	
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
		
		//Initialize apiIndex to 0
		apiIndex = 0;
		
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
		SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-DD'T'HH:MM:SS");
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
		
		//Add API_KEY for authentication, using current apiIndex to select from available keys
		url += "&apiKey=" + API_KEYS[apiIndex];
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
		//Use Try/Catch to Handle Possible IOException or HTTP Error
		try
		{
			//Open Connection
			URL newsURL  = new URL(url);
			HttpURLConnection newsConnection = (HttpURLConnection)newsURL.openConnection();
			
			//Set Request Method to Get
			newsConnection.setRequestMethod("GET");
			
			//Get Http Response Code
			int responseCode = newsConnection.getResponseCode();
			
			//Handle Return Based On Response Code
			
			//If Connection Worked Correctly
			if(responseCode == 200) 
			{		
				//Initialize InputStreamReader
				InputStreamReader newsReader = new InputStreamReader(newsConnection.getInputStream());
			
				return newsReader;
			}
			//If Too Many Requests Have Been Made with this API key
			else 
			{
				System.out.println(responseCode);
				if(apiIndex < 4) //If All keys have not been exhuasted
				{
					apiIndex++; //Move to next key
					this.updateURL(); //Update URL with new key
					return this.connect(); //Recursively call connect method with updated key
				}
				else //If all keys exhausted
				{
					apiIndex = 0; //Reset apiIndex to 0;
					return null; //Return null
				}
			}
		}
		catch(IOException e)
		{
			//Return null if IOException is thrown
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * The getArticleArray method returns an ArrayList of Articles based on the NewsAPIConnection
	 * object's current url field.
	 *
	 *@return An ArrayList populated by Article objects based on the data from newsAPI relative
	 *to the the current url field.
	 */
	
	public ArrayList<Article> getArticleArray()
	{		
		//Declare Array to Hold Article objects
		ArrayList<Article> returnArray = null;
		
		//Use Try/Catch to Handle IOException or NullPointerException from connect method
		try
		{
			//Create InputStreamReader from current url with connect method
			InputStreamReader newsReader = this.connect();
		
			//Use Gson classes to read JSON from source
			//Use connect Method to receive InputStreamReader from current url field
			JsonParser newsParser = new JsonParser();
			JsonObject jsonObject = newsParser.parse(newsReader).getAsJsonObject();
		
			//Close InputStreamReader
			newsReader.close();
		
			//Parse "Total Results" to determine how many page "flips" are necessary for this query
			//PageFlips = Number of Pages at 100 Results Each. Round Up for Integer Division
			int totalResults = jsonObject.get("totalResults").getAsInt();
			int pageFlips;
			if(totalResults % 100 == 0)
				pageFlips = totalResults / 100;
			else
				pageFlips = totalResults / 100 + 1;
			
			if(pageFlips > 100)
				pageFlips = 100;
		
			//Create JsonArray of Articles from first Page
			JsonArray jsonArray = jsonObject.get("articles").getAsJsonArray();
		

			//Add each Page of JSON output from NewsAPI to jsonArray
			for(pageNumber = 2; pageNumber < pageFlips; pageNumber++)
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
				loopReader.close();

			}

			//Reset pageNumber to 1 for next method call.
			pageNumber = 1;
		
			//Instantiate Article Array to hold each Article
			returnArray = new ArrayList<Article>();
		
			//Use Article class's JsonElement arg constructor to instantiate Article objects
			//Populate returnArray with said Article objects
			for(int index = 0; index < jsonArray.size(); index++)
				returnArray.add(new Article(jsonArray.get(index)));
		
		}
		//Catch NullPointerException if Connect Method returns Null, return empty Array
		catch(NullPointerException e)
		{
			e.printStackTrace();
			pageNumber = 1;
			return new ArrayList<Article>();
		}
		//If IOException is Caught from failing to Close Connections, printStackTrace
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		//If no Exception is thrown, return Array of Article Objects
		return returnArray;
	}
}
		
		
