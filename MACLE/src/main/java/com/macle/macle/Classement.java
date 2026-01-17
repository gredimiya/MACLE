package com.macle.macle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Classement {
    int nb_place;
    ObservableList<String> top, out;

    Classement(int nb_place){
        this.nb_place = nb_place;
        this.top = FXCollections.observableArrayList();
        this.out = FXCollections.observableArrayList();
        for (int i=0; i<nb_place; i++) {
            this.top.add("");
        }
    }

    void add_top(String name, int pos){
        if (pos < 1 || pos > nb_place) return;
        int index = pos - 1;
        if (!this.top.get(index).isEmpty()) {
            this.decaler(index);
        }
        this.top.set(index, name);
    }

    void add_out(String name){
        this.out.add(name);
    }

    void decaler(int pos){
        // Si la dernière place n'est pas vide, l'élément sort du top
        if (!this.top.getLast().isEmpty()) {
            this.out.add(this.top.getLast());
        }
        // Décalage vers le bas
        for (int i = this.nb_place - 2; i >= pos; i--) {
            this.top.set(i + 1, this.top.get(i));
        }
    }

    void supp_out(String name){
        this.out.remove(name);
    }

}
