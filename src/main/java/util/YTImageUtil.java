package util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class YTImageUtil {

    public static void getYaoTongImage(String imgUrl, String imgStorePath, String referPath) {

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
            httpget.setHeader( "Referer",referPath );
            httpget.setHeader( "Cookie","Hm_lvt_21f2fde8228a3428719fdc5669ab5410=1535962499,1536638143; Hm_lpvt_21f2fde8228a3428719fdc5669ab5410=1536660151" );
            httpget.setHeader( "Host","info.img.yt1998.com" );
            httpget.setHeader( "User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:62.0) Gecko/20100101 Firefox/62.0" );
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
            e.printStackTrace();
        } finally {
            if(in != null){
                try {
                    in.close();
                } catch ( IOException e ) {
                    e.printStackTrace();
                }
            }

            if( httpclient != null){
                try {
                    httpclient.close();
                } catch ( IOException e ) {
                    e.printStackTrace();
                }
            }
        }

    }
}
