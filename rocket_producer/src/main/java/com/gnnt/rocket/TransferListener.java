package com.gnnt.rocket;

import com.gnnt.mapper.TransactionLogMapper;
import com.gnnt.model.TransactionLog;
import com.gnnt.model.TransactionLogCriteria;
import com.gnnt.service.TransferMoneyProcessorService;
import com.gnnt.util.JsonUtil;
import com.gnnt.vo.TransferEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
@Slf4j
@Component
@RocketMQTransactionListener(txProducerGroup = "gnnt_producer_transfer")
//本地事务监听器，txProducerGroup 消息提供者组 名称
public class TransferListener implements RocketMQLocalTransactionListener {

    @Autowired
    private TransferMoneyProcessorService transferMoneyProcessorService;

    @Autowired
    private TransactionLogMapper transactionLogMapper;
    /**
     * 执行本地事务
     * 当事务消息发送到mq成功，为prepare状态时回调此方法 执行本地事务
     * @param message 消息体对象
     * @param o
     * @return
     */
    public RocketMQLocalTransactionState executeLocalTransaction(Message message, Object o) {
        log.info("TransferListener executeLocalTransaction is executed...");
        //解析message 转换为TransferEntity
        TransferEntity transfer_entity =this.message2TransferEntity(message);
        //执行本地事务操作
        boolean result = transferMoneyProcessorService.transferSub(transfer_entity);
        //根据本地事务处理结果，给mq返回状态信息，处于prepare状态的消息是提交还是回滚。
        log.info("LocalTransaction execute result{}",result);
        if (result)
            return RocketMQLocalTransactionState.COMMIT;
        return RocketMQLocalTransactionState.ROLLBACK;
    }

    /**
     * 检查本地事务
     * @param message
     * @return
     */
    public RocketMQLocalTransactionState checkLocalTransaction(Message message) {
        log.info("TransferListener checkLocalTransaction is executed...");
        //解析message 转换为TransferEntity
        TransferEntity transfer_entity =this.message2TransferEntity(message);
        
        //查询事务日志表是否有当前事务ID的记录，然后根据查询结果，返回对应状态

        TransactionLogCriteria transactionLogCriteria = new TransactionLogCriteria();
        TransactionLogCriteria.Criteria criteria = transactionLogCriteria.createCriteria();
        criteria.andTransactionIdEqualTo(transfer_entity.getTransactionID());

        List<TransactionLog> transactionLogs = transactionLogMapper.selectByExample(transactionLogCriteria);
        Optional<TransactionLog> first = transactionLogs.stream().findFirst();

        log.info("TransactionLog transactionID{},是否存在{}",transfer_entity.getTransactionID(), first.isPresent());

        if(first.isPresent())
            return RocketMQLocalTransactionState.COMMIT;
        return RocketMQLocalTransactionState.UNKNOWN;
    }
    public TransferEntity message2TransferEntity(Message message){
        String json = new String((byte[]) message.getPayload());
        TransferEntity transfer_entity =(TransferEntity) JsonUtil.parseJson2Object(TransferEntity.class, "transfer_entity", json);
        return transfer_entity;
    }
}
