package com.gnnt.mapper;

import com.gnnt.model.TransactionLog;
import com.gnnt.model.TransactionLogCriteria;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TransactionLogMapper {
    long countByExample(TransactionLogCriteria example);

    int deleteByExample(TransactionLogCriteria example);

    int deleteByPrimaryKey(Integer id);

    int insert(TransactionLog record);

    int insertSelective(TransactionLog record);

    List<TransactionLog> selectByExample(TransactionLogCriteria example);

    TransactionLog selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") TransactionLog record, @Param("example") TransactionLogCriteria example);

    int updateByExample(@Param("record") TransactionLog record, @Param("example") TransactionLogCriteria example);

    int updateByPrimaryKeySelective(TransactionLog record);

    int updateByPrimaryKey(TransactionLog record);
}