# Mybatis2Sql Tool
- 无需连接数据库，通过动态代理，将动态sql转换为静态sql语句。
- 可以根据输入的参数自动生成对应的sql。
## 使用方式
文件结构
```
├─src
│  ├─main
│  │  ├─java
│  │  │  ├─mapper
│  │  │  └─vo
│  │  └─resources
│  │      └─mapper
...
```
将`Mapper.java`文件放入`src\main\java\mapper`

将`Mapper.xml`文件放入`src\main\resources\mapper`

更改`src\main\java\Main.java`中代码即可在控制台中打印sql
```java
import mapper.TempMapper;

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
```

## 注意事项
- 可以自行生成Vo满足输入条件
- Mapper的入参支持Object、Map、@Param
- Number的实现类、String、List类型等需要手动添加@Param注解
- slfj的报错不妨碍使用，无视即可