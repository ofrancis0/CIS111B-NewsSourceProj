
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
		//Use NewsAPIConnector class to connecto to NewsAPI and receive Article Array
		
		//Iterate over Article Array, using SQLReadWriter class to write each Article to the database
		
		//Pause for a set amount of time (Perhaps half an hour) before repeating
		//Must pause to deal with limited calls to NewsAPI per day
	}
}
