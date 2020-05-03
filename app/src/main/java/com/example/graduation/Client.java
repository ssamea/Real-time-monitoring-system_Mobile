package com.example.graduation;

public class Client {
    private String ID;
    private String PASSWORD;
    private String name;
    private String ADDRESS;
    private String EMAIL;
    private String MOBILE;
    private String RESDIENT_NUMBER;



    public Client(String ID,String PASSWORD,String name,String ADDRESS, String EMAIL, String MOBILE, String RESDIENT_NUMBER){
        this.ID=ID;
        this.PASSWORD=PASSWORD;
        this.name=name;
        this.ADDRESS=ADDRESS;
        this.EMAIL=EMAIL;
        this.MOBILE=MOBILE;
        this.RESDIENT_NUMBER=RESDIENT_NUMBER;
    }


    public String getADDRESS() {
        return ADDRESS;
    }

    public void setADDRESS(String ADDRESS) {
        this.ADDRESS = ADDRESS;
    }

    public String getEMAIL() {
        return EMAIL;
    }

    public void setEMAIL(String EMAIL) {
        this.EMAIL = EMAIL;
    }

    public String getMOBILE() {
        return MOBILE;
    }

    public void setMOBILE(String MOBILE) {
        this.MOBILE = MOBILE;
    }

    public String getRESDIENT_NUMBER() {
        return RESDIENT_NUMBER;
    }

    public void setRESDIENT_NUMBER(String RESDIENT_NUMBER) {
        this.RESDIENT_NUMBER = RESDIENT_NUMBER;
    }



    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getPASSWORD() {
        return PASSWORD;
    }

    public void setPASSWORD(String PASSWORD) {
        this.PASSWORD = PASSWORD;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}