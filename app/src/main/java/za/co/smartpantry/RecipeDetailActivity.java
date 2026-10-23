package za.co.smartpantry;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class RecipeDetailActivity extends Activity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state); setContentView(R.layout.activity_recipe_detail);
        DBHelper db = new DBHelper(this);
        int recipeId = getIntent().getIntExtra("id", -1);
        DBHelper.Recipe recipe = db.getRecipe(recipeId);
        TextView title = findViewById(R.id.txtRecipeName);
        TextView ingredients = findViewById(R.id.txtRecipeIngredients);
        TextView method = findViewById(R.id.txtRecipeMethod);

        if (recipe == null) {
            title.setText("Recipe not found");
            ingredients.setText("");
            method.setText("");
        } else {
            title.setText(recipe.name);
            StringBuilder list = new StringBuilder();
            for (DBHelper.RecipeIngredient item : recipe.ingredients) {
                list.append("• ").append(item.name).append(" — ").append(item.quantity).append(" ").append(item.unit).append("\n");
            }
            ingredients.setText(list.toString());
            method.setText(recipe.method);
        }
        findViewById(R.id.btnBackDetail).setOnClickListener(v -> finish());
    }
}


