package com.locassa.yamo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.locassa.yamo.model.enums.SubscriptionType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
public class UserSubscription extends AuditEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long uuid;

    @ManyToOne
    private User associatedUser;

    private Date startDate;

    private Date endDate;

    private SubscriptionType type;

    @Transient
    private boolean expired;

    @Column(columnDefinition = "TEXT")
    @JsonIgnore
    private String latestAppleReceipt;

    @Column(columnDefinition = "TEXT")
    @JsonIgnore
    private String receiptData;

    @JsonIgnore
    private String uniqueIdentifier;

    private String orderId = null;

    private String packageName = null;

    private String productId = null;

    private Long purchaseTime = 0L;

    private int purchaseState = -1;

    @Column(columnDefinition = "TEXT")
    private String purchaseToken = null;

    private String developerPayload = null;

    private boolean autoRenewing = false;

    public Long getUuid() {
        return uuid;
    }

    public void setUuid(Long uuid) {
        this.uuid = uuid;
    }

    public User getAssociatedUser() {
        return associatedUser;
    }

    public void setAssociatedUser(User associatedUser) {
        this.associatedUser = associatedUser;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public SubscriptionType getType() {
        return type;
    }

    public void setType(SubscriptionType type) {
        this.type = type;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public String getLatestAppleReceipt() {
        return latestAppleReceipt;
    }

    public void setLatestAppleReceipt(String latestAppleReceipt) {
        this.latestAppleReceipt = latestAppleReceipt;
    }

    public String getReceiptData() {
        return receiptData;
    }

    public void setReceiptData(String receiptData) {
        this.receiptData = receiptData;
    }

    public String getUniqueIdentifier() {
        return uniqueIdentifier;
    }

    public void setUniqueIdentifier(String uniqueIdentifier) {
        this.uniqueIdentifier = uniqueIdentifier;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Long getPurchaseTime() {
        return purchaseTime;
    }

    public void setPurchaseTime(Long purchaseTime) {
        this.purchaseTime = purchaseTime;
    }

    public int getPurchaseState() {
        return purchaseState;
    }

    public void setPurchaseState(int purchaseState) {
        this.purchaseState = purchaseState;
    }

    public String getPurchaseToken() {
        return purchaseToken;
    }

    public void setPurchaseToken(String purchaseToken) {
        this.purchaseToken = purchaseToken;
    }

    public String getDeveloperPayload() {
        return developerPayload;
    }

    public void setDeveloperPayload(String developerPayload) {
        this.developerPayload = developerPayload;
    }

    public boolean isAutoRenewing() {
        return autoRenewing;
    }

    public void setAutoRenewing(boolean autoRenewing) {
        this.autoRenewing = autoRenewing;
    }
}
