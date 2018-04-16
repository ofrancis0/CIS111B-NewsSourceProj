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
	
	private String title, source, date, url;
	
	//------------------------------------------------------------------------------------------------------------
	// Constructors
	//------------------------------------------------------------------------------------------------------------
	
	/**
	 * The first Article Constructor accepts a JsonElement argument (from the Gson external library) and
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
	
	
	//INSERT SECOND ARTICLE CONSTRUCTOR THAT ACCEPTS AN ARGUMENT FROM SQL DATABASE
	
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
	 * an Article Object argument and returns a boolean value indicating whether the articles match.
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
				
		//If two or more keywords remain in intersection, return true
		if(articleKeywords.size() >= 2)
			return true;
		else
			return false;
	}	
	
}