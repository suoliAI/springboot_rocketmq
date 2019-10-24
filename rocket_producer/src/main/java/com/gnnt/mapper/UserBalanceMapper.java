package com.gnnt.mapper;

import com.gnnt.model.UserBalance;
import com.gnnt.model.UserBalanceCriteria;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserBalanceMapper {
    long countByExample(UserBalanceCriteria example);

    int deleteByExample(UserBalanceCriteria example);

    int deleteByPrimaryKey(Integer id);

    int insert(UserBalance record);

    int insertSelective(UserBalance record);

    List<UserBalance> selectByExample(UserBalanceCriteria example);

    UserBalance selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") UserBalance record, @Param("example") UserBalanceCriteria example);

    int updateByExample(@Param("record") UserBalance record, @Param("example") UserBalanceCriteria example);

    int updateByPrimaryKeySelective(UserBalance record);

    int updateByPrimaryKey(UserBalance record);
}