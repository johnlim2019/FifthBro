package com.example.testbro;

import java.io.Serializable;
import java.util.ArrayList;

public class ParserClass {
    private ArrayList<Serializable> parse;
    ParserClass(){}
    ParserClass(Serializable obj){
        this.parse.add(obj);
    }
    public Serializable getObj(){
        return this.parse.get(0);
    }
    public void addObj(Serializable obj){
        this.parse.set(0, obj);
    }

}
