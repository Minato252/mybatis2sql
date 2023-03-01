import java.io.IOException;


/**
 * <p> 读取mapper下的java和xml 生成动态代理对象 </p>
 *
 * @param <T> Mapper接口类型
 * @author Minato
 * @data 2023/3/1 13:43
 */
public class Mybatis2Sql<T> {

    private String simpleName;
    private Class<T> clz;

    /**
     * <p>构造函数 传入的clz和泛型保持一致</p>
     *
     * @param clz
     */
    Mybatis2Sql(Class<T> clz) {
        this.clz = clz;
        this.simpleName = clz.getSimpleName();
    }

    /**
     * 根据默认地址(resources下)获取xml 生成动态代理对象
     *
     * @return 动态代理对象
     */
    public T getProxy() {
        String xmlPath = "mapper\\" + simpleName + ".xml";
        try {
            T mapper = (T) MapperProxyFactory.getMapper(clz,
                    xmlPath);
            return mapper;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }
}
