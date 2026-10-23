package za.co.smartpantry;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Ui {

    public static final int GREEN = Color.rgb(39, 105, 76);
    public static final int BG = Color.rgb(246, 248, 245);

    // Creates a screen with a title and background colour.
    public static LinearLayout page(Activity a, String title) {
        LinearLayout root = new LinearLayout(a);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(18, 18, 18, 12);
        root.setBackgroundColor(BG);

        TextView h = new TextView(a);
        h.setText(title);
        h.setTextSize(25);
        h.setTextColor(GREEN);
        h.setTypeface(null, 1);
        h.setPadding(0, 8, 0, 16);

        root.addView(h);
        return root;
    }

    // Creates a green button.
    public static Button button(Activity a, String label) {
        Button b = new Button(a);
        b.setText(label);
        b.setAllCaps(false);
        b.setTextColor(Color.WHITE);
        b.setBackgroundTintList(ColorStateList.valueOf(GREEN));
        return b;
    }

    // Creates a text label.
    public static TextView text(Activity a, String s, int size) {
        TextView t = new TextView(a);
        t.setText(s);
        t.setTextSize(size);
        t.setTextColor(Color.DKGRAY);
        t.setPadding(8, 8, 8, 8);
        return t;
    }

    // Creates a text input field.
    public static EditText edit(Activity a, String hint) {
        EditText e = new EditText(a);
        e.setSingleLine(true);
        e.setHint(hint);
        e.setPadding(12, 8, 12, 8);
        return e;
    }

    // Adds navigation buttons to the bottom of a screen.
    public static void addNav(Activity a, LinearLayout root) {
        LinearLayout nav = new LinearLayout(a);
        nav.setOrientation(LinearLayout.HORIZONTAL);

        String[] labels = {"Pantry", "Recipes", "Settings"};

        for (String s : labels) {
            Button b = button(a, s);
            b.setTextSize(12);

            nav.addView(
                    b,
                    new LinearLayout.LayoutParams(0, -2, 1)
            );

            b.setOnClickListener(v -> {
                if (s.equals("Pantry")) {
                    a.startActivity(
                            new Intent(a, MainActivity.class)
                    );
                } else if (s.equals("Recipes")) {
                    a.startActivity(
                            new Intent(a, SuggestionsActivity.class)
                    );
                } else {
                    a.startActivity(
                            new Intent(a, SettingsActivity.class)
                    );
                }
            });
        }

        root.addView(nav);
    }
}

