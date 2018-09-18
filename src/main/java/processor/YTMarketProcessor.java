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

public class YTMarketProcessor implements PageProcessor {

    private Site site = Site.me().setRetryTimes( 4 ).setRetryTimes( 1000 );
    private static final String MARKETREGEX = "http://www\\.yt1998\\.com/minute--\\d+--\\d+\\.html";
    private static final String DOMAIN = "http://www.yt1998.com";
    private static final String IMGSTOREPATH = "/com/yt1998";
    private static String storePath = PropertiesReader.getPicStorePath();
    private static String serverHost = PropertiesReader.getServerHost();
    private static Logger log = LoggerFactory.getLogger( YTMarketProcessor.class );

    @Override
    public void process( Page page ) {
        if(page.getUrl().get().matches( MARKETREGEX )){
            String title = page.getHtml().xpath( "//h1[@class='mb10']/text()" ).get();
            String author = page.getHtml().getDocument().select("span.ml10:contains(作者：)").text();
            String source = page.getHtml().getDocument().select("span.ml10:contains(来源：)").text();
            String marketName = page.getHtml().getDocument().select("span.ml10:contains(市场：)").text().replaceAll( "市场：", "" );
            String publishTime = page.getHtml().getDocument().select("span.m110:contains(时间：)").text();
            String contentText = page.getHtml().css( "div.f14.lh28.mt20", "allText" ).get();
            String contentCode = page.getHtml().css( "div.f14.lh28.mt20" ).get();

            Elements elements = page.getHtml().getDocument().select( "div.f14.lh28.mt20")
                    .get( 0 ).getElementsByTag( "img" );
            for(Element e : elements){
                String url = e.attr( "src" );
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
            bean.setNewsType( "天天行情-" + marketName );
            bean.setSourceName( "中药材药通网" );
            bean.setFetchDate( TimeFormatUtils.INSTANCE.formatFetchTime( new Date().getTime() ) );

            System.out.println("!=================");
            System.out.println(bean);
            System.out.println("!!=================");

//            List<String> newsUrl = page.getHtml().xpath( "//div[@class='m-page']" ).links().all();
//            for(String s : newsUrl){
//                if( StringUtils.isNotBlank( s ) && s.matches( MARKETREGEX )){
//                    System.out.println(s);
//                    page.addTargetRequest( s );
//                }
//            }
            NewsDao.insertNews( bean );
        }

    }

    @Override
    public Site getSite() {
        return site;
    }

    private static int getNewsUrlTotal(String url){
        Connection connection = Jsoup.connect( url ).ignoreContentType(true).timeout( 50000 );

        connection.header( "User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:62.0) Gecko/20100101 Firefox/62.0" );
        connection.header( "Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8" );
        connection.header( "Accept-Encoding","gzip,deflate");
        connection.header( "Host","www.yt1998.com" );
        connection.header( "Cookie","Hm_lvt_21f2fde8228a3428719fdc5669ab5410=1535962499,1536638143,1536670986,1536733562; JSESSIONID=6CB4D18DDF345C39661FB7C34D7F67AF; Hm_lpvt_21f2fde8228a3428719fdc5669ab5410=1536736485");

        try {
            Document doc = connection.get();
            Map<String,Object> mapper = ( Map< String, Object > ) JacksonMapper.INSTANCE.readJsonToObject( doc.body().text() );
            int total = Integer.parseInt(String.valueOf(mapper.get( "total" )));
            return total;
        } catch ( IOException e ) {
            log.error( "获取市场快讯总数异常，异常信息：{}", e.getMessage() );
            return 0;
        }
    }

    private static List<String> getNewsList(String url, String urlPattern, String newsPattern){
        int total = getNewsUrlTotal(url);
        List<String> urlList = new LinkedList<>(  );
        log.info( "共有市场快讯：{}条" ,total );
        int page = total/50;
        for(int i=0;i<=page;i++){
            String listUrl = urlPattern + i;
            Connection connection = Jsoup.connect( listUrl ).ignoreContentType(true).timeout( 10000 );

            connection.header( "User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:62.0) Gecko/20100101 Firefox/62.0" );
            connection.header( "Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8" );
            connection.header( "Accept-Encoding","gzip,deflate");
            connection.header( "Host","www.yt1998.com" );
            connection.header( "Cookie","Hm_lvt_21f2fde8228a3428719fdc5669ab5410=1535962499,1536638143,1536670986,1536733562; JSESSIONID=6CB4D18DDF345C39661FB7C34D7F67AF; Hm_lpvt_21f2fde8228a3428719fdc5669ab5410=1536736485");

            try {
                Document doc = connection.get();
                String text = doc.body().text().replaceAll( "\r","" ).replaceAll( "\t","" ).replaceAll( "\n","" );
                Map<String,Object> mapper = ( Map< String, Object > ) JacksonMapper.readJsonToObject( text );
                if(mapper != null){
                    List<Map<String,Object>> list = ( List< Map< String, Object > > ) mapper.get( "data" );
                    if(list != null){
                        for(Map map : list){
                            String acid = String.valueOf( map.get( "acid" ) );
                            if(StringUtils.isNotBlank( acid )){
                                String newsUrl = newsPattern.replaceAll( "xxx", acid );
                                urlList.add( newsUrl );
                            }
                        }
                    }
                }
            } catch ( IOException e ) {
                log.error( "获取市场快讯异常，异常信息：{}", e.getMessage() );
            }
        }
        log.info( "共解析到市场快讯数量：{}条" , urlList.size() );
        return urlList;
    }

    public static Spider startYTMarket( ) {
        String BoZhouUrl = "http://www.yt1998.com/ytw/second/marketMgr/query.jsp?random=0.07414794749716991&scid=1&lmid=3&ycnam=&times=1&pageSize=1&pageIndex=0";
        String AnGuoUrl = "http://www.yt1998.com/ytw/second/marketMgr/query.jsp?random=0.9289503823316974&scid=2&lmid=3&ycnam=&times=2&pageSize=1&pageIndex=0";
        String ChengDuUrl = "http://www.yt1998.com/ytw/second/marketMgr/query.jsp?random=0.8966319479536038&scid=4&lmid=3&ycnam=&times=3&pageSize=1&pageIndex=0";
        String YuLinUrl = "http://www.yt1998.com/ytw/second/marketMgr/query.jsp?random=0.5437516350475065&scid=3&lmid=3&ycnam=&times=4&pageSize=1&pageIndex=0";

        String BoZhouPage = "http://www.yt1998.com/ytw/second/marketMgr/query.jsp?random=0.07414794749716991&scid=1&lmid=3&ycnam=&times=1&pageSize=50&pageIndex=";
        String AnGuoPage = "http://www.yt1998.com/ytw/second/marketMgr/query.jsp?random=0.9289503823316974&scid=2&lmid=3&ycnam=&times=2&pageSize=50&pageIndex=";
        String ChengDuPage = "http://www.yt1998.com/ytw/second/marketMgr/query.jsp?random=0.8966319479536038&scid=4&lmid=3&ycnam=&times=3&pageSize=50&pageIndex=";
        String YuLinPage = "http://www.yt1998.com/ytw/second/marketMgr/query.jsp?random=0.5437516350475065&scid=3&lmid=3&ycnam=&times=4&pageSize=50&pageIndex=";

        String BoZhouNews = "http://www.yt1998.com/minute--xxx--1.html";
        String AnGuoNews = "http://www.yt1998.com/minute--xxx--2.html";
        String ChengDuNews = "http://www.yt1998.com/minute--xxx--4.html";
        String YuLinNews = "http://www.yt1998.com/minute--xxx--3.html";

        List<String> BoZhouList = getNewsList( BoZhouUrl, BoZhouPage, BoZhouNews );
        List<String> AnGuoList = getNewsList( AnGuoUrl, AnGuoPage, AnGuoNews );
        List<String> ChengDuList = getNewsList( ChengDuUrl, ChengDuPage, ChengDuNews );
        List<String> YuLinList = getNewsList( YuLinUrl, YuLinPage, YuLinNews );

        Spider spider = Spider.create( new YTMarketProcessor() );

        for(String s : BoZhouList){
            spider.addUrl( s );
        }

        for(String s : AnGuoList){
            spider.addUrl( s );
        }

        for(String s : ChengDuList){
            spider.addUrl( s );
        }

        for(String s : YuLinList){
            spider.addUrl( s );
        }

        spider.thread( 4 ).run();

        return spider;
    }
}
