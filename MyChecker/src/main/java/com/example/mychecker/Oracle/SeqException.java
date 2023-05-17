package com.example.mychecker.Oracle;

public class SeqException extends Exception{
    //无参构造
    public SeqException() {
        super();
    }
    //带有String的有参构造
    public SeqException(String s) {
        super(s);
    }

    public SeqException(SeqException e) {
        super(e);
    }
}
