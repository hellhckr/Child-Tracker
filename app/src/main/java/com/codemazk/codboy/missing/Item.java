package com.codemazk.codboy.missing;

/**
 * Created by abhinay on 21-03-2017.
 */

public class Item {
    String id;
    String name;
    String mobile;



    public Item(String id,String name){
        this.id=id;
        this.name=name;


    }
    public Item(String id,String name,String mobile){
        this.id=id;
        this.name=name;
        this.mobile=mobile;


    }
    //Getter and Setter

    public String getIdd() {return id;}

    public void setIdd(String id){this.id=id;}

    public String getName() {return name;}

    public void setName(String name){this.name=name;}

    public String getMobile() {return mobile;}

    public void setMobile(String mobile){this.mobile=mobile;}
}
