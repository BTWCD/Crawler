package bean;

public class NewsBean {

    private String sourceUrl;
    private String newsType;
    private String title;
    private String author;
    private String publishTime;
    private String contentText;
    private String contentCode;
    private String sourceName;
    private String fetchDate;

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl( String sourceUrl ) {
        this.sourceUrl = sourceUrl;
    }

    public String getNewsType() {
        return newsType;
    }

    public void setNewsType( String newsType ) {
        this.newsType = newsType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle( String title ) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor( String author ) {
        this.author = author;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime( String publishTime ) {
        this.publishTime = publishTime;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText( String contentText ) {
        this.contentText = contentText;
    }

    public String getContentCode() {
        return contentCode;
    }

    public void setContentCode( String contentCode ) {
        this.contentCode = contentCode;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName( String sourceName ) {
        this.sourceName = sourceName;
    }

    public String getFetchDate() {
        return fetchDate;
    }

    public void setFetchDate( String fetchDate ) {
        this.fetchDate = fetchDate;
    }

    @Override
    public String toString() {
        return "NewsBean{" +
                "sourceUrl='" + sourceUrl + '\'' +
                ", newsType='" + newsType + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", publishTime='" + publishTime + '\'' +
                ", contentText='" + contentText + '\'' +
                ", contentCode='" + contentCode + '\'' +
                ", sourceName='" + sourceName + '\'' +
                ", fetchDate='" + fetchDate + '\'' +
                '}';
    }
}
