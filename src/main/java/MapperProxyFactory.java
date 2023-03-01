import java.io.IOException;
import java.lang.reflect.Proxy;

/**
 * <p> 代理工厂类 </p>
 *
 * @author Minato
 * @data 2023/3/1 13:47
 */
public class MapperProxyFactory {
    /**
     * 获取Mapper接口实例（代理对象）
     *
     * @param mapperClass Mapper接口类
     * @param resource    xml文件路径
     * @return 接口实例
     * @throws IOException
     */
    public static <T> T getMapper(Class<T> mapperClass, String resource) throws IOException {
        T proxy = (T) Proxy.newProxyInstance(mapperClass.getClassLoader(),
                new Class[]{mapperClass}, new SqlHandler(resource));
        return proxy;
    }
}