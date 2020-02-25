package com.andre601.statusnpc.util;

public enum OnlineStatus{
    ONLINE("Online"),
    OFFLINE("Offline"),
    AFK("AFK");
    
    private String status;
    
    OnlineStatus(String status){
        this.status = status;
    }
    
    public String getStatus(){
        return status;
    }
}
