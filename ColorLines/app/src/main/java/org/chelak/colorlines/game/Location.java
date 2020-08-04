package org.chelak.colorlines.game;

import org.chelak.colorlines.base.Coding;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sergey.chelak on 14.01.16.
 */
public class Location implements Coding {

    private int x;
    private int y;

    public Location() {
        //
    }

    public Location(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean equals(Location location) {
        return location != null && equals(location.getX(), location.getY());
    }

    public boolean equals(int x, int y) {
        return this.x == x && this.y == y;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public static Location sum(Location a, Location b) {
        return new Location(a.x+b.x, a.y+b.y);
    }

    @Override
    public JSONObject encode() throws Exception {
        JSONObject object = new JSONObject();
        object.put("x", x);
        object.put("y", y);
        return object;
    }

    @Override
    public void decode(JSONObject jsonObject) throws JSONException {
        this.x = jsonObject.getInt("x");
        this.y = jsonObject.getInt("y");
    }
}
