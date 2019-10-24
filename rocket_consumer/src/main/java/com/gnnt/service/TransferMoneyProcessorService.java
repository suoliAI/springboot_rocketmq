package com.gnnt.service;

import com.gnnt.vo.TransferEntity;

public interface TransferMoneyProcessorService {
    boolean transferAdd(TransferEntity transferEntity);
}
