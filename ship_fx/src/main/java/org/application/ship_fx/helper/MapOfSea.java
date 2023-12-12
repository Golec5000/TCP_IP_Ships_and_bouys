package org.application.ship_fx.helper;

import java.util.ArrayList;
import java.util.List;

public class MapOfSea {
    private final int gridSize = 40;
    private final int cellSize = 20;
    private List<Cord> cords;

    public MapOfSea() {
        this.cords = new ArrayList<>();

        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                cords.add(new Cord(i, j));
            }
        }
    }

    public List<Cord> getCells() {
        return cords;
    }
}
