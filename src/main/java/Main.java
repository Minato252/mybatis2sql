import mapper.TempMapper;
import vo.TempVo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p> example </p>
 *
 * @author Minato
 * @data 2023/3/1 13:46
 */
public class Main {
    public static void main(String[] args) {

        //使用接口生成Mybatis2Sql对象
        Mybatis2Sql<TempMapper> mybatis2Sql =
                new Mybatis2Sql<>(TempMapper.class);

        //生成代理对象调用方法
        //Param
        mybatis2Sql.getProxy().findTempVoById(123L, "name");

        //Object
        TempVo tempVo = new TempVo();
        tempVo.setId(123L);
        tempVo.setName("name");
        mybatis2Sql.getProxy().findTempVoByObject(tempVo);

        //Map
        Map<String, Object> tempMap = new HashMap<>();
        tempMap.put("id", 123L);
        tempMap.put("name", "name");
        mybatis2Sql.getProxy().findTempVoByObject(tempVo);

        //List
        List<String> list = new ArrayList<>(2);
        list.add("123");
        list.add("345");
        mybatis2Sql.getProxy().findTempVoByList(list);
    }
}
