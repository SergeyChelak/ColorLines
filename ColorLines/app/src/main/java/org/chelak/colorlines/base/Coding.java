package org.chelak.colorlines.base;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Sergey on 20.02.2016.
 */
public interface Coding {

    JSONObject encode() throws Exception;

    void decode(JSONObject jsonObject) throws JSONException;
}
