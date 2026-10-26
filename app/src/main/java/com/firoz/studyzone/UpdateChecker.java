
package com.firoz.studyzone;

import android.os.AsyncTask;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.URL;

public final class UpdateChecker {
    public interface Callback { void onResult(String version, String notes, String apkUrl); }
    private UpdateChecker() {}
    public static void check(Callback callback) {
        if (AppConfig.UPDATE_METADATA_URL == null || AppConfig.UPDATE_METADATA_URL.trim().isEmpty()) {
            callback.onResult("", "", ""); return;
        }
        new AsyncTask<Void, Void, JSONObject>() {
            protected JSONObject doInBackground(Void... unused) {
                HttpURLConnection conn = null;
                try {
                    URL u = new URL(AppConfig.UPDATE_METADATA_URL);
                    if (!"https".equalsIgnoreCase(u.getProtocol())) return null;
                    conn = (HttpURLConnection) u.openConnection();
                    conn.setConnectTimeout(5000); conn.setReadTimeout(5000);
                    conn.setRequestMethod("GET");
                    if (conn.getResponseCode() != 200) return null;
                    java.io.InputStream input = conn.getInputStream();
                    java.io.ByteArrayOutputStream output = new java.io.ByteArrayOutputStream();
                    byte[] buffer = new byte[4096]; int count;
                    while ((count = input.read(buffer)) != -1) output.write(buffer, 0, count);
                    input.close();
                    return new JSONObject(new String(output.toByteArray(), java.nio.charset.StandardCharsets.UTF_8));
                } catch (Exception e) { return null; }
                finally { if (conn != null) conn.disconnect(); }
            }
            protected void onPostExecute(JSONObject obj) {
                if (obj == null) { callback.onResult("", "", ""); return; }
                String version = obj.optString("version", "");
                String notes = obj.optString("notes", "");
                String apk = obj.optString("apkUrl", "");
                if (!apk.startsWith("https://") || version.isEmpty()) callback.onResult("", "", "");
                else callback.onResult(version, notes, apk);
            }
        }.execute();
    }
}
