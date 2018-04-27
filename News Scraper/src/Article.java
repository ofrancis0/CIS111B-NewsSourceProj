import java.util.*;
import java.time.format.*;
import java.time.temporal.*;
import java.time.Instant;

import paralleldots.ParallelDots;
import com.google.gson.*;

/**
 * <h1>The Article Class</h1>
 * The Article class is designed to hold information about individual news Articles.
 * It contains constructors and methods to store, retrieve, and compare data about these
 * articles, which are aggregated from a variety of popular sources from newsAPI. The overloaded
 * constructors allow creation of Article objects directly from JsonElements (from newsAPI),
 * or from SQL data (our database).
 * 
 * @author Aaron Wile, Chrissa LaPorte, Oriel Francis
 * @since 16 April 2018
 */

public class Article
{
	//------------------------------------------------------------------------------------------------------------
	// Instance Variables
	//------------------------------------------------------------------------------------------------------------
	
	private String title, source, date, description, topic, url;
	private int databaseID;
	
	//------------------------------------------------------------------------------------------------------------
	// Constructors
	//------------------------------------------------------------------------------------------------------------
	
	/**
	 * The default Article constructor allows for the creation of Article objects without defining any of the
	 * instance variables.
	 */
	
	public Article()
	{
	}
	
	/**
	 * The overloaded Article Constructor accepts a JsonElement argument (from the Gson external library) and
	 * initializes the Article objects fields according to the data from the JsonElement. This constructor is
	 * intended for use with newsAPI's JSON output.
	 * 
	 * @param jsonElement A JsonElement representing a news article from newsAPI.
	 */
	
	public Article(JsonElement jsonElement)
	{
		//"Cast" the JsonElement to a JsonObject in order to retrieve member data
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		
		//Use Gson get method to retrieve relevent data from JSON Article
		//Use replaceAll method to delete punctuation from Title (for now)
		title = jsonObject.get("title").toString();
		source = jsonObject.get("source").getAsJsonObject().get("name").toString();
		date = jsonObject.get("publishedAt").toString().replaceAll("\"", "");
		description = jsonObject.get("description").toString();
		url = jsonObject.get("url").toString();
	}
	
	//------------------------------------------------------------------------------------------------------------
	// Accessor Methods
	//------------------------------------------------------------------------------------------------------------
	
	/**
	 * The getTitle method returns an Article's title field.
	 * 
	 * @return A String containing the Article's title.
	 */
	
	public String getTitle()
	{
		return title;
	}
	
	/**
	 * The getSource method returns an Article's source field.
	 * 
	 * @return A String containing the Article's source.
	 */
	
	public String getSource()
	{
		return source;
	}
	
	/**
	 * The getDate method returns an Article's date field.
	 * 
	 * @return A String containing the Article's date.
	 */
	
	public String getDate()
	{
		return date;
	}
	
	/**
	 * The getDescription method returns an Article's description field.
	 * 
	 * @return A String containing the Article's description.
	 */
	
	public String getDescription()
	{
		return description;
	}
	
	/**
	 * The getURL method returns an Article's url field.
	 * 
	 * @return A String containing the Article's URL.
	 */
	
	public String getURL()
	{
		return url;
	}
	
	/**
	 * The getTopic method returns an Article's topic field.
	 * 
	 * @return A String containing the Article's topic.
	 */
	
	public String getTopic()
	{
		if(topic != null)
			return topic;
		else
			return null;
	}
	
	/**
	 * The getID method returns an Article's databaseID field, for use with the SQL database.
	 * 
	 * @return An int containing the Article's SQL databaseID.
	 */
	
	public int getID()
	{
		return databaseID;
	}
	
	
	//------------------------------------------------------------------------------------------------------------
	// Mutator Methods
	//------------------------------------------------------------------------------------------------------------
	
	/**
	 * The setTitle method accepts a String argument and sets the Article's title field accordingly.
	 * 
	 * @param title A String containing the updated Article title.
	 */
	
	public void setTitle(String title)
	{
		//Use replaceAll method to remove punctuation
		this.title = title;
	}
	
	/**
	 * The setSource method accepts a String argument and sets the Article's source field accordingly.
	 * 
	 * @param source A String containing the updated Article source.
	 */
	
	public void setSource(String source)
	{
		this.source = source;
	}
	
	/**
	 * The setDate method accepts a String argument and sets the Article's date field accordingly.
	 * The date should be in ISO 8601 format.
	 * 
	 * @param date A String containing the updated Article date.
	 */
	
	public void setDate(String date)
	{
		this.date = date;
	}
	
	/**
	 * The setDescription method accepts a String argument and sets the Article's description field accordingly.
	 * 
	 * @param description A String containing the updated Article description.
	 */
	
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	/**
	 * The setURL method accepts a String argument and sets the Article's url field accordingly.
	 * 
	 * @param url A String containing the updated url field.
	 */
	
	public void setURL(String url)
	{
		this.url = url;
	}
	
	/**
	 * The setTopic method accepts a String argument and sets the Article's topic field accordingly.
	 * 
	 * @param topic A String containing the updated topic field.
	 */
	
	public void setTopic(String topic)
	{
		this.topic = topic;
	}
	
	/**
	 * The setID method accepts an int argument and sets the Article's databaseID field accordingly.
	 * 
	 * @param databaseID An int containing the Article's SQL Database ID.
	 */
	
	public void setID(int databaseID)
	{
		this.databaseID = databaseID;
	}
	
	//------------------------------------------------------------------------------------------------------------
	// Helper Methods
	//------------------------------------------------------------------------------------------------------------
 
	/**
	 * The generateTopic method calls the ParallelDots keyword API to generate a topic off of an article's most
	 * important entities. It then updates an Article's topic field accordingly.
	 */
	
	public void generateTopic()
	{
		//Use try/catch to handle Possible exception
		try
		{
			//Reset Topic Field
			topic = "";
			
			//Instantiate ParallelDots object, passing API key
			ParallelDots pdObject = new ParallelDots("Kwr4COFZaeu3JHwOPXuIHYFqeqdaajmAkF5VclV49Hw");
			      
			//Get Json Output from Parallel Dots based on current description field, save as String
			String jsonString = pdObject.ner(this.description);
			      
			//Use Gson classes to parse Json String into a JsonArray of Keywords
			JsonParser pdParser = new JsonParser();
			JsonObject jsonObject = pdParser.parse(jsonString).getAsJsonObject();
			JsonArray keywordArray = jsonObject.get("entities").getAsJsonArray();
			
			//Instantiate TreeMap to Hold keywords by Confidence Score
			TreeMap<Double, String> scoreMap = new TreeMap<Double, String>();
			
			//Add Entities from ParallelDots to TreeMap, using scores as Keys and Entities as Values
			for(JsonElement keyword : keywordArray)
				scoreMap.put(keyword.getAsJsonObject().get("confidence_score").getAsDouble(), 
							 keyword.getAsJsonObject().get("name").toString());
			
			//Pull 2 Highest Scoring Keywords from TreeMap and Concatenate them into Topic Field
			int keywordsAdded = 0;
			for(Double key : scoreMap.descendingKeySet())
			{
				if(keywordsAdded == 0)
				{
					topic += scoreMap.get(key);
					keywordsAdded++;
					if(scoreMap.size() > 1)
						topic += " and ";
				}
				else if(keywordsAdded == 1)
				{
					topic += scoreMap.get(key);
					keywordsAdded++;
				}
			}
			
			//Remove unwanted Quotation Marks
			topic = topic.replaceAll("\"", "");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * The getTimeStamp method generate's a java.sql.Timestamp object that represents an Article's date field.
	 * 
	 * @return A java.sql.Timestamp object representing an Article's date field.
	 */
	
	public java.sql.Timestamp getTimeStamp() throws Exception
	{
		//Instantiate DateTimeFormatter object to parse from ISO 8601 string
		DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_DATE_TIME;
		
		//Parse Date Field into TemporalAccessor
	    TemporalAccessor accessor = timeFormatter.parse(this.date);
	    Date dateObj = Date.from(Instant.from(accessor));
	    
	    //Turn util.Date into sql.Date
	    java.sql.Timestamp returnDate = new java.sql.Timestamp(dateObj.getTime());
	    return returnDate;
	}

}