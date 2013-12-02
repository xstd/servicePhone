package com.xstd.phoneService.model.status;

import java.io.Serializable;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table SMSSTATUS.
 */
public class SMSStatus implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private long serverID;
    private Long receviedCount;
    private Long sentCount;
    private Long leaveCount;
    private Long lastSentTime;
    private Long lastReceivedTime;
    private Long cmnetCount;
    private Long unicomCount;
    private Long telecomCount;
    private Long subwayCount;
    private Long unknownCount;

    public SMSStatus() {
    }

    public SMSStatus(long serverID) {
        this.serverID = serverID;
    }

    public SMSStatus(long serverID, Long receviedCount, Long sentCount, Long leaveCount, Long lastSentTime, Long lastReceivedTime, Long cmnetCount, Long unicomCount, Long telecomCount, Long subwayCount, Long unknownCount) {
        this.serverID = serverID;
        this.receviedCount = receviedCount;
        this.sentCount = sentCount;
        this.leaveCount = leaveCount;
        this.lastSentTime = lastSentTime;
        this.lastReceivedTime = lastReceivedTime;
        this.cmnetCount = cmnetCount;
        this.unicomCount = unicomCount;
        this.telecomCount = telecomCount;
        this.subwayCount = subwayCount;
        this.unknownCount = unknownCount;
    }

    public long getServerID() {
        return serverID;
    }

    public void setServerID(long serverID) {
        this.serverID = serverID;
    }

    public Long getReceviedCount() {
        return receviedCount;
    }

    public void setReceviedCount(Long receviedCount) {
        this.receviedCount = receviedCount;
    }

    public Long getSentCount() {
        return sentCount;
    }

    public void setSentCount(Long sentCount) {
        this.sentCount = sentCount;
    }

    public Long getLeaveCount() {
        return leaveCount;
    }

    public void setLeaveCount(Long leaveCount) {
        this.leaveCount = leaveCount;
    }

    public Long getLastSentTime() {
        return lastSentTime;
    }

    public void setLastSentTime(Long lastSentTime) {
        this.lastSentTime = lastSentTime;
    }

    public Long getLastReceivedTime() {
        return lastReceivedTime;
    }

    public void setLastReceivedTime(Long lastReceivedTime) {
        this.lastReceivedTime = lastReceivedTime;
    }

    public Long getCmnetCount() {
        return cmnetCount;
    }

    public void setCmnetCount(Long cmnetCount) {
        this.cmnetCount = cmnetCount;
    }

    public Long getUnicomCount() {
        return unicomCount;
    }

    public void setUnicomCount(Long unicomCount) {
        this.unicomCount = unicomCount;
    }

    public Long getTelecomCount() {
        return telecomCount;
    }

    public void setTelecomCount(Long telecomCount) {
        this.telecomCount = telecomCount;
    }

    public Long getSubwayCount() {
        return subwayCount;
    }

    public void setSubwayCount(Long subwayCount) {
        this.subwayCount = subwayCount;
    }

    public Long getUnknownCount() {
        return unknownCount;
    }

    public void setUnknownCount(Long unknownCount) {
        this.unknownCount = unknownCount;
    }

    @Override
    public String toString() {
        return "[SMSStatus]" + "serverID = " + serverID + ", " + "receviedCount = " + receviedCount + ", " + "sentCount = " + sentCount + ", " + "leaveCount = " + leaveCount + ", " + "lastSentTime = " + lastSentTime + ", " + "lastReceivedTime = " + lastReceivedTime + ", " + "cmnetCount = " + cmnetCount + ", " + "unicomCount = " + unicomCount + ", " + "telecomCount = " + telecomCount + ", " + "subwayCount = " + subwayCount + ", " + "unknownCount = " + unknownCount + "\r\n";
    }

}
