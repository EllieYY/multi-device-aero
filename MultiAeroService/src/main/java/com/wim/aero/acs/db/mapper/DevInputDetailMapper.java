package com.wim.aero.acs.db.mapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;

import com.wim.aero.acs.db.entity.DevInputDetail;
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
public interface DevInputDetailMapper extends BaseMapper<DevInputDetail> {
    List<DevInputDetail> selectAllByPDeviceIdList(@Param("list") List<Integer> idList);

    List<DevInputDetail> selectAllByControllerId(@Param("controllerId") Integer controllerId);

    List<DevInputDetail> selectAllByControllerIdAndSioNumber(@Param("controllerId") Integer controllerId,
                                                             @Param("sioNumber") Integer sioNumber);

}
