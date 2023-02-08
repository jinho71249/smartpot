package com.example.smartpot;

import android.app.Application;



public class Db extends Application {

    private String potNames; //모든 화분이름이 있는 값 가져오기
    private String potIDs;
    private int potNum;   //화분 갯수
    private String selectedPot; //선택된 화분
    private String wlv;      //수위값
    private String names[]=new String[100]; //화분이름을 하나하나 저장한 배열
    private String ids[]=new String[100];
    private int namesnum =0;  //name배열 번호
    private int idnum=0;

    public String getPotIDs() {
        return potIDs;
    }

    public void setPotIDs(String potIDs) {
        this.potIDs = potIDs;
    }

    public int getIdnum() {
        return idnum;
    }

    public void setIdnum(int idnum) {
        this.idnum = idnum;
    }

    public String getWlv() {
        return wlv;
    }

    public void setWlv(String wlv) {
        this.wlv = wlv;
    }

    public int getNamesnum() {
        return namesnum;
    }

    public void setNamesnum(int namesnum) {
        this.namesnum = namesnum;
    }

    public String getNames(int i) {
        return names[i];
    }

    public void setNames(String names) {
        this.names[namesnum++] = names;
    }

    public String getIds(int i) {
        return ids[i];
    }

    public void setIds(String ids) {
        this.ids[idnum++] = ids;
    }

    public Db(){}

    public String getSelectedPot() {
        return selectedPot;
    }

    public void setSelectedPot(String selectedPot) {
        this.selectedPot = selectedPot;
    }

    public String getPotNames() {
        return potNames;
    }

    public void setPotNames(String potNames) {
        this.potNames = potNames;
    }

    public int getPotNum() {
        return potNum;
    }

    public void setPotNum(int potNum) {
        this.potNum = potNum;
    }

}
