package com.medakk.alternote.note;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Note
 *  -a simple note with a title and some text
 */
public class SimpleNote {
    private String title;
    private String content;

    public SimpleNote(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    private static final String JSON_TITLE = "title";
    private static final String JSON_CONTENT = "content";
    public JSONObject toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JSON_TITLE, title);
        jsonObject.put(JSON_CONTENT, content);

        return jsonObject;
    }
}
