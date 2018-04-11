/**
 * The NewsItem class creates objects pertaining to individual news articles, holding
 * basic data about the article in question.
 *
 */


//TODO revisit list of fields needed for the class
public class NewsItem
{
    private String url;              // to hold article url
    private String author;           // for article's author
    private String title;            // for article's title
    private String description;      // for article's description
    private String sourceName;       // to hold the article's publication

    /**
     *  As a starting point, the default NewsItem constructor sets the fields to empty Strings as a default.
     */
    public NewsItem()
    {
        this.url = "";
        this.author = "";
        this.title = "";
        this.description ="";
        this.sourceName = "";
    }

    /**
     * getUrl method provides the URL for the article
     * @return url as a String
     */
    public String getUrl() {
        return url;
    }

    /**
     * setUrl method sets the article's URL as a String
     * @param url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * getAuthor method provides the article's author
     * @return author as a String
     */
    public String getAuthor() {
        return author;
    }

    /**
     * setAuthor method allows you to set the article's author
     * @param author
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * getTitle method provides the article's title
     * @return a String for the article title
     */
    public String getTitle() {
        return title;
    }

    /**
     * setTitle method sets the article's title
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * getDescription method provides a description of the article
     * @return the article's description as a String
     */
    public String getDescription() {
        return description;
    }

    /**
     * setDescription method sets the article's description
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * getSourceName method provides the name of the article's publication
     * @return a String for the name of the source (publication)
     */
    public String getSourceName() {
        return sourceName;
    }

    /**
     * setSourceName method allows you to set the publication name
     * @param sourceName
     */
    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    /**
     * toString method provides the basic information on a NewsItem object
     * @return a String with the basic data on a NewsItem
     */
    @Override
    public String toString() {
        return "NewsItem{" +
                "url='" + url + '\'' +
                ", author='" + author + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", sourceName='" + sourceName + '\'' +
                '}';
    }
}
