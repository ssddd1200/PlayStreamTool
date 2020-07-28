package com.yjs.model;

public class TableModel {

    public String outStream;
    public String inputStream;
    public String pro;

    public TableModel(String outStream, String inputStream, String pro) {
        this.outStream = outStream;
        this.inputStream = inputStream;
        this.pro = pro;
    }

    public String getOutStream() {
        return outStream;
    }

    public void setOutStream(String outStream) {
        this.outStream = outStream;
    }



    public String getInputStream() {
        return inputStream;
    }

    public void setInputStream(String inputStream) {
        this.inputStream = inputStream;
    }

    public String getPro() {
        return pro;
    }

    public void setPro(String pro) {
        this.pro = pro;
    }


    @Override
    public String toString() {
        return outStream + ';' + inputStream + ';' + pro;
    }
}
