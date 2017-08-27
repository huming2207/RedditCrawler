import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class RedditHandler
{
    public static ArrayList<String> grabRawRedditRepliesHtml(String url)
    {
        ArrayList<String> redditReplies = new ArrayList<>();

        try
        {
            // Get the replies from a reddit topic
            Document redditPageDoc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) " +
                            "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36")
                    .referrer("https://www.google.com.au")
                    .header("DNT","1")
                    .get();

            Elements rawReplyElements = redditPageDoc.getElementsByClass("md");

            for(Element element : rawReplyElements)
            {
                redditReplies.add(element.html());
            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        System.out.println(String.format("[INFO] Got %d replies!", redditReplies.size()));
        return redditReplies;
    }

}
