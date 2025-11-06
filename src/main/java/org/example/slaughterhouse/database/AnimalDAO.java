package org.example.slaughterhouse.database;

import java.util.ArrayList;

public interface AnimalDAO {
    public ArrayList<Integer> readProductsWithAnimal(int id);
}
