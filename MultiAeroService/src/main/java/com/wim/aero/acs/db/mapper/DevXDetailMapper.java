package com.wim.aero.acs.db.mapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;

import com.wim.aero.acs.db.entity.DevXDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Ellie
 * @since 2022-03-22
 */
@Mapper
public interface DevXDetailMapper extends BaseMapper<DevXDetail> {
    List<DevXDetail> selectAllByControllerId(@Param("controllerId") Integer controllerId);

    List<Integer> selectControllerPortByControllerIdAndModel(@Param("controllerId") Integer controllerId,
                                                             @Param("list") List<Integer> modelList);

    Integer selectModelByControllerIdAndSioNumber(@Param("controllerId") Integer controllerId,
                                                  @Param("sioNumber") Integer sioNumber);
}
