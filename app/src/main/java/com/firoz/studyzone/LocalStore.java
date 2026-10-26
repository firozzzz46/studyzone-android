
package com.firoz.studyzone;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public final class LocalStore {
    private static final String PREFS = "study_zone_local";
    private final SharedPreferences prefs;
    public LocalStore(Context context) { prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE); }
    public boolean isDark() { return prefs.getBoolean("dark", true); }
    public void setDark(boolean dark) { prefs.edit().putBoolean("dark", dark).apply(); }
    public void addFavorite(String title, String url) {
        List<String> items = getFavorites();
        for (String item : items) {
            try { if (new JSONObject(item).optString("url").equals(url)) return; } catch (Exception ignored) {}
        }
        JSONObject obj = new JSONObject();
        try { obj.put("title", title); obj.put("url", url); } catch (JSONException ignored) {}
        items.add(obj.toString());
        saveFavorites(items);
    }
    public List<String> getFavorites() {
        List<String> list = new ArrayList<>();
        String raw = prefs.getString("favorites", "[]");
        try {
            JSONArray arr = new JSONArray(raw);
            for (int i=0; i<arr.length(); i++) list.add(arr.getString(i));
        } catch (Exception ignored) {}
        return list;
    }
    public void removeFavorite(int index) {
        List<String> list = getFavorites();
        if (index >= 0 && index < list.size()) { list.remove(index); saveFavorites(list); }
    }
    private void saveFavorites(List<String> items) {
        JSONArray arr = new JSONArray();
        for (String item : items) arr.put(item);
        prefs.edit().putString("favorites", arr.toString()).apply();
    }
    public void clearAppData() {
        boolean dark = isDark();
        prefs.edit().clear().putBoolean("dark", dark).apply();
    }
}
