import db.DbPoolConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import processor.*;
import us.codecraft.webmagic.Spider;
import util.PropertiesReader;

import java.util.concurrent.TimeUnit;

public class CrawlerMain {

    private static Logger log = LoggerFactory.getLogger( CrawlerMain.class );

    static{
        DbPoolConnection.INSTANCE.init();
    }

    public static void main( String[] args ) {
        String crawlerName = PropertiesReader.getCrawlerName();
        log.info( "启动爬虫名字为：{}", crawlerName );
        Spider spider = startCrawlerByName( crawlerName );
        if(spider != null){
            boolean running = true;
            while(running){
                try {
                    TimeUnit.MINUTES.sleep( 1 );
                } catch ( InterruptedException e ) {
                    e.printStackTrace();
                }
                log.info( "爬虫状态为：{}", spider.getStatus().toString() );
                if( "Stopped".equalsIgnoreCase( spider.getStatus().toString() )){
                    running = false;
                }
            }
            log.info( "爬虫停止，即将退出程序" );
            System.exit( 1 );
        }else{
            log.info( "爬虫启动出现未知异常，即将退出程序" );
            System.exit( 1 );
        }
    }

    private static Spider startCrawlerByName( String crawlerName ) {
        Spider spider = null;

        switch ( crawlerName ){
            case "qnong":
                spider = Spider.create( new QNongProcessor() );
                spider.addUrl( "http://www.qnong.com.cn/jiankang/yaoshan/index.html" )
                      .addUrl( "http://www.qnong.com.cn/zhongzhi/yaocai/index.html" )
                      .thread( 3 ).run();
                break;

            case "zyczyc":
                spider = Spider.create( new ZyczycProcessor() );
                spider.addUrl( "http://www.zyczyc.com/info/WenZhang.aspx?lmid=11&p=1" ).thread( 5 ).run();
                break;

            case "hunaas":
                spider = Spider.create(new HunaasNewsProcessor());
                spider.addUrl( "http://www.hunaas.cn/news.asp?cid=29" )
                      .addUrl( "http://www.hunaas.cn/news.asp?cid=2" )
                      .addUrl( "http://www.hunaas.cn/news.asp?cid=79" )
                      .addUrl( "http://www.hunaas.cn/news.asp?cid=201" )
                      .addUrl( "http://www.hunaas.cn/news.asp?cid=202" )
                      .thread( 5 )
                      .run();
                break;

            case "yt1998Law":
                spider = YTLawProcessor.startYTLaw();
                break;

            case "yt1998Market":
                spider = YTMarketProcessor.startYTMarket();
                break;

            default:
                log.info( "无指定域名网站爬虫，爬虫程序停止" );
                System.exit( 1 );
                break;
        }
        return spider;
    }
}
