package com.wim.aero.acs.db.mapper;
import java.util.List;

import com.wim.aero.acs.db.entity.DevInputDetail;
import com.wim.aero.acs.model.db.AcrStrikeInfo;
import org.apache.ibatis.annotations.Param;

import com.wim.aero.acs.db.entity.DevReaderDetail;
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
public interface DevReaderDetailMapper extends BaseMapper<DevReaderDetail> {
    List<DevReaderDetail> selectAllByPDeviceId(@Param("list") List<Integer> ids);

    List<DevReaderDetail> selectAllByControllerId(@Param("controllerId") Integer controllerId);

    List<DevReaderDetail> selectAllByControllerIdAndSioNumber(@Param("controllerId") Integer controllerId,
                                                              @Param("sioNumber") Integer sioNumber);

    AcrStrikeInfo selectStrikeByDeviceId(@Param("deviceId") Integer deviceId);

    int CountByInputInfo(@Param("scpId") Integer controllerId,
                         @Param("sioId") Integer sioId,
                         @Param("inputNo") Integer inputNo);

    int CountByOutputInfo(@Param("scpId") Integer controllerId,
                             @Param("sioId") Integer sioId,
                             @Param("outputNo") Integer outputNo);
}
