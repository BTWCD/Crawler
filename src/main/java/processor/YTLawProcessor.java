package processor;

import bean.NewsBean;
import dao.NewsDao;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import util.*;

import java.io.IOException;
import java.util.*;

public class YTLawProcessor implements PageProcessor {

    private Site site = Site.me().setRetryTimes( 4 ).setRetryTimes( 1000 );
    private static final String LAWREGEX = "http://www\\.yt1998\\.com/lawMinute--\\d+\\.html";
    private static final String DOMAIN = "http://www.yt1998.com";
    private static final String IMGSTOREPATH = "/com/yt1998";
    private static String storePath = PropertiesReader.getPicStorePath();
    private static String serverHost = PropertiesReader.getServerHost();
    private static Logger log = LoggerFactory.getLogger( YTLawProcessor.class );

    @Override
    public void process( Page page ) {
        if(page.getUrl().get().matches( LAWREGEX )){
            String title = page.getHtml().xpath( "//h1[@class='mb10']/text()" ).get();
            String author = page.getHtml().xpath( "//p[@class='gray']/text()" ).get();
            String source = page.getHtml().getDocument().select("span:contains(来源：)").text();
            String publishTime = page.getHtml().getDocument().select("span:contains(时间：)").text();
            String contentText = page.getHtml().css( "div.f14.lh28.mt20", "allText" ).get();
            String contentCode = page.getHtml().css( "div.f14.lh28.mt20" ).get();

            Elements elements = page.getHtml().getDocument().select( "div.f14.lh28.mt20")
                    .get( 0 ).getElementsByTag( "img" );
            for(Element e : elements){
                String url = e.attr( "src" );
                System.out.println(url);
                StringBuffer imgUrl = new StringBuffer( "" );
                StringBuffer imgStorePath = new StringBuffer( "" );
                StringBuffer imageSrcPath = new StringBuffer( "" );
                String localPath = UrlRegexUtil.getDomainName( url );
                String referPath = page.getUrl().get() + localPath.replace( "com/yt1998","" );

                if( UrlRegexUtil.isHttpUrl( url )){
                    imgUrl = imgUrl.append( url );
                    imgStorePath = imgStorePath.append(storePath).append(localPath);
                    imageSrcPath = imageSrcPath.append( serverHost ).append( imgStorePath );
                } else {
                    imgUrl = imgUrl.append( DOMAIN ).append( url );
                    imgStorePath = imgStorePath.append(storePath).append( IMGSTOREPATH ).append(localPath);
                    imageSrcPath = imageSrcPath.append( serverHost ).append( imgStorePath );
                }
                YTImageUtil.getYaoTongImage( imgUrl.toString(), imgStorePath.toString(), referPath );
                contentCode = contentCode.replace( url, imageSrcPath.toString() );
            }

            NewsBean bean = new NewsBean();
            bean.setSourceUrl( page.getUrl().get() );
            bean.setTitle( title );
            bean.setAuthor( author );
            bean.setPublishTime( publishTime );
            bean.setContentCode( contentCode );
            bean.setContentText( contentText );
            bean.setNewsType( "新闻法规" );
            bean.setSourceName( "中药材药通网" );
            bean.setFetchDate( TimeFormatUtils.INSTANCE.formatFetchTime( new Date().getTime() ) );

            System.out.println("!=================");
            System.out.println(bean);
            System.out.println("!!=================");

//            List<String> newsUrl = page.getHtml().xpath( "//div[@class='m-page']" ).links().all();
//            for(String s : newsUrl){
//                if( StringUtils.isNotBlank( s ) && s.matches( LAWREGEX )){
//                    page.addTargetRequest( s );
//                }
//            }
            NewsDao.insertNews( bean );
        }
    }

    private static int getNewsUrlTotal(){
        Connection connection = Jsoup.connect( "http://www.yt1998.com/ytw/second/marketMgr/query.jsp?random=0.6724341135038809&lmid=4&ycnam=&pageIndex=0&pageSize=1" ).ignoreContentType(true).timeout( 50000 );

        connection.header( "User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:62.0) Gecko/20100101 Firefox/62.0" );
        connection.header( "Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8" );
        connection.header( "Accept-Encoding","gzip,deflate");
        connection.header( "Host","www.yt1998.com" );
        connection.header( "Cookie","Hm_lvt_21f2fde8228a3428719fdc5669ab5410=1535962499,1536638143,1536670986,1536733562; JSESSIONID=6CB4D18DDF345C39661FB7C34D7F67AF; Hm_lpvt_21f2fde8228a3428719fdc5669ab5410=1536736485");

        try {
            Document doc = connection.get();
            Map<String,Object> mapper = ( Map< String, Object > ) JacksonMapper.readJsonToObject( doc.body().text() );
            int total = Integer.parseInt(String.valueOf(mapper.get( "total" )));
            return total;
        } catch ( IOException e ) {
            log.error( "获取新闻总数异常，异常信息：{}", e.getMessage() );
            return 0;
        }
    }

    private static List<String> getNewsList(){
        int total = getNewsUrlTotal();
        List<String> urlList = new LinkedList<>(  );
        log.info( "共有新闻：{}条" ,total );
        int page = total/10;
        for(int i=0;i<=page;i++){
            String url = "http://www.yt1998.com/ytw/second/marketMgr/query.jsp?random=0.6724341135038809&lmid=4&ycnam=&pageSize=10&pageIndex=" + i;
            Connection connection = Jsoup.connect( url ).ignoreContentType(true).timeout( 10000 );

            connection.header( "User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:62.0) Gecko/20100101 Firefox/62.0" );
            connection.header( "Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8" );
            connection.header( "Accept-Encoding","gzip,deflate");
            connection.header( "Host","www.yt1998.com" );
            connection.header( "Cookie","Hm_lvt_21f2fde8228a3428719fdc5669ab5410=1535962499,1536638143,1536670986,1536733562; JSESSIONID=6CB4D18DDF345C39661FB7C34D7F67AF; Hm_lpvt_21f2fde8228a3428719fdc5669ab5410=1536736485");

            try {
                Document doc = connection.get();
                String text = doc.body().text().replaceAll( "\n","" ).replaceAll( "\t","" ).replaceAll( "\r","" );
                Map<String,Object> mapper = ( Map< String, Object > ) JacksonMapper.readJsonToObject( text );
                if(mapper != null){
                    List<Map<String,Object>> list = ( List< Map< String, Object > > ) mapper.get( "data" );
                    if(list != null){
                        for(Map map : list){
                            String acid = String.valueOf( map.get( "acid" ) );
                            if(StringUtils.isNotBlank( acid )){
                                String newsUrl = "http://www.yt1998.com/lawMinute--" + acid.trim() + ".html";
                                urlList.add( newsUrl );
                            }
                        }
                    }
                }
            } catch ( IOException e ) {
                log.error( "获取新闻详情异常，异常信息：{}", e.getMessage() );
            }
        }
        log.info( "共解析到新闻数量：{}条" , urlList.size() );
        return urlList;
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static Spider startYTLaw(  ) {
        List<String> urlList = getNewsList();
        Spider spider = Spider.create( new YTLawProcessor() );
        for(String s : urlList){
            spider.addUrl( s );
        }
        spider.thread( 5 ).run();
        return spider;
    }
}
