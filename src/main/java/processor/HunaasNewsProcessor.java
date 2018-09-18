package processor;

import bean.NewsBean;
import dao.NewsDao;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import util.ImageUtil;
import util.PropertiesReader;
import util.TimeFormatUtils;
import util.UrlRegexUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class HunaasNewsProcessor implements PageProcessor {

    private Site site = Site.me().setSleepTime( 1000 ).setRetryTimes( 4 );
    private static final String NEWSLISTREX = "http://www\\.hunaas\\.cn/news\\.asp\\?cid=\\d+";
    private static final String SHOWNEWSURL = "http://www\\.hunaas\\.cn/shownews\\.asp\\?nid=\\d+";

    private static final String DOMAIN = "http://www.hunaas.cn";
    private static final String IMGSTOREPATH = "cn/hunaas";
    private static String storePath = PropertiesReader.getPicStorePath();
    private static String serverHost = PropertiesReader.getServerHost();

    @Override
    public void process( Page page ) {
        if(page.getUrl().get().matches( SHOWNEWSURL )){
            String title = page.getHtml().xpath( "//div[@class='ArticleTitle']/text()" ).get();
            String messageInfo = page.getHtml().xpath( "//div[@class='ArticleMessage']/text()" ).get();
            String author = messageInfo.split( "阅读" )[0].trim();
            String publishTime = messageInfo.split( "时间:" )[1].split( "字体:" )[0].trim();
            String contentText = page.getHtml().css( ".ArticleTencont", "allText" ).get();
            String contentCode = page.getHtml().css( ".ArticleTencont" ).get();
            String type = page.getHtml().xpath( "//div[@class='col1_tw']/text()" ).get();

            List<String> showNewsList = page.getHtml().xpath( "//div[@class='NewsPreNext']" ).links().all();
            for(String url : showNewsList){
                if(url.matches( SHOWNEWSURL )){
                    page.addTargetRequest( url );
                }
            }

            List<String> imgUrls = page.getHtml().xpath( "//div[@id='article']/p/img/@src" ).all();
            for(String url : imgUrls){
                StringBuffer imgUrl = new StringBuffer( "" );
                StringBuffer imgStorePath = new StringBuffer( "" );
                StringBuffer imageSrcPath = new StringBuffer( "" );
                String localPath = UrlRegexUtil.getDomainName( url );

                if( UrlRegexUtil.isHttpUrl( url )){
                    imgUrl = imgUrl.append( url );
                    imgStorePath = imgStorePath.append(storePath).append(localPath);
                    imageSrcPath = imageSrcPath.append( serverHost ).append( imgStorePath );
                } else {
                    imgUrl = imgUrl.append( DOMAIN ).append( url );
                    imgStorePath = imgStorePath.append(storePath).append( IMGSTOREPATH ).append(localPath);
                    imageSrcPath = imageSrcPath.append( serverHost ).append( imgStorePath );
                }
                ImageUtil.getImage( imgUrl.toString(), imgStorePath.toString() );
                contentCode = contentCode.replace( url, imageSrcPath.toString() );
            }

            NewsBean bean = new NewsBean();
            bean.setSourceUrl( page.getUrl().get() );
            bean.setTitle( title );
            bean.setAuthor( author );
            bean.setPublishTime( publishTime );
            bean.setContentCode( contentCode );
            bean.setContentText( contentText );
            bean.setNewsType( type );
            bean.setSourceName( "湖南省农科院" );
            bean.setFetchDate( TimeFormatUtils.INSTANCE.formatFetchTime( new Date().getTime() ) );

            NewsDao.insertNews( bean );

        }else if(page.getUrl().get().matches( NEWSLISTREX )){
            List<String> urlList = page.getHtml().xpath( "//ul[@class='news_list']" ).links().regex( SHOWNEWSURL ).all();
            System.out.println(urlList);
            page.addTargetRequests( urlList );
        }
    }

    @Override
    public Site getSite() {
        return site;
    }



    public static void main( String[] args ) {

    }
}
