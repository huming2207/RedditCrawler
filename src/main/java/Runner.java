import org.apache.commons.lang3.time.StopWatch;

import java.io.IOException;
import java.util.Scanner;

public class Runner
{
    public static void main(String[] args)
    {
        StopWatch stopWatch = new StopWatch();

        stopWatch.start();

        Scanner scanner = new Scanner(System.in);

        System.out.print("[INPUT] Enter reddit page: ");
        String redditUrl = scanner.nextLine();

        System.out.print("[INPUT] Enter output path: ");
        String outputPath = scanner.nextLine();

        ImgurHandler imgurHandler = new ImgurHandler();

        try
        {
            imgurHandler.filterAndSaveImgur(outputPath, RedditHandler.grabRawRedditRepliesHtml(redditUrl), redditUrl);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        stopWatch.stop();
        System.out.println(String.format("[INFO] Done, took time %d ms.", stopWatch.getTime()));
    }
}
