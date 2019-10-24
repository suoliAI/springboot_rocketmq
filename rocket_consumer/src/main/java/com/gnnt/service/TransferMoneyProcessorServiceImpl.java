package com.gnnt.service;

import com.gnnt.mapper.TransactionLogMapper;
import com.gnnt.mapper.UserBalanceMapper;
import com.gnnt.model.TransactionLog;
import com.gnnt.model.TransactionLogCriteria;
import com.gnnt.model.UserBalance;
import com.gnnt.model.UserBalanceCriteria;
import com.gnnt.vo.TransferEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class TransferMoneyProcessorServiceImpl implements TransferMoneyProcessorService {
    @Autowired
    private UserBalanceMapper userBalanceMapper;
    @Autowired
    private TransactionLogMapper transactionLogMapper;

    @Transactional(rollbackFor = {Exception.class})
    public boolean transferAdd(TransferEntity transferEntity) {

        log.info("TransferMoneyProcessorServiceImpl transferAdd is executed transfer info{}",transferEntity.toString());
        //查询事务日志表中是否有处理的记录，保持幂等性，如果有 不再处理，直接返回
        TransactionLogCriteria transactionLogCriteria = new TransactionLogCriteria();
        TransactionLogCriteria.Criteria criteria = transactionLogCriteria.createCriteria();
        criteria.andTransactionIdEqualTo(transferEntity.getTransactionID());

        List<TransactionLog> transactionLogs = transactionLogMapper.selectByExample(transactionLogCriteria);
        Optional<TransactionLog> transactionLog = transactionLogs.stream().findFirst();
        if (transactionLog.isPresent())
            return true;

        UserBalanceCriteria userBalanceCriteria  =  new UserBalanceCriteria();
        UserBalanceCriteria.Criteria user_criteria = userBalanceCriteria.createCriteria();
        user_criteria.andAccountEqualTo(transferEntity.getReciveAccount());
        List<UserBalance> userBalances = userBalanceMapper.selectByExample(userBalanceCriteria);
        Optional<UserBalance> first = userBalances.stream().findFirst();
        first.ifPresent(
                userBalance -> {
                    userBalance.setBalance(userBalance.getBalance()+transferEntity.getMoney());
                    userBalanceMapper.updateByExample(userBalance, userBalanceCriteria);
                    TransactionLog transactionLog2 = new TransactionLog();
                    transactionLog2.setTransactionId(transferEntity.getTransactionID());
                    transactionLogMapper.insert(transactionLog2);
                }
        );
        return true;
    }
}
