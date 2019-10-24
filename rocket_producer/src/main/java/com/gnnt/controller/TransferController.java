package com.gnnt.controller;

import com.gnnt.service.TransferMoneyProcessorService;
import com.gnnt.vo.TransferEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/transfer")
public class TransferController {

    @Autowired
    private TransferMoneyProcessorService transferMoneyProcessorService;

    @RequestMapping("/proc/{payaccount}/{reciveaccount}/{money}")
    public String transfer(@PathVariable("payaccount") String payAccount,@PathVariable("reciveaccount") String reciveAccount,@PathVariable("money") Double money ){
        TransferEntity transferEntity = new TransferEntity();
        String tx_ID = UUID.randomUUID().toString();
        transferEntity.setTransactionID(tx_ID);
        transferEntity.setPayAccount(payAccount);
        transferEntity.setReciveAccount(reciveAccount);
        transferEntity.setMoney(money);
        transferMoneyProcessorService.sendTransferMessage(transferEntity);
        return "转账成功";
    }
}
