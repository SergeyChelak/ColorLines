package org.chelak.colorlines.game;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sergey.chelak on 15.01.16.
 */
public class LinesLogic extends BaseLogic {

    public static LinesLogic getInstance() {
        return new LinesLogic(9, 7);
    }

    public LinesLogic(int boardSize, int colorsCount) {
        super(boardSize, colorsCount);
    }

    // perform searching with a simultaneously moving in specified directions
    private List<Location> findFigure(Location position, Location[] direction) {
        Location[] currentPosition = new Location[direction.length];
        for (int i = 0; i < currentPosition.length; ++i)
            currentPosition[i] = position;
        List<Location> steps = new ArrayList<>();
        steps.add(position);
        int color = get(position);
        boolean hasStep = true;
        while (hasStep) {
            hasStep = false;
            for (int i = 0; i < currentPosition.length; ++i) {
                Location pos = currentPosition[i];
                Location newPosition = Location.sum(pos, direction[i]);
                if (isValidRange(newPosition) && get(newPosition) == color) {
                    hasStep = true;
                    currentPosition[i] = newPosition;
                    steps.add(newPosition);
                }
            }
        }
        return steps;
    }

    @Override
    protected boolean isFigureCompleted(Location location) {
        Location[][] arr = {
                {new Location(1, 0), new Location(-1, 0)},
                {new Location(0, 1), new Location(0, -1)},
                {new Location(1, 1), new Location(-1, -1)},
                {new Location(1, -1), new Location(-1, 1)}
        };
        List<List<Location>> stepList = new ArrayList<>();
        // find lines in 4 directions with length strictly more than 4
        for (Location[] direction : arr) {
            List<Location> steps = findFigure(location, direction);
            if (steps.size() > 4) {
                stepList.add(steps);
            }
        }
        // mark cells to remove with negative value
        return markToRemove(stepList);
    }

    @Override
    protected int calculateScores(int value) {
        return value * (value - 4);
    }
}
