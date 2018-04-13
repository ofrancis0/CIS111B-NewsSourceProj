
public class TestDriver {
	
	public static void main(String[] args) throws Exception
	{
		NewsAPIConnector newsConn = new NewsAPIConnector();
		System.out.println(newsConn.getArticleArray());
	}

}
