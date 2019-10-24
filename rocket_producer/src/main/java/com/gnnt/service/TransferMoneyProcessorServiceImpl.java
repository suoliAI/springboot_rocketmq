package com.gnnt.service;

import com.gnnt.mapper.TransactionLogMapper;
import com.gnnt.mapper.UserBalanceMapper;
import com.gnnt.model.TransactionLog;
import com.gnnt.model.UserBalance;
import com.gnnt.model.UserBalanceCriteria;
import com.gnnt.util.JsonUtil;
import com.gnnt.vo.TransferEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class TransferMoneyProcessorServiceImpl implements  TransferMoneyProcessorService {
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    @Autowired
    private UserBalanceMapper userBalanceMapper;
    @Autowired
    private TransactionLogMapper transactionLogMapper;

    public void sendTransferMessage(TransferEntity transferEntity) {
        String transfer_json = JsonUtil.parseObject2Json(transferEntity, "transfer_entity");
        Message<String> message = MessageBuilder.withPayload(transfer_json).build();
        TransactionSendResult transactionSendResult = rocketMQTemplate.sendMessageInTransaction("gnnt_producer_transfer", "transfer_info", message, null);
        log.info("send transfer message body={}, result{}",message.getPayload(),transactionSendResult.getSendStatus());
    }
    @Transactional
    public boolean transferSub(TransferEntity transferEntity) {
        log.info("TransferMoneyProcessorServiceImpl transferSub is executed, transfer info{}",transferEntity.toString());
        UserBalanceCriteria userBalanceCriteria  =  new UserBalanceCriteria();
        UserBalanceCriteria.Criteria criteria = userBalanceCriteria.createCriteria();
        criteria.andAccountEqualTo(transferEntity.getPayAccount());
        List<UserBalance> userBalances = userBalanceMapper.selectByExample(userBalanceCriteria);
        Optional<UserBalance> first = userBalances.stream().findFirst();


        //执行付款账户的扣款，并记录当前事务ID，保持幂等性

        first.ifPresent(
                userBalance ->{
                    userBalance.setBalance(userBalance.getBalance()-transferEntity.getMoney());
                    userBalanceMapper.updateByExample(userBalance, userBalanceCriteria);
                    TransactionLog transactionLog = new TransactionLog();
                    transactionLog.setTransactionId(transferEntity.getTransactionID());
                    transactionLogMapper.insert(transactionLog);
                }
        );
        log.info("TransferMoneyProcessorServiceImpl transferSub execute success...");
        return true;
    }
}
