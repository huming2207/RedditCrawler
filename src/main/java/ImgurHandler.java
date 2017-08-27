import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.FilenameUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ImgurHandler
{
    public void filterAndSaveImgur(String outputPath,
                                   ArrayList<String> redditReplies, String redditUrl)
                                    throws IOException
    {
        for(String redditReply : redditReplies)
        {
            System.out.println(String.format("[INFO] Parsing reply: %s", redditReply));

            if(redditReply.contains("imgur.com/"))
            {
                Document replyDocument = Jsoup.parse(redditReply);

                for(Element linkElement : replyDocument.select("a"))
                {
                    String pageUrl = linkElement.absUrl("href");

                    if(!pageUrl.isEmpty() && pageUrl.contains("i.imgur.com/"))
                    {
                        fileDownloader(outputPath, pageUrl, redditUrl);
                    }
                    else if(!pageUrl.isEmpty() && pageUrl.contains("imgur.com/"))
                    {
                        String imgUrl = getImgurPicUrl(pageUrl, redditUrl);

                        System.out.println(String.format("[INFO] Found image from %s", imgUrl));

                        // Download the file via OKHttp
                        fileDownloader(outputPath, imgUrl, pageUrl);
                    }
                }


            }
            else
            {
                System.out.println("[WARN] Imgur link not found.");
            }

        }

    }

    private String getImgurPicUrl(String url, String redditUrl) throws IOException
    {
        // Get a imgur page
        Document imgurDocument = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) " +
                        "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36")
                .referrer(redditUrl)
                .header("DNT","1")
                .get();

        // Get element
        Element imgElement = imgurDocument
                .getElementsByAttributeValue("rel", "image_src").get(0);

        // Get imgur iamge url
        return imgElement.absUrl("href");
    }

    private void fileDownloader(String outputPath, String imgUrl, String pageUrl) throws IOException
    {
        OkHttpClient httpClient = new OkHttpClient();
        Request request = new Request.Builder()

                // Set image URL
                .url(imgUrl)

                // Remove original "Okhttp/3.x" user agent from header
                .removeHeader("User-Agent")

                // Add a fake one lol
                .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) " +
                        "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36")

                // Add some other fake headers
                .addHeader("DNT", "1")
                .addHeader("Referer", pageUrl)
                .get()
                .build();

        Response response = httpClient.newCall(request).execute();

        if(!response.isSuccessful())
        {
            System.err.println(String.format("[ERROR] Failed to download file from %s at page %s", imgUrl, pageUrl));
            return;
        }

        FileOutputStream fileOutputStream =
                new FileOutputStream(outputPath + "/" + FilenameUtils.getName(imgUrl));

        fileOutputStream.write(response.body().bytes());
        fileOutputStream.close();
    }
}
