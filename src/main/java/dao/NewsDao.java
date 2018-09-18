package dao;

import bean.NewsBean;
import com.alibaba.druid.pool.DruidPooledConnection;
import db.DbPoolConnection;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NewsDao {

    public static void insertNews( NewsBean bean){
        String sql = "insert into herb_news (source_url,news_type,title,author,publish_time,content_text,content_code,source_name,fetch_date)" +
                " values (?,?,?,?,?,?,?,?,?)";
        try( DruidPooledConnection connection = DbPoolConnection.INSTANCE.getConnection();
             PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString( 1, bean.getSourceUrl() );
            pst.setString( 2, bean.getNewsType() );
            pst.setString( 3, bean.getTitle() );
            pst.setString( 4, bean.getAuthor() );
            pst.setString( 5, bean.getPublishTime() );
            pst.setString( 6, bean.getContentText() );
            pst.setString( 7, bean.getContentCode() );
            pst.setString( 8, bean.getSourceName() );
            pst.setString( 9, bean.getFetchDate() );
            pst.execute();

        } catch ( SQLException e ) {
            e.printStackTrace();
        }
    }
}
