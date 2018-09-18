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

public class QNongProcessor implements PageProcessor {

    private Site site = Site.me().setRetryTimes( 3 ).setSleepTime( 1000 );

    private static final String NEWSPAGEREGEX = "http://www\\.qnong\\.com\\.cn/jiankang/yaoshan/\\d+\\.html";
    private static final String PLANTNEWS = "http://www\\.qnong\\.com\\.cn/zhongzhi/yaocai/\\d+\\.html";
    private static final String HEALTHLIST = "http://www\\.qnong\\.com\\.cn/jiankang/yaoshan/index_*\\d*\\.html";
    private static final String PLANTLIST = "http://www\\.qnong\\.com\\.cn/zhongzhi/yaocai/index_*\\d*\\.html";
    private static final String DOMAIN = "http://www.qnong.com.cn";
    private static final String IMGSTOREPATH = "cn/com/qnong";
    private static String storePath = PropertiesReader.getPicStorePath();
    private static String serverHost = PropertiesReader.getServerHost();

    @Override
    public void process( Page page ) {

        if(page.getUrl().get().matches( HEALTHLIST )){
            List<String> list = page.getHtml().xpath( "//ul[@class='list']/li" ).links().all();
            for(String s : list){
                System.out.println(s);
                if(s.matches( NEWSPAGEREGEX )){
                    page.addTargetRequest( s );
                }
            }

            List<String> pageList = page.getHtml().xpath( "//div[@id='pages']/a" ).links().all();
            for(String s : pageList){
                System.out.println(s);
                if(s.matches( HEALTHLIST )){
                    page.addTargetRequest( s );
                }
            }

        }else if(page.getUrl().get().matches( PLANTLIST )){

            List<String> list = page.getHtml().xpath( "//ul[@class='list']/li" ).links().all();
            for(String s : list){
                System.out.println(s);
                if(s.matches( PLANTNEWS )){
                    page.addTargetRequest( s );
                }
            }

            List<String> pageList = page.getHtml().xpath( "//div[@id='pages']/a" ).links().all();
            for(String s : pageList){
                System.out.println(s);
                if(s.matches( PLANTLIST )){
                    page.addTargetRequest( s );
                }
            }
        }else if(page.getUrl().get().matches( NEWSPAGEREGEX )){
            String title = page.getHtml().xpath( "//div[@class='showoftitle']/h1/text()" ).get();
            String subInfo = page.getHtml().xpath( "//div[@class='showoftime']/text()" ).get();
            String titleAndTime = subInfo.split( "标签" )[0].trim();
            String publishTime = titleAndTime.split( "来源" )[0].trim();
            String author = titleAndTime.split( "来源：" )[1].trim();
            String contentText = page.getHtml().css( ".showofcontent_jiankang", "allText" ).get();
            String contentCode = page.getHtml().css( ".showofcontent_jiankang" ).get();

            List<String> imgUrls = page.getHtml().xpath( "//div[@class='showofcontent_jiankang']/p/img/@src" ).all();
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
            bean.setNewsType( "农人健康" );
            bean.setSourceName( "黔农网" );
            bean.setFetchDate( TimeFormatUtils.INSTANCE.formatFetchTime( new Date().getTime() ) );

            NewsDao.insertNews( bean );

        }else if(page.getUrl().get().matches( PLANTNEWS )){
            String title = page.getHtml().xpath( "//div[@class='showoftitle']/h1/text()" ).get();
            String subInfo = page.getHtml().xpath( "//div[@class='showoftime']/text()" ).get();
            String titleAndTime = subInfo.split( "标签" )[0].trim();
            String publishTime = titleAndTime.split( "来源" )[0].trim();
            String author = titleAndTime.split( "来源：" )[1].trim();
            String contentText = page.getHtml().css( ".showofcontent", "allText" ).get();
            String contentCode = page.getHtml().css( ".showofcontent" ).get();

            List<String> imgUrls = page.getHtml().xpath( "//div[@class='showofcontent']/p/img/@src" ).all();
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
            bean.setNewsType( "种植技术" );
            bean.setSourceName( "黔农网" );
            bean.setFetchDate( TimeFormatUtils.INSTANCE.formatFetchTime( new Date().getTime() ) );

            NewsDao.insertNews( bean );

        }

    }

    @Override
    public Site getSite() {
        return site;
    }

}
