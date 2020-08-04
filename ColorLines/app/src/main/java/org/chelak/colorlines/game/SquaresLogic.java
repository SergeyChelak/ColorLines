package org.chelak.colorlines.game;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sergey on 06.02.2016.
 */
public class SquaresLogic extends BaseLogic {

    public static SquaresLogic getInstance() {
        return new SquaresLogic(8, 6);
    }

    private Location[][] directionArray = {
            { new Location(1,0), new Location(0,1), new Location(1,1) },
            { new Location(-1,0), new Location(0,-1), new Location(-1,-1) },
            { new Location(-1,0), new Location(0,1), new Location(-1,1) },
            { new Location(1,0), new Location(0,-1), new Location(1,-1) },
    };

    public SquaresLogic(int boardSize, int colorsCount) {
        super(boardSize, colorsCount);
    }

    private List<Location> findSquare(Location location, int color, Location[] directions) {
        List<Location> steps = new ArrayList<>();
        steps.add(location);
        for ( Location dir : directions ) {
            Location loc = Location.sum(dir, location);
            if ( isValidRange(loc) && get(loc) == color) {
                steps.add(loc);
            }
        }
        if ( steps.size() == 4 )
            return steps;
        return null;
    }

    @Override
    protected boolean isFigureCompleted(Location location) {
        int color = get(location);
        List<List<Location>> stepList = new ArrayList<>();
        for ( Location[] directions : directionArray ) {
            List<Location> list = findSquare(location, color, directions);
            if ( list != null )
                stepList.add(list);
        }
        return markToRemove(stepList);
    }
}
