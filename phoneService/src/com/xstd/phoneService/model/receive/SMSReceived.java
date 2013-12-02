package com.xstd.phoneService.model.receive;

import java.io.Serializable;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table SMSRECEIVED.
 */
public class SMSReceived implements Serializable {

    private static final long serialVersionUID = 1L;
    
    /** Not-null value. */
    private String from;
    /** Not-null value. */
    private String imei;
    /** Not-null value. */
    private String phoneType;
    private String networkType;
    private long receiveTime;

    public SMSReceived() {
    }

    public SMSReceived(String from) {
        this.from = from;
    }

    public SMSReceived(String from, String imei, String phoneType, String networkType, long receiveTime) {
        this.from = from;
        this.imei = imei;
        this.phoneType = phoneType;
        this.networkType = networkType;
        this.receiveTime = receiveTime;
    }

    /** Not-null value. */
    public String getFrom() {
        return from;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setFrom(String from) {
        this.from = from;
    }

    /** Not-null value. */
    public String getImei() {
        return imei;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setImei(String imei) {
        this.imei = imei;
    }

    /** Not-null value. */
    public String getPhoneType() {
        return phoneType;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setPhoneType(String phoneType) {
        this.phoneType = phoneType;
    }

    public String getNetworkType() {
        return networkType;
    }

    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }

    public long getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(long receiveTime) {
        this.receiveTime = receiveTime;
    }

    @Override
    public String toString() {
        return "[SMSReceived]" + "from = " + from + ", " + "imei = " + imei + ", " + "phoneType = " + phoneType + ", " + "networkType = " + networkType + ", " + "receiveTime = " + receiveTime + "\r\n";
    }

}