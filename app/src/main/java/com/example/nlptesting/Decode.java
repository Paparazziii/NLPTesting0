package com.example.nlptesting;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Decode {
    private String generation;

    private ArrayList ogc;

    private String template;

    private String text;
    private String code;

    public void setGeneration(String generation){
        this.generation = generation;
    }

    public String getGeneration(){
        return generation;
    }

    public void setTemplate(String template){
        this.template = template;
    }

    public String getTemplate(){
        return template;
    }

    public String getResult(){
        return generation + template;
    }

/**    public String getCode(){
        return code;
    }
    public String getText(){
        return text;
    }
    public void setCode(){
        this.code = code;
    }
    public void setText(){
        this.text = text;
    }
***/
}
