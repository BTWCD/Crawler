package util;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlRegexUtil {

    public static boolean isHttpUrl(String url) {
        String regex = "((http[s]{0,1}|ftp)://[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[\\s\\S\\.\\-~!@#$%^&*+?:_/=<>]*)?)|((www.)|[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[\\s\\S\\.\\-~!@#$%^&*+?:_/=<>]*)?)";

        Pattern pat = Pattern.compile(regex.trim());
        Matcher mat = pat.matcher(url.trim());
        boolean isurl = mat.matches();
        if (isurl) {
            isurl = true;
        }
        return isurl;
    }

    public static boolean isStartWithHttp(String url){
        boolean isStartWith = false;
        if( StringUtils.isNotBlank( url )){
            String prefix = url.trim().subSequence( 0,7 ).toString().toLowerCase();

            if(prefix.startsWith( "http://" )){
                isStartWith = true;

            }else{
                isStartWith = false;
            }
        }
        return isStartWith;
    }

    public static boolean isStartWithHttps(String url){
        boolean isStartWith = false;
        if( StringUtils.isNotBlank( url )){
            String prefix = url.trim().subSequence( 0,8 ).toString().toLowerCase();

            if(prefix.startsWith( "https://" )){
                isStartWith = true;
            }else{
                isStartWith = false;
            }
        }
        return isStartWith;
    }

    public static String getDomain(String url){
        String pattern = "^((http://)|(https://))?";

        return StringUtils.isNotBlank( url )?url.replaceAll( pattern, "" ):"";
    }

    public static String getDomainName( String url){
        String pattern = "^((http://)|(https://))?([a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,6}(/)";
        String httpPattern = "^((http://)|(https://))?";
        Pattern pat = Pattern.compile(pattern);
        Matcher mat = pat.matcher(url);

        StringBuffer sb = new StringBuffer( "" );

        if(mat.find()){
            String domain = mat.group().replaceAll( httpPattern, "" ).replaceAll( "/","" );
            String[] strings = domain.split( "\\." );
            for(int i=strings.length-1;i>=0;i--){
                if(!"www".equalsIgnoreCase( strings[i] )){
                    sb.append(strings[i]).append( "/" );
                }
            }
        }

        String picPath = url.replaceAll(pattern, "");
        sb.append( picPath );

        return sb.toString();
    }
}
