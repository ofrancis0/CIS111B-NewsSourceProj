import java.util.*;
import java.time.format.*;
import java.time.temporal.*;
import java.time.Instant;

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
	 * The generateTopic method creates a set of words longer than 4 characters and concatenates that set into
	 * a String. It then updates the topic field to that String.
	 */
	
	public void generateTopic()
	{
		//Split Title of Article into String Array of Words
		String[] titleArray = title.split("\\W");
			
		//Convert String Array into Set to remove duplicates
		Set<String> titleSet = new HashSet<String>(Arrays.asList(titleArray));
			
		//Remove All Words <4 letters
		Set<String> keywordSet = new HashSet<String>(); //A set to hold all >3 char words from title
		for(String word : titleSet)
			if(word.length() >= 4)
				keywordSet.add(word); //Add each word of appropriate length to keywordSet
			
		topic = "";
		for(String word : keywordSet)
			topic += word + " ";
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