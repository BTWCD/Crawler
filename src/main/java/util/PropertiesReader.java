package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static common.DefaultValueConstants.*;
import static common.PropConstants.*;

public class PropertiesReader {
    private final static ExecutorService fixedThreadPool = Executors.newSingleThreadExecutor();
    private final static Properties props = new Properties();
    private static Logger logger = LoggerFactory.getLogger( PropertiesReader.class );
    private static Path filePath;

    static {
        Path path = Paths.get( "./" );

        try ( DirectoryStream< Path > directoryStream = Files.newDirectoryStream( path, "crawler.properties" ) ) {
            for ( Path aDirectoryStream : directoryStream ) {
                String fileName = aDirectoryStream.getFileName().toString();
                filePath = path.resolve( fileName );
                if ( filePath.toFile().isFile() ) {
                    logger.trace( "读取配置文件:" + filePath.toFile().getCanonicalPath() );
                    if ( Files.exists( filePath, LinkOption.NOFOLLOW_LINKS ) ) {
                        props.load( Files.newInputStream( filePath ) );
                    }
                    if ( props.isEmpty() ) {
                        logger.warn( "", filePath.toFile().getCanonicalPath() );
                    }
                } else {
                    logger.warn( "", path.toFile().getCanonicalPath() );
                }
            }

        } catch ( IOException e ) {
            logger.error( e.getMessage(), e, filePath );
        }
        fixedThreadPool.execute( new FileListener( path, props ) );
    }

    private static String readValue( String key, String defaultValue ) {
        String value = props.getProperty( key, defaultValue );
        logger.trace( "读取配置:" + key + "=" + value );
        if ( value == null || value.isEmpty() ) {
            logger.warn( "{},{}", key, value );
            value = defaultValue;
        }
        return value;
    }

    private static boolean isInteger( String value ) {
        try {
            Integer.parseInt( value );
        } catch ( Exception e ) {
            return false;
        }
        return true;
    }

    private static boolean isLegalFile( Path path ) {
        return Files.isRegularFile( path ) & Files.isReadable( path ) &
                Files.isExecutable( path ) & Files.isWritable( path );
    }

    public static Path getFilePath() {
        return filePath;
    }

    public static String getDBConfigFile() {
        String dbConfigPath = readValue( DB_CONFIG_PATH, DB_CONFIG_PATH_DEFAULT );
        return validateDBConfigFile( dbConfigPath );
    }

    private static String validateDBConfigFile( String dbConfigPath ) {
        Path path = Paths.get( dbConfigPath );
        if ( !isLegalFile( path ) || !path.toFile().exists() ) {
            logger.warn( dbConfigPath, DB_CONFIG_PATH_DEFAULT );
            dbConfigPath = DB_CONFIG_PATH_DEFAULT;
        }

        return dbConfigPath;
    }

    public static String getCrawlerName(){
        String crawlerName = readValue( CRAWLER_NAME, CRAWLER_NAME_DEFAULT );
        return crawlerName;
    }

    public static String getPicStorePath(){
        String picStorePath = readValue( PIC_STORE_PATH, PIC_STORE_PATH_DEAFAULT );
        return picStorePath;
    }

    public static String getServerHost(){
        String serverHost = readValue( SERVER_HOST_ADDRESS, SERVER_HOST_ADDRESS_DEAFAULT );
        return serverHost;
    }

}

