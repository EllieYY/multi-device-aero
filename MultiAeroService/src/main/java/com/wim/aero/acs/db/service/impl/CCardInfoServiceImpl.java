package com.wim.aero.acs.db.service.impl;

import com.wim.aero.acs.db.entity.CCardInfo;
import com.wim.aero.acs.db.mapper.CCardInfoMapper;
import com.wim.aero.acs.db.service.CCardInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wim.aero.acs.protocol.card.CardAdd;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Ellie
 * @since 2022-03-22
 */
@Service
public class CCardInfoServiceImpl extends ServiceImpl<CCardInfoMapper, CCardInfo> implements CCardInfoService {
//    public List<CardAdd> getByCardNo(List<String> cardNoList) {
//        return this.baseMapper.selectAllByCardNo(cardNoList);
//    }

//    public List<CardAdd> getByAccessLevels(List<Integer> alList) {
//        return this.baseMapper.selectAllByAccessLevels(alList);
//    }

    /**
     * 通过控制器id找卡 -- 普通控制器
     * @param scpId
     * @return
     */
    public List<CardAdd> getByScpId(int scpId) {
        return this.baseMapper.selectAllByScpId(scpId);
    }

    /**
     * 通过控制器id找卡 -- 电梯控制器
     * @param scpId
     * @return
     */
    public List<CardAdd> getByEleScpId(int scpId) {
        return this.baseMapper.selectAllByEleScpId(scpId);
    }

    /**
     * 通过卡号找卡的权限信息 -- 普通控制器
     */
    public List<CardAdd> getByCardList(List<String> cardList) throws DataAccessException {
        return this.baseMapper.selectAllByCardList(cardList);
    }

    /**
     * 通过卡号找卡的权限信息 -- 电梯控制器
     * @param cardList
     * @return
     */
    public List<CardAdd> getByCardListForEleScp(List<String> cardList) throws DataAccessException {
        return this.baseMapper.selectAllByCardListForEle(cardList);
    }


    /**
     * 通过卡号找卡的权限信息 -- 普通控制器
     */
    public List<CardAdd> getByCardListAndScpId(int scpId, List<String> cardList) throws DataAccessException {
        return this.baseMapper.selectAllByScpAndCardList(scpId, cardList);
    }

    /**
     * 通过卡号找卡的权限信息 -- 电梯控制器
     * @param cardList
     * @return
     */
    public List<CardAdd> getByCardListAndScpIdForEleScp(int scpId, List<String> cardList) throws DataAccessException {
        return this.baseMapper.selectAllByScpAndCardListForEle(scpId, cardList);
    }

    public List<Integer> getScpIdsByCardNo(List<String> cardList) {
        return this.baseMapper.selectScpIdsByCardNo(cardList);
    }

    public List<Integer> getScpIdsByCardNoForEle(List<String> cardList) {
        return this.baseMapper.selectScpIdsByCardNoForEle(cardList);
    }

}
