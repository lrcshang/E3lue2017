package com.e3lue.us.model;

import java.io.Serializable;

/**
 * Created by Leo on 2016/10/8.
 */

public class GameClubOrder implements Serializable {
    private int ID;
    private String GameClubName;
    private int GameClubID;
    private int DirectOrder;
    private String OrderCode;
    private String CustomerName;
    private String Currency;
    private String DocumentMaker;
    private String CreateTime;
    private String ClubAmount;
    private String SaleAmount;
    private String MarketingCost;
    private String Discount;
    private String PurchaseAmount;
    private String FinanceInfo;
    private String StateName;
    private String LastReceiveId;
    private int State;

    public int getState() {
        return State;
    }

    public void setState(int state) {
        State = state;
    }



    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getGameClubName() {
        return GameClubName;
    }

    public void setGameClubName(String gameClubName) {
        GameClubName = gameClubName;
    }

    public int getGameClubID() {
        return GameClubID;
    }

    public void setGameClubID(int gameClubID) {
        GameClubID = gameClubID;
    }

    public int getDirectOrder() {
        return DirectOrder;
    }

    public void setDirectOrder(int directOrder) {
        DirectOrder = directOrder;
    }

    public String getOrderCode() {
        return OrderCode;
    }

    public void setOrderCode(String orderCode) {
        OrderCode = orderCode;
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String customerName) {
        CustomerName = customerName;
    }

    public String getCurrency() {
        return Currency;
    }

    public void setCurrency(String currency) {
        Currency = currency;
    }

    public String getDocumentMaker() {
        return DocumentMaker;
    }

    public void setDocumentMaker(String documentMaker) {
        DocumentMaker = documentMaker;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public String getClubAmount() {
        return ClubAmount;
    }

    public void setClubAmount(String clubAmount) {
        ClubAmount = clubAmount;
    }

    public String getSaleAmount() {
        return SaleAmount;
    }

    public void setSaleAmount(String saleAmount) {
        SaleAmount = saleAmount;
    }

    public String getMarketingCost() {
        return MarketingCost;
    }

    public void setMarketingCost(String marketingCost) {
        MarketingCost = marketingCost;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }

    public String getPurchaseAmount() {
        return PurchaseAmount;
    }

    public void setPurchaseAmount(String purchaseAmount) {
        PurchaseAmount = purchaseAmount;
    }

    public String getFinanceInfo() {
        return FinanceInfo;
    }

    public void setFinanceInfo(String financeInfo) {
        FinanceInfo = financeInfo;
    }

    public String getStateName() {
        return StateName;
    }

    public void setStateName(String stateName) {
        StateName = stateName;
    }

    public String getLastReceiveId() {
        return LastReceiveId;
    }

    public void setLastReceiveId(String lastReceiveId) {
        LastReceiveId = lastReceiveId;
    }


}
