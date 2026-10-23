package za.co.smartpantry;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;

public class SuggestionsActivity extends Activity {
    private DBHelper db;
    private ArrayList<DBHelper.Recipe> matches;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state); db = new DBHelper(this); setContentView(R.layout.activity_suggestions);
        findViewById(R.id.btnAllRecipes).setOnClickListener(v -> startActivity(new Intent(this, AllRecipesActivity.class)));
        findViewById(R.id.btnNavPantry).setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        findViewById(R.id.btnNavRecipes).setOnClickListener(v -> showMatches());
        findViewById(R.id.btnNavSettings).setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
        showMatches();
    }
    @Override protected void onResume() { super.onResume(); if (db != null) showMatches(); }

    private void showMatches() {
        matches = db.getMatchingRecipes();
        TextView message = findViewById(R.id.txtSuggestionMessage);
        message.setText(matches.isEmpty()
                ? "No recipes match your pantry yet — add more ingredients or check your quantities."
                : matches.size() + " recipe(s) match your pantry.");
        ArrayList<String> names = new ArrayList<>();
        for (DBHelper.Recipe r : matches) names.add(r.name + "\n" + r.ingredients.size() + " required ingredients");
        ListView list = findViewById(R.id.listSuggestions);
        list.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names));
        list.setOnItemClickListener((parent, view, position, id) -> {
            Intent i = new Intent(this, RecipeDetailActivity.class);
            i.putExtra("id", matches.get(position).id); startActivity(i);
        });
    }
}