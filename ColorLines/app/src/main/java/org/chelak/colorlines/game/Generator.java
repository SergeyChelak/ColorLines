package org.chelak.colorlines.game;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sergey on 17.01.2016.
 */
public class Generator extends Board {

    protected static final String KEY_COLOR_COUNT = "color.count";
    protected static final String KEY_RESERVED_COLOR = "reserved.color";

    private int colorsCount;
    private int reservedColor;

    public Generator(int size, int colorsCount) {
        super(size);
        this.colorsCount = colorsCount;
        reservedColor = 0;
    }

    @Override
    public JSONObject encode() throws Exception {
        JSONObject obj = super.encode();
        obj.put(KEY_COLOR_COUNT, colorsCount);
        obj.put(KEY_RESERVED_COLOR, reservedColor);                    // I think it's senseless
        return obj;
    }

    @Override
    public void decode(JSONObject jsonObject) throws JSONException {
        super.decode(jsonObject);
        this.colorsCount = jsonObject.getInt(KEY_COLOR_COUNT);
        this.reservedColor = jsonObject.getInt(KEY_RESERVED_COLOR);     // I think it's senseless
    }

    public void refreshBoard(int[][] src) {
        valuesCopy(src, this.board);
        reservedColor = 0;
    }

    private List<Location> getEmptyCellList() {
        List<Location> list = new ArrayList<>();
        for ( int i = 0; i < boardSize; ++i )
            for ( int j = 0; j < boardSize; ++j) {
                Location location = new Location(i,j);
                if ( get(location) == 0 )
                    list.add(location);
            }
        return list;
    }

    private int getBallColor() {
        int value;
        if ( reservedColor != 0 ) {
            value = reservedColor;
            reservedColor = 0;
        } else {
            value = (int)(-Math.random()*colorsCount) - 1;
        }
        return value;
    }

    public int chooseNextBallLocation(int count) {
        List<Location> emptyCells = getEmptyCellList();
        int emptyCellsCount = emptyCells.size();
        if ( emptyCells.size() > 0 ) {
            for (int i = 0; i < count; ++i) {
                int position = (int) (Math.random() * emptyCells.size());
                Location location = emptyCells.get(position);
                set(location, getBallColor());
                emptyCells.remove(position);
                if ( emptyCells.size() == 0 )
                    break;
            }
        }
        return emptyCellsCount;
    }

    public void updateNextBallLocation(Location from, Location to, int color) {
        // if user put the ball at planed cell the game generates
        // a missing balls in current empty cells
        reservedColor = get(to);
        if ( reservedColor < 0 ) {
            set(to, color);   // busy
            set(from, 0);   // empty
            chooseNextBallLocation(1);
        } else {
            swap(from, to);
        }
    }


}
