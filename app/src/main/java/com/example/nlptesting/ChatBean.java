package com.example.nlptesting;

public class ChatBean {
    public static final int SEND=1;//send
    public static final int RECEIVE=2;//receive
    private int state;//message status
    private String message;//info

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
