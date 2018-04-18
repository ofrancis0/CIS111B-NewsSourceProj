import java.util.*;

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
	
	private String title, source, date, topic, url;
	
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
		title = jsonObject.get("title").toString().replaceAll("[^\\w\\s]", "");
		source = jsonObject.get("source").getAsJsonObject().get("name").toString();
		date = jsonObject.get("publishedAt").toString();
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
		this.title = title.replaceAll("[^\\w\\s]", "");
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
	
	//------------------------------------------------------------------------------------------------------------
	// Matching Methods
	//------------------------------------------------------------------------------------------------------------
	
	//THESE METHODS MUST BE COMPLETELY REPLACED, POSSIBLY WITH REFERENCE TO DANDELION API FOR TEXT ANALYTICS
	//FOR NOW, THIS IS PLACEHOLDER CODE WHILE THE OTHER TECHNICAL ASPECTS ARE WORKED OUT
	
	/**
	 * The getKeywords method creates a set of keywords from an article's title. Keywords are defined as words
	 * greater than 3 characters in length.
	 * 
	 * @return A String set containing an article's keywords.
	 */
	
	public Set<String> getKeywords()
	{
		//Split Title of Article into String Array of Words
		String[] titleArray = title.split("\\W");
		
		//Sort array alphabetically
		Arrays.sort(titleArray);
		
		//Convert String Array into Set to remove duplicates
		Set<String> titleSet = new HashSet<String>(Arrays.asList(titleArray));
		
		//Remove All Words <4 letters
		Set<String> keywordSet = new HashSet<String>(); //A set to hold all >3 char words from title
		for(String word : titleSet)
			if(word.length() >= 4)
				keywordSet.add(word); //Add each word of appropriate length to keywordSet
		
		return keywordSet;
	}
	
	/**
	 * The match method compares two Article objects and determines if they are about the same topic. It accepts
	 * an Article Object argument and returns a boolean value indicating whether the articles match. It also
	 * updates both Articles' topic field if there is a match.
	 * 
	 * @param crossRefArticle An Article object to compare against this Article to determine a match.
	 * @return True if the articles are about the same topic, false if not.
	 */
	
	public boolean match(Article crossRefArticle)
	{
		//Use getKeywords method to create set of both this Article's and crossRefArticle's keywords
		Set<String> articleKeywords = this.getKeywords();
		Set<String> crossRefKeywords = crossRefArticle.getKeywords();
		
		//Create Intersection of both sets
		articleKeywords.retainAll(crossRefKeywords);
		
		//If two or more keywords remain in intersection, return true and update topic field
		if(articleKeywords.size() >= 2)
		{
			//If topic field does not exist, create topic field for both
			if(this.topic == null && crossRefArticle.getTopic() == null)
			{
				this.topic = "";
			
				for(String keyword : articleKeywords)
				topic += keyword + " ";
			}
			//Else if topic field exists for this, but not crossRefArticle, update CrossRefArticle
			else if(this.topic != null && crossRefArticle.getTopic() == null)
			{
				crossRefArticle.setTopic(this.topic);
			}
			//Else if topic field exists for crossRefArticle, but not this, update this
			else if(this.topic == null && crossRefArticle.getTopic() != null)
			{
				this.topic = crossRefArticle.getTopic();
			}

			return true;
		}
		else
			return false;
	}
	
	//------------------------------------------------------------------------------------------------------------
	// Testing Methods
	//------------------------------------------------------------------------------------------------------------
	
	/**
	 * The toString() method overrides Java.Object's toString method, which allows for printing Article info out
	 * to the console.
	 * 
	 * @return A String containing information on an article's fields.
	 */
	
	@Override
	public String toString()
	{
		return "Title: " + title + " Source: " + source + " Date: " + date + " URL: " + url;
	}
	
}