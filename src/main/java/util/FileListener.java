package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Properties;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Created by 邓嘉龙 on 2017/4/27.
 */
public class FileListener implements Runnable {
    private static Logger logger = LoggerFactory.getLogger( FileListener.class );

    private Path rootPath;
    private Properties props;

    public FileListener( Path path, Properties props ) {
        this.rootPath = path;
        this.props = props;
    }

    public void run() {
        try ( WatchService service = FileSystems.getDefault().newWatchService() ) {
            rootPath.register( service, ENTRY_CREATE, ENTRY_MODIFY );
            while ( true ) {
                WatchKey watchKey = service.take();
                List< WatchEvent< ? > > watchEvents = watchKey.pollEvents();
                for ( WatchEvent< ? > event : watchEvents ) {
                    if ( event.kind() == OVERFLOW ) {// event lost or discarded
                        continue;
                    }
                    Path filePath = rootPath.resolve( event.context().toString() );
                    if ( filePath.endsWith( ".properties" ) ) {
                        if ( event.kind() == ENTRY_CREATE || event.kind() == ENTRY_MODIFY ) {
                            props.load( Files.newInputStream( filePath ) );
                        }
                    }
                }
                watchKey.reset();
            }
        } catch ( InterruptedException | IOException e ) {
            logger.error( e.getMessage(), e );
        }
    }
}
