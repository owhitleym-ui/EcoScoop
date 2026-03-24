import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.Files;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;



public class MyXmlPullApp
{
    private String startTag;
    private String endTag;
    private XmlPullParser parser;
    private XmlPullParserFactory factory;

    public void processDocument(XmlPullParser xpp)
            throws XmlPullParserException, IOException
    {
        int eventType = xpp.getEventType();
        do {
            if(eventType == xpp.START_DOCUMENT) {
                System.out.println("Start document");
            } else if(eventType == xpp.END_DOCUMENT) {
                System.out.println("End document");
            } else if(eventType == xpp.START_TAG) {
                processStartElement(xpp);
            } else if(eventType == xpp.END_TAG) {
                processEndElement(xpp);
            } else if(eventType == xpp.TEXT) {
                processText(xpp);
            }
            eventType = xpp.next();
        } while (eventType != xpp.END_DOCUMENT);
    }


    public void processStartElement (XmlPullParser xpp)
    {
        String name = xpp.getName();
        String uri = xpp.getNamespace();
        if ("".equals (uri)) {
            System.out.println("Start element: " + name);
        } else {
            System.out.println("Start element: {" + uri + "}" + name);
        }
    }


    public void processEndElement (XmlPullParser xpp)
    {
        String name = xpp.getName();
        String uri = xpp.getNamespace();
        if ("".equals (uri))
            System.out.println("End element: " + name);
        else
            System.out.println("End element:   {" + uri + "}" + name);
    }

    int holderForStartAndLength[] = new int[2];

    public void processText (XmlPullParser xpp) throws XmlPullParserException
    {
        char ch[] = xpp.getTextCharacters(holderForStartAndLength);
        int start = holderForStartAndLength[0];
        int length = holderForStartAndLength[1];
        System.out.print("Characters:    \"");
        for (int i = start; i < start + length; i++) {
            switch (ch[i]) {
                case '\\':
                    System.out.print("\\\\");
                    break;
                case '"':
                    System.out.print("\\\"");
                    break;
                case '\n':
                    System.out.print("\\n");
                    break;
                case '\r':
                    System.out.print("\\r");
                    break;
                case '\t':
                    System.out.print("\\t");
                    break;
                default:
                    System.out.print(ch[i]);
                    break;
            }
        }
        System.out.print("\"\n");
    }
}
