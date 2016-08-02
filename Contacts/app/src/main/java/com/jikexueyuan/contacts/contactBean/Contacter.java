package com.jikexueyuan.contacts.contactBean;

/**
 * Created by Administrator on 2016/5/21.
 **/
public class Contacter {

    private String name;
    private String number;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Contacter(String name,String number) {
        this.number = number;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Contacter{" +
                "name='" + name + '\'' +
                ", number='" + number + '\'' +
                '}';
    }
}
