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
        Path filePath = Paths.get("Earth911");
        String content = Files.readString(filePath);

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        System.out.println("parser implementation class is "+xpp.getClass());

        MyXmlPullApp app = new MyXmlPullApp();

        if(args.length == 0) {
            System.out.println("Parsing simple sample XML");//:\n"+ SAMPLE_XML);
            xpp.setInput( new StringReader( content ) );
            app.processDocument(xpp);
        } else {
            for (int i = 0; i < args.length; i++) {
                System.out.println("Parsing file: "+args[i]);
                xpp.setInput ( new FileReader( args [i] ) );
                app.processDocument(xpp);
                }
        }
        for (Article article : app.getArticles()) {
            System.out.println(article);
        }
    }
}
