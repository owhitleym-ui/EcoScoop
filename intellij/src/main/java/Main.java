import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main (String args[])
            throws XmlPullParserException, IOException
    {
        Path filePath = Paths.get("Grist");
        String content = Files.readString(filePath);

        ArticleParser app = new ArticleParser();
        app.parse(args, content, "Grist");

        for (Article article : app.loadArticles()) {
            System.out.println(article);
        }
    }
}
