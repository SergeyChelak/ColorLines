package org.chelak.colorlines.game;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sergey on 05.02.2016.
 */
public class RingsLogic extends BaseLogic {

    public static RingsLogic getInstance() {
        return new RingsLogic(8, 6);
    }

    private static Location directions[] = {
            new Location(-1, 0),
            new Location(1, 0),
            new Location(0, -1),
            new Location(0, 1)
    };

    public RingsLogic(int boardSize, int colorsCount) {
        super(boardSize, colorsCount);
    }

    private List<Location> findRing(Location center, int color) {
        if (isValidRange(center)) {
            List<Location> steps = new ArrayList<>();
            for (Location direction : directions) {
                Location cell = Location.sum(center, direction);
                if (isValidRange(cell) && get(cell) == color) {
                    steps.add(cell);
                }
            }
            if (steps.size() == directions.length)
                return steps;
        }
        return null;
    }

    @Override
    protected boolean isFigureCompleted(Location location) {
        int color = get(location);
        List<List<Location>> stepList = new ArrayList<>();
        for (Location direction : directions) {
            Location center = Location.sum(location, direction);
            List<Location> list = findRing(center, color);
            if ( list != null )
                stepList.add(list);
        }
        return markToRemove(stepList);
    }
}
