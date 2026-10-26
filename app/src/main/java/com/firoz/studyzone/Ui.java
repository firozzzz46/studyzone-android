
package com.firoz.studyzone;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Context;

public final class Ui {
    private Ui() {}
    public static int color(String c) { return Color.parseColor(c); }
    public static GradientDrawable bg(String color, int radius, String stroke) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color(color)); d.setCornerRadius(radius);
        if (stroke != null) d.setStroke(2, color(stroke));
        return d;
    }
    public static TextView text(Context c, String value, int size, int color, boolean bold) {
        TextView t = new TextView(c); t.setText(value); t.setTextSize(size); t.setTextColor(color);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        t.setGravity(Gravity.CENTER_VERTICAL); t.setPadding(2, 5, 2, 5); return t;
    }
    public static Button button(Context c, String value) {
        Button b = new Button(c); b.setText(value); b.setAllCaps(false);
        b.setTextColor(color(AppConfig.DEEP_NAVY)); b.setBackground(bg(AppConfig.GOLD, 24, null));
        return b;
    }
    public static LinearLayout column(Context c) {
        LinearLayout l = new LinearLayout(c); l.setOrientation(LinearLayout.VERTICAL);
        l.setPadding(20, 18, 20, 20); l.setBackgroundColor(color(AppConfig.DEEP_NAVY)); return l;
    }
    public static LinearLayout row(Context c) {
        LinearLayout l = new LinearLayout(c); l.setOrientation(LinearLayout.HORIZONTAL);
        l.setGravity(Gravity.CENTER_VERTICAL); return l;
    }
    public static void space(LinearLayout l, int h) {
        View v = new View(l.getContext()); l.addView(v, new LinearLayout.LayoutParams(1, h));
    }
}
