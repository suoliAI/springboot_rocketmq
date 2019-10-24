package com.gnnt.listener;

import com.gnnt.service.TransferMoneyProcessorService;
import com.gnnt.util.JsonUtil;
import com.gnnt.vo.TransferEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
@Slf4j
@Component
@RocketMQMessageListener(consumerGroup = "gnnt_consumer_transfer", topic = "transfer_info" )
public class TransferConsumerListener implements RocketMQListener<Message> {

    @Autowired
    private TransferMoneyProcessorService transferMoneyProcessorService;

    public void onMessage(Message message) {
        log.info("recive messsage body{}",message.getPayload());
        //解析message,转换为我们需要而业务实体
        TransferEntity transferEntity = this.message2TransferEntity(message);
        //调用业务层处理转账信息
        boolean result = transferMoneyProcessorService.transferAdd(transferEntity);


    }
    public TransferEntity message2TransferEntity(org.springframework.messaging.Message message){
        String json = new String((byte[]) message.getPayload());
        TransferEntity transfer_entity =(TransferEntity) JsonUtil.parseJson2Object(TransferEntity.class, "transfer_entity", json);
        return transfer_entity;
    }
}
