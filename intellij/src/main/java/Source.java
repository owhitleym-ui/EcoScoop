public class Source {
    private final String websiteName;
    private final String url;
    private final String publishDate;

    public Source(String websiteName, String url, String publishDate){
        this.websiteName = websiteName;
        this.url = url;
        this.publishDate = publishDate;
    }

    public String getWebsiteName(){
        return websiteName;
    }

    public String getUrl(){
        return url;
    }

    public String getPublishDate() {
        return publishDate;
    }
}
