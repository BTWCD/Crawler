package db;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.pool.DruidPooledConnection;
import org.apache.commons.dbutils.QueryRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.PropertiesReader;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 创建Druid数据库连接池
 * 使用枚举类型实现单例模式
 * Created by Deng Jialong on 2017/2/25.
 */
public enum DbPoolConnection {
    // 单例模式实例
    INSTANCE;

    private static DruidDataSource dds = null;
    private static QueryRunner queryRunner = null;
    private static String propertyPath = PropertiesReader.getDBConfigFile();
    private static Logger logger = LoggerFactory.getLogger( DbPoolConnection.class );

    public void init() {
        Properties properties = new Properties();
        try {
            properties.load( Files.newInputStream( Paths.get( propertyPath ) ) );
            dds = ( DruidDataSource ) DruidDataSourceFactory.createDataSource( properties );
            queryRunner = new QueryRunner( dds );
        } catch ( Exception e ) {
            logger.error( e.getMessage(), e );
        }
    }

    public DruidDataSource getDataSource() {
        return dds;
    }

    public QueryRunner getQueryRunner() {
        return queryRunner;
    }

    public DruidPooledConnection getConnection() {
        try {

            return dds.getConnection();
        } catch ( SQLException e ) {
            logger.error( e.getMessage(), e );
            return null;
        }
    }

    public void close() {
        dds.close();
    }
}
