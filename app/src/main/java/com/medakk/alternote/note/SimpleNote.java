package com.medakk.alternote.note;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

/**
 * Note
 *  -a simple note with a title and some text
 */
public class SimpleNote {
    /*
     *this is used to identify the note in the current context,
     * ie: it doesn't need to be retained(saved/loaded)
     */
    private UUID uuid;
    public boolean dirty;

    private String title;
    private String content;

    public SimpleNote(String title, String content) {
        this.title = title;
        this.content = content;

        uuid = UUID.randomUUID();
        dirty = false;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void set(String title, String content) {
        this.title = title;
        this.content = content;
        dirty = true;
    }

    private static final String JSON_TITLE = "title";
    private static final String JSON_CONTENT = "content";

    public JSONObject toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JSON_TITLE, title);
        jsonObject.put(JSON_CONTENT, content);

        return jsonObject;
    }

    public static SimpleNote fromJSON(JSONObject jsonObject) throws JSONException{
        final String title = jsonObject.getString(JSON_TITLE);
        final String content = jsonObject.getString(JSON_CONTENT);

        return new SimpleNote(title, content);
    }
}
