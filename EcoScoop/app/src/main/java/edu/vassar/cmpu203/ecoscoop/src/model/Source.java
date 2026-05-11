package edu.vassar.cmpu203.ecoscoop.src.model;

/**
 * Holds information about where an article came from,
 * including the website name, the article URL, and the publish date.
 */
public class Source {
    private final String websiteName;
    private final String url;
    private final String publishDate;

    /**
     * Creates a Source with the given publication details.
     *
     * @param websiteName the name of the publication
     * @param url         the direct link to the article
     * @param publishDate when the article was published
     */
    public Source(String websiteName, String url, String publishDate){
        this.websiteName = websiteName;
        this.url = url;
        this.publishDate = publishDate;
    }

    /** Returns the name of the website that published the article. */
    public String getWebsiteName(){
        return websiteName;
    }

    /** Returns the direct URL to the article. */
    public String getUrl(){
        return url;
    }

    /** Returns the publish date as a string. */
    public String getPublishDate() {
        return publishDate;
    }

    /** Returns a readable summary of the source info. */
    public String toString(){
        return websiteName + " " + url + " " + publishDate;
    }
}
