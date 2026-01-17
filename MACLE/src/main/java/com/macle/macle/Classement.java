package com.macle.macle;

import java.util.ArrayList;

public class Classement {
    int nb_place;
    ArrayList<String> top, out;

    Classement(int nb_place){
        this.nb_place = nb_place;
        for (int i=0; i<nb_place; i++) {
            this.top.add("");
        }
    }

    void add_top(String name, int pos){
        if (this.top.get(pos - 1).isEmpty()){
            this.decaler(pos-1);
        }
        this.top.set(pos-1,name);
    }

    void add_out(String name){
        this.out.add(name);
    }

    void decaler(int pos){
        if (this.top.getLast()!=""){
            this.out.add(this.top.getLast());
        }
        for (int i=this.nb_place-2;i>pos-1;i--){
            this.top.set(i+1,this.top.get(i));
        }
    }

    void supp_out(String name){
        this.out.remove(name);
    }

}
