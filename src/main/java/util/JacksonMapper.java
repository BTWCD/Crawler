package util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Create by Deng Jialong 2017/02/17
 */
public enum JacksonMapper {
    INSTANCE;
    private static final ObjectMapper mapper = new ObjectMapper().configure( JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true );
    private static Logger log = LoggerFactory.getLogger( JacksonMapper.class );

    public static Map<String, ?> readJsonToObject(String jsonData) {
        Map map = new HashMap();
        try {
            map = mapper.readValue(jsonData, Map.class);
        } catch (JsonParseException e) {
            log.info( "JsonParseException!!!" );
            log.error( "解析异常字符串为：{}", jsonData );
            log.error( "json解析异常，信息：{}", e.getMessage() );
        } catch (JsonMappingException e) {
            log.info( "JsonMappingException!!!" );
            log.error( "解析异常字符串为：{}", jsonData );
            log.error( "json解析异常，信息：{}", e.getMessage() );
        } catch (IOException e) {
            log.info( "IOException!!!" );
            log.error( "解析异常字符串为：{}", jsonData );
            log.error( "json解析异常，信息：{}", e.getMessage() );
        }
        return map;
    }

    public static List<?> readJsonToList(String jsonData) {
        List list = new ArrayList<>(  );
        try {
            list = mapper.readValue(jsonData, List.class);
        } catch (JsonParseException e) {
            log.error( "json解析异常，信息：{}", e.getMessage() );
        } catch (JsonMappingException e) {
            log.error( "json解析异常，信息：{}", e.getMessage() );
        } catch (IOException e) {
            log.error( "json解析异常，信息：{}", e.getMessage() );
        }
        return list;
    }

    public static String writeObjectToJson(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error( "json解析异常，信息：{}", e.getMessage() );
            return "{}";
        }
    }

    public static ObjectMapper getMapper() {
//        mapper.setDateFormat( TimeFormatUtils.INSTANCE.getJacksonFormat() );
        return mapper;
    }

}
