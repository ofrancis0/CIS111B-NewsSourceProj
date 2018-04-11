import java.io.*;
import java.net.*;
import com.google.gson.*;

import java.sql.Connection;
import java.sql.DriverManager;

//  to run query
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
// to use with PreparedStatement
import java.sql.PreparedStatement;

// This class makes a call to NewsAPI and then inputs the data into a MySQL database
public class NewsAPItoSQL
{

    public static void main(String[] args) throws Exception {

        //URL for newsAPI Link  - requesting headlines only (count of 20)
        String newsLink = "https://newsapi.org/v2/top-headlines?country=us&apiKey=a513991c58ac4bc58031b3bc981c4d5b";

        //Use the getArticleArray method to create a JsonArray of articles from the newsAPI link
        JsonArray returnArray = getArticleArray(newsLink);


        // See separate NewsItem class file - it just felt easier to create a class
        // Create an array of NewsItem objects
        NewsItem[] newsItems = new NewsItem[returnArray.size()];


/*
    DONE: extract from JSON  id or name of the news item (from within source)
    Aaron, you are more skilled with JSON than I am so perhaps there is a more efficient way!

    DONE: escape the ' characters in the strings - not necessary if using a PreparedStatement,
    as you'll see below

 */
        /*  use loop to create each of the NewsItem objects and then populate the different fields
            the deleteQuotes method only deletes quotes from the author field for the moment
            but it does work
        */
        for (int i = 0; i < returnArray.size(); i++) {
            newsItems[i] = new NewsItem();
            newsItems[i].setUrl(returnArray.get(i).getAsJsonObject().get("url").toString());
            newsItems[i].setAuthor(returnArray.get(i).getAsJsonObject().get("author").toString());
            deleteQuotes(newsItems[i]);
            newsItems[i].setTitle(returnArray.get(i).getAsJsonObject().get("title").toString());
            newsItems[i].setDescription(returnArray.get(i).getAsJsonObject().get("description").toString());
        }

        // create an ArrayList to hold the information from JSON on sources
        // since I only need either source id or source name, just added source name to the ArrayList
        List<String> sources = new ArrayList<>();
        for (int i = 0; i < returnArray.size(); i++)
        {
            JsonElement step1 = returnArray.get(i);
            JsonObject step2 = step1.getAsJsonObject();
            JsonObject step3 = step2.get("source").getAsJsonObject();
            String sourceName = step3.getAsJsonObject().get("name").toString();
            sources.add(sourceName);
        }


        // then add the source name to each of the NewsItem objects in the Array of newsItem objects
        for(int i = 0; i<sources.size(); i++)
        {
            newsItems[i].setSourceName(sources.get(i));
        }

        System.out.println(newsItems[1]);

        // TODO create methods to improve the flow of the program
        // This section of the code creates a connection with a SQL database
        // Open a connection
        Connection conn = null;

        // try connecting to the mySQL database
        try {
            // DON'T FORGET to update this line to indicate active user name and password
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/news_test",
                    "user", "password");

            // create a PreparedStatement object to send "insert row" statement to MySQL
            // initially had it in the loop but pulled it out of the loop
            PreparedStatement insertRow = null;
            // define the string for the query
            // use ? to indicate the parameter to be inserted (total of 5 parameters)
            String sqlStatement = "INSERT INTO news_item " +
                    "(website_name, author, title, description, url) " +
                    "Values"
                    + "(?,?,?,?,?)";

            // prepare the statement
            insertRow = conn.prepareStatement(sqlStatement);

            // count variable to hold number of rows inserted
            int count = 0;

            // loop through array of news items to set the different fields for each inserted row in table
            for (int i = 0; i < newsItems.length; i++)
            {
                insertRow.setString(1, newsItems[i].getSourceName());
                insertRow.setString(2, newsItems[i].getAuthor());
                insertRow.setString(3, newsItems[i].getTitle());
                insertRow.setString(4,newsItems[i].getDescription());
                insertRow.setString(5, newsItems[i].getUrl());

                int rows = insertRow.executeUpdate();
                count++;
            }
            System.out.println(count + " row(s) were inserted");

            // close connection with DB
            conn.close();
        }
        catch (Exception ex)
        {
            System.out.println("Error: " + ex.getMessage());
        }


    }
        /**
         * The getArticleArray method takes a URL to a NewsAPI Json file and returns a JsonArray, from the Gson library,
         * in which each individual element represents a separate article.
         *
         *@param url A String containing the NewsAPI url
         *@return A JsonArray populated by articles from newsAPI
         */

        private static JsonArray getArticleArray(String url) throws Exception
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

            // return a JsonArray using Gson of Articles
            return jsObject.get("articles").getAsJsonArray();

        }

    /**
     * The deleteQuotes method deletes the " in the Json text, which is the first and last character of each field
     * This method is still in progress, since it only works so far with the author field
     * @param news the method takes a NewsItem object news
     */
        public static void deleteQuotes(NewsItem news)
        {
            // some text fields include a "null" string, so ignore the field with "null" text
            if(!news.getAuthor().equals("null"))
            {
                // create StringBuilder object from author field of the NewsItem object
                StringBuilder cleanAuthor = new StringBuilder(news.getAuthor());
                // get the length of the StringBuilder string

                int length = cleanAuthor.length();
                // delete last character in the string (") which is at index: length -1
                cleanAuthor.deleteCharAt(length - 1);

                // delete first character in the string (also a ")
                cleanAuthor.deleteCharAt(0);

                // convert the StringBuilder object back to a String and update the author field of NewsItem object
                String result = cleanAuthor.toString();
                news.setAuthor(result);
            }

        }

}


