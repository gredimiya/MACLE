package com.macle.macle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Classement {
    int nb_place;
    ObservableList<String> top, out;

    public Classement(int nb_place) {
        this.nb_place = nb_place;
        this.top = FXCollections.observableArrayList();
        this.out = FXCollections.observableArrayList();
        for (int i = 0; i < nb_place; i++) {
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

    public void decaler(int index) {
        // 1. Chercher le premier trou (chaîne vide) à partir de l'index d'insertion
        int indexTrou = -1;
        for (int i = index; i < nb_place; i++) {
            if (top.get(i).isEmpty()) {
                indexTrou = i;
                break;
            }
        }

        // 2. Si on a trouvé un trou, on décale uniquement jusqu'à ce trou
        if (indexTrou != -1) {
            for (int i = indexTrou; i > index; i--) {
                top.set(i, top.get(i - 1));
            }
            // La place à l'index est maintenant prête à être écrasée par le nouvel anime
        }
        // 3. Si aucun trou n'est trouvé, on suit l'ancien comportement (le dernier sort)
        else {
            String dernier = top.get(nb_place - 1);
            if (!dernier.isEmpty()) {
                out.add(dernier);
            }
            for (int i = nb_place - 1; i > index; i--) {
                top.set(i, top.get(i - 1));
            }
        }
    }

    void supp_out(String name){
        this.out.remove(name);
    }

    public void resize(int newSize) {
        // 1. Si on réduit la taille
        if (newSize < this.nb_place) {
            // On récupère tout ce qui dépasse la nouvelle limite
            for (int i = newSize; i < this.nb_place; i++) {
                String animeQuiSort = this.top.get(i);
                if (animeQuiSort != null && !animeQuiSort.isEmpty()) {
                    this.out.add(animeQuiSort); // On l'envoie dans Dehors
                }
            }
            // On supprime les places en trop du Top
            this.top.subList(newSize, this.nb_place).clear();
        }
        // 2. Si on augmente la taille
        else if (newSize > this.nb_place) {
            for (int i = this.nb_place; i < newSize; i++) {
                this.top.add(""); // On ajoute des places vides
            }
        }

        this.nb_place = newSize;
    }

}
