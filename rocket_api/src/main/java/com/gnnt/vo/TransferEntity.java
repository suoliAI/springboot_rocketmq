package com.gnnt.vo;

import lombok.Data;

/**
 * 转账实体类
 */
@Data
public class TransferEntity {
    private String payAccount;
    private String reciveAccount;
    private double money;
    private String transactionID;

    @Override
    public String toString() {
        return "TransferEntity{" +
                "payAccount='" + payAccount + '\'' +
                ", reciveAccount='" + reciveAccount + '\'' +
                ", money=" + money +
                ", transactionID='" + transactionID + '\'' +
                '}';
    }
}
