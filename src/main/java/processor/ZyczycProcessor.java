package processor;

import bean.NewsBean;
import dao.NewsDao;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import util.ImageUtil;
import util.PropertiesReader;
import util.TimeFormatUtils;
import util.UrlRegexUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ZyczycProcessor implements PageProcessor{

    private Site site = Site.me().setSleepTime( 1000 ).setRetryTimes( 4 );
    private static final String LISTREGEX = "http://www\\.zyczyc\\.com/info/WenZhang\\.aspx\\?lmid=11&p=\\d+";
    private static final String DETAILREGEX = "http://www\\.zyczyc\\.com/info/Content\\.aspx\\?lmid=11&acid=\\d+&shopid=-1";
    private static final String DOMAIN = "http://www.zyczyc.com";
    private static final String IMGSTOREPATH = "/com/zyczyc";
    private static String storePath = PropertiesReader.getPicStorePath();
    private static String serverHost = PropertiesReader.getServerHost();

    @Override
    public void process( Page page ) {
        if(page.getUrl().get().matches( LISTREGEX )){
            List<String> newsList = page.getHtml().xpath( "//a[@class='lvse11ptcu']" ).links().all();
            page.addTargetRequests( newsList );

            List<String> pageList = page.getHtml().xpath( "//a[@class='lvse10pt']" ).links().all();
            //page.addTargetRequests( pageList );
        }else if(page.getUrl().get().matches( DETAILREGEX )){
            String title = page.getHtml().xpath( "//span[@id='ContentPlaceHolder1_LabelTitle']/text()" ).get();
            String publishTime = page.getHtml().xpath( "//span[@id='ContentPlaceHolder1_LabelDate']/text()" ).get();
            String author = page.getHtml().xpath( "//span[@id='ContentPlaceHolder1_LabelWriter']/text()" ).get();
            String contentCode = page.getHtml().css( "td.huisebiankuang:nth-child(1)> table:nth-child(1) > tbody:nth-child(1) > tr:nth-child(2) > td:nth-child(1) > table:nth-child(1) > tbody:nth-child(1)" ).get();
            String contentText = page.getHtml().css( "td.huisebiankuang:nth-child(1)> table:nth-child(1) > tbody:nth-child(1) > tr:nth-child(2) > td:nth-child(1) > table:nth-child(1) > tbody:nth-child(1)", "allText" ).get();

            Elements elements = page.getHtml().getDocument().select( "td.huisebiankuang:nth-child(1)> table:nth-child(1) > tbody:nth-child(1) > tr:nth-child(2) > td:nth-child(1) > table:nth-child(1) > tbody:nth-child(1)")
                    .get( 0 ).getElementsByTag( "img" );
            for(Element e : elements){
                String url = e.attr( "src" );
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
                    imgStorePath = imgStorePath.append(storePath).append(IMGSTOREPATH).append(localPath);
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
            bean.setNewsType( "行业新闻" );
            bean.setSourceName( "东方中药材网" );
            bean.setFetchDate( TimeFormatUtils.INSTANCE.formatFetchTime( new Date().getTime() ) );

            System.out.println("!=================");
            System.out.println(bean);
            System.out.println("!!=================");

            NewsDao.insertNews( bean );
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

}
