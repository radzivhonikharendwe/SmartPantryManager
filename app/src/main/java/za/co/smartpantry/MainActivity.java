package za.co.smartpantry;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import java.util.ArrayList;

public class MainActivity extends Activity {
    private DBHelper db;
    private ArrayList<DBHelper.PantryItem> items;
    private Button btnAddRecipe;
    private ArrayAdapter<String> adapter;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        db = new DBHelper(this);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btnAddIngredient).setOnClickListener(v -> startActivity(new Intent(this, IngredientActivity.class)));
        findViewById(R.id.btnSuggestions).setOnClickListener(v -> startActivity(new Intent(this, SuggestionsActivity.class)));
        findViewById(R.id.btnNavPantry).setOnClickListener(v -> refreshList());
        findViewById(R.id.btnNavRecipes).setOnClickListener(v -> startActivity(new Intent(this, SuggestionsActivity.class)));
        findViewById(R.id.btnNavSettings).setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
        ListView list = findViewById(R.id.listPantry);
        list.setOnItemClickListener((parent, view, position, id) -> showItemOptions(items.get(position)));
        refreshList();
        btnAddRecipe = findViewById(R.id.btnAddRecipe);

        btnAddRecipe.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddRecipeActivity.class);
            startActivity(intent);
        });
    }
    @Override protected void onResume() { super.onResume(); if (db != null) refreshList(); }

    private void refreshList() {
        items = db.getPantry();
        ArrayList<String> labels = new ArrayList<>();
        for (DBHelper.PantryItem p : items) {
            String quantityText = (p.quantity == Math.floor(p.quantity)) ? String.valueOf((long) p.quantity) : String.valueOf(p.quantity);
            String line = "🥗  " + p.name + "  •  " + quantityText + " " + p.unit;
            if (!p.expiry.isEmpty()) line += "\nExpiry: " + p.expiry;
            labels.add(line + "\nTap to edit or delete");
        }
        if (labels.isEmpty()) labels.add("Your pantry is empty.\nTap Add ingredient to get started.");
        ListView list = findViewById(R.id.listPantry);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, labels);
        list.setAdapter(adapter);
        list.setOnItemClickListener((parent, view, position, id) -> {
            if (!items.isEmpty()) showItemOptions(items.get(position));
        });
    }
    private void showItemOptions(DBHelper.PantryItem item) {
        new AlertDialog.Builder(this).setTitle(item.name).setItems(new String[]{"Edit", "Delete", "Cancel"}, (dialog, which) -> {
            if (which == 0) {
                Intent i = new Intent(this, IngredientActivity.class);
                i.putExtra("id", item.id); i.putExtra("name", item.name); i.putExtra("quantity", item.quantity);
                i.putExtra("unit", item.unit); i.putExtra("expiry", item.expiry); startActivity(i);
            } else if (which == 1) {
                new AlertDialog.Builder(this).setTitle("Delete ingredient?").setMessage("Remove " + item.name + "?")
                        .setNegativeButton("Cancel", null).setPositiveButton("Delete", (d,w) -> { db.deletePantry(item.id); refreshList(); }).show();
            }
        }).show();
    }
}
