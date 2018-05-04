
public class TestDriver
{
	
	public static void main(String[] args) throws Exception
	{
		NewsAPIConnector newsConn = new NewsAPIConnector();
		Article[] articleArray = newsConn.getArticleArray();
	}
}