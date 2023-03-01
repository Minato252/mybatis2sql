package mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import vo.TempVo;

import java.util.List;
import java.util.Map;

@Mapper
public interface TempMapper {

    public TempVo findTempVoById(@Param("id") Long id,
                                 @Param("name") String name);

    public TempVo findTempVoByObject(TempVo tempVo);

    public TempVo findTempVoByMap(Map tempMap);

    public TempVo findTempVoByList(@Param("list") List list);


}
