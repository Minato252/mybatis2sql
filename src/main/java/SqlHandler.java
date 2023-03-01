import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p> 动态代理handler，直接打印sql句子 </p>
 * @author Minato
 * @data 2023/3/1 13:48
 */


public class SqlHandler implements InvocationHandler {


    /**
     * 配置类
     */
    private Configuration configuration;

    public SqlHandler(String resource) throws IOException {
        configuration = new Configuration();
        try (InputStream inputStream = Resources.getResourceAsStream(resource)) {
            XMLMapperBuilder builder = new XMLMapperBuilder(inputStream, configuration, resource, configuration.getSqlFragments());
            builder.parse();
        }finally {

        }
    }

    /**
     * <p>handler的invoke方法，执行生成sql的逻辑</p>
     * @param proxy 代理对象
     * @param method 代理方法
     * @param args 代理方法的入参
     * @return null防止强转不兼容
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MappedStatement mappedStatement = configuration.getMappedStatement(method.getName());
        Map<String,Object> map = new HashMap<>();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (int i = 0;i<parameterAnnotations.length;i++){
            Object object = args[i];
            if (parameterAnnotations[i].length == 0){ //说明该参数没有注解，此时该参数可能是实体类，也可能是Map，也可能只是单参数
                if (object.getClass().getClassLoader() == null && object instanceof Map){
                    map.putAll((Map<? extends String, ?>) object);
                    System.out.println("该对象为Map");
                }else{//形参为自定义实体类
                    map.putAll(objectToMap(object));
                    System.out.println("该对象为用户自定义的对象");
                }
            }else{//说明该参数有注解，且必须为@Param
                for (Annotation annotation : parameterAnnotations[i]){
                    if (annotation instanceof Param){
                        map.put(((Param) annotation).value(),object);
                    }
                }
            }
        }


        BoundSql boundSql = mappedStatement.getBoundSql(map);
        //处理动态参数#{param}
        String sql = dynamicSqlHandler(boundSql);

        System.out.println(sql);
        return null;
    }




    private static Map<String, Object> objectToMap(Object obj) throws IllegalAccessException {
        Map<String, Object> map = new HashMap<>();
        Class<?> clazz = obj.getClass();
        System.out.println(clazz);
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            String fieldName = field.getName();
            Object value = field.get(obj);
            map.put(fieldName, value);
        }
        return map;
    }

    private static String getParameterValue(Object param) {
        if (param == null) {
            return "null";
        }
        if (param instanceof Number) {
            return param.toString();
        }
        String value = null;
        if (param instanceof String) {
            value = "'"+param.toString()+"'";;
        } else if (param instanceof Date) {
            value = ((Date)param).getTime()+"";
        } else if (param instanceof Enum) {
            value = "'"+((Enum<?>) param).name()+"'";
        } else {
            value = param.toString();
        }
        return value;
    }

    /**
     * 处理动态sql中的占位符?
     * @param boundSql
     */
    private String dynamicSqlHandler(BoundSql boundSql){
        String sql=boundSql.getSql();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        Object parameterObject = boundSql.getParameterObject();
        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        if (parameterMappings != null){
            String parameter = "null";
            String propertyName;
            MetaObject newMetaObject = configuration.newMetaObject(parameterObject);
            for (ParameterMapping parameterMapping : parameterMappings) {
                if (parameterMapping.getMode() == ParameterMode.OUT) {
                    continue;
                }
                propertyName = parameterMapping.getProperty();
                if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                    parameter = getParameterValue(parameterObject);
                } else if (newMetaObject.hasGetter(propertyName)) {
                    parameter = getParameterValue(newMetaObject.getValue(propertyName));
                } else if (boundSql.hasAdditionalParameter(propertyName)) {
                    parameter = getParameterValue(boundSql.getAdditionalParameter(propertyName));
                }
                sql = sql.replaceFirst("\\?", parameter);
            }
        }
        return sql;
    }



}
