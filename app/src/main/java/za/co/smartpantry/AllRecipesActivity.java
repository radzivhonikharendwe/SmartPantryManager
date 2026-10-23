
package za.co.smartpantry;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class AllRecipesActivity extends Activity {

    private DBHelper db;
    private ArrayList<DBHelper.Recipe> recipes;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_all_recipes);

        db = new DBHelper(this);

        ListView list = findViewById(R.id.listAllRecipes);

        // Open a recipe when the user taps it.
        list.setOnItemClickListener((parent, view, position, id) -> {
            if (recipes != null && position < recipes.size()) {
                Intent intent = new Intent(
                        AllRecipesActivity.this,
                        RecipeDetailActivity.class
                );

                intent.putExtra("id", recipes.get(position).id);
                startActivity(intent);
            }
        });

        // Return to the previous screen.
        findViewById(R.id.btnBackRecipes)
                .setOnClickListener(v -> finish());

        loadRecipes();
    }

    private void loadRecipes() {

        // Retrieve user-created recipes from SQLite.
        recipes = db.getRecipes();

        ArrayList<String> names = new ArrayList<>();

        for (DBHelper.Recipe recipe : recipes) {
            names.add("🍽  " + recipe.name);
        }

        if (names.isEmpty()) {
            names.add("No recipes yet.\nGo back and tap + Recipe to add one.");
        }

        ListView list = findViewById(R.id.listAllRecipes);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                names
        );

        list.setAdapter(adapter);

        // Prevent the empty-state message from opening a recipe.
        list.setOnItemClickListener((parent, view, position, id) -> {
            if (recipes != null && position < recipes.size()) {
                Intent intent = new Intent(
                        AllRecipesActivity.this,
                        RecipeDetailActivity.class
                );

                intent.putExtra("id", recipes.get(position).id);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (db != null) {
            loadRecipes();
        }
    }

    @Override
    protected void onDestroy() {
        if (db != null) {
            db.close();
        }

        super.onDestroy();
    }
}

