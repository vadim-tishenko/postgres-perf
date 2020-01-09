package ru.cwl.example.psqlperf.jdbc;

public class TfcSensor {
    int idTr;//          integer  not null,
    long gmtEventTime;// bigint   not null,
    int nNum;//          integer not null,
    float val;//            real     not null,
    long gmtSysTime;//   bigint   not null

    public int getIdTr() {
        return idTr;
    }

    public void setIdTr(int idTr) {
        this.idTr = idTr;
    }

    public long getGmtEventTime() {
        return gmtEventTime;
    }

    public void setGmtEventTime(long gmtEventTime) {
        this.gmtEventTime = gmtEventTime;
    }

    public int getnNum() {
        return nNum;
    }

    public void setnNum(int nNum) {
        this.nNum = nNum;
    }

    public float getVal() {
        return val;
    }

    public void setVal(float val) {
        this.val = val;
    }

    public long getGmtSysTime() {
        return gmtSysTime;
    }

    public void setGmtSysTime(long gmtSysTime) {
        this.gmtSysTime = gmtSysTime;
    }
}
