package util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ImageUtil {

    private static Logger log = LoggerFactory.getLogger( ImageUtil.class );

    public static void getImage(String imgUrl, String imgStorePath) {
        int num = imgStorePath.lastIndexOf("/");

        if (num == imgStorePath.length()) {
            imgStorePath = imgStorePath.substring(1, num);
        }

        num = imgStorePath.lastIndexOf("/");
        String name = imgStorePath.substring(num, imgStorePath.length());
        String path = imgStorePath.substring( 0, num );

        CloseableHttpClient httpclient = null;
        InputStream in = null;

        try{

            httpclient = HttpClients.createDefault();

            Path target = Paths.get(path);
            if(!Files.isReadable(target)){
                Files.createDirectories( target );
            }

            HttpGet httpget = new HttpGet(imgUrl);
            HttpResponse response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            in = entity.getContent();

            File file = new File(imgStorePath);

            if(!file.exists() ){
                FileOutputStream fos = new FileOutputStream(file);
                int flag = -1;
                byte[] tmp = new byte[1024];
                while ((flag = in.read(tmp)) != -1) {
                    fos.write(tmp,0,flag);
                }
                fos.flush();
                fos.close();
            }
        } catch ( IOException e ) {
            log.error( "下载图片：{}  异常，异常信息：{}", imgUrl, e.getMessage() );
        } finally {
            if(in != null){
                try {
                    in.close();
                } catch ( IOException e ) {
                    log.error( "下载图片IO流关闭异常：{}", e.getMessage() );
                }
            }

            if( httpclient != null){
                try {
                    httpclient.close();
                } catch ( IOException e ) {
                    log.error( "下载图片httpclient关闭异常：{}", e.getMessage() );
                }
            }
        }

    }

}
