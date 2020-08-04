package org.chelak.colorlines.game;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by sergey.chelak on 14.01.16.
 */
public class WayFinder extends Board {

    private List<Location> way;

    public WayFinder(int[][] gameBoard) {
        super(gameBoard.length);

        int size = gameBoard.length;
        for (int i = 0; i < size; ++i)
            for (int j = 0; j < size; ++j) {
                board[i][j] = gameBoard[i][j] == 0 ? 0 : -1;
            }
    }

    private Location[] getWaveFronts(int i, int j) {
        return new Location[]{
                new Location(i + 1, j),
                new Location(i - 1, j),
                new Location(i, j + 1),
                new Location(i, j - 1)
        };
    }


    private int waveSearch(Location from, Location to) {
        int size = board.length;
        int front = 1;
        set(from, front);
        boolean hasMoreSteps = true;
        while (hasMoreSteps) {
            List<Location> steps = new ArrayList<>();
            for (int i = 0; i < size; ++i)
                for (int j = 0; j < size; ++j)
                    if (get(i, j) == front) {
                        steps.add(new Location(i, j));
                    }
            hasMoreSteps = steps.size() > 0;
            for (Location step : steps) {
                if (step.equals(to))
                    return front;
                Location[] waveFronts = getWaveFronts(step.getX(), step.getY());
                for (Location location : waveFronts)
                    if (isValidRange(location) && get(location) == 0) {
                        set(location, front+1);
                    }
            }
            front++;
        }
        return 0;
    }

    private int getValue(Location location) {
        return board[location.getX()][location.getY()];
    }

    private List<Location> getBackWay(int front, Location location) {
        List<Location> steps = new ArrayList<>(front + 1);
        int currentFront = front;
        Location currentLocation = location;
        while (currentFront != 1) {
            steps.add(currentLocation);
            boolean isStepFound = false;
            Location[] waveFronts = getWaveFronts(currentLocation.getX(), currentLocation.getY());
            for (Location wfLocation : waveFronts)
                if (isValidRange(wfLocation)) {
                    int waveFront = getValue(wfLocation);
                    if (waveFront == currentFront - 1) {
                        currentFront = waveFront;
                        currentLocation = wfLocation;
                        steps.add(currentLocation);
                        isStepFound = true;
                        break;
                    }
                }
            if (!isStepFound)
                return null;
        }
        return steps;
    }

    public boolean findWay(Location from, Location to) {
        int steps = waveSearch(from, to);
        way = null;
        if (steps > 0) {
            way = getBackWay(steps, to);
            if (way != null)
                Collections.reverse(way);
        }
        return steps > 0;
    }

    public List<Location> getWay() {
        return way;
    }
}
