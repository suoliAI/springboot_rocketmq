package com.gnnt.service;

import com.gnnt.vo.TransferEntity;

public interface TransferMoneyProcessorService {
    void sendTransferMessage(TransferEntity transferEntity);
    /**
     * 转账减方法
     * @param transferEntity
     * @return
     */
    boolean transferSub(TransferEntity transferEntity);
}
