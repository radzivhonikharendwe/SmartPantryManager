
package za.co.smartpantry;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class AddRecipeActivity extends Activity {

    private DBHelper dbHelper;

    private EditText etRecipeName;
    private EditText etRecipeMethod;
    private EditText etIngredientName;
    private EditText etIngredientQuantity;
    private EditText etIngredientUnit;

    private TextView tvIngredients;

    private ArrayList<DBHelper.RecipeIngredient> ingredients =
            new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        dbHelper = new DBHelper(this);

        etRecipeName = findViewById(R.id.etRecipeName);
        etRecipeMethod = findViewById(R.id.etRecipeMethod);
        etIngredientName = findViewById(R.id.etIngredientName);
        etIngredientQuantity = findViewById(R.id.etIngredientQuantity);
        etIngredientUnit = findViewById(R.id.etIngredientUnit);

        tvIngredients = findViewById(R.id.tvIngredients);

        Button btnAddIngredient =
                findViewById(R.id.btnAddIngredient);

        Button btnSaveRecipe =
                findViewById(R.id.btnSaveRecipe);

        btnAddIngredient.setOnClickListener(v -> addIngredient());

        btnSaveRecipe.setOnClickListener(v -> saveRecipe());
    }

    private void addIngredient() {
        String name = etIngredientName.getText().toString().trim();
        String quantityText =
                etIngredientQuantity.getText().toString().trim();
        String unit = etIngredientUnit.getText().toString().trim();

        if (name.isEmpty() || quantityText.isEmpty()
                || unit.isEmpty()) {
            Toast.makeText(this,
                    "Enter all ingredient details",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double quantity = Double.parseDouble(quantityText);

            if (!Double.isFinite(quantity) || quantity <= 0) {
                Toast.makeText(this,
                        "Enter a valid quantity",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            ingredients.add(
                    new DBHelper.RecipeIngredient(name, quantity, unit)
            );

            tvIngredients.append(
                    name + " - " + quantity + " " + unit + "\n"
            );

            etIngredientName.setText("");
            etIngredientQuantity.setText("");
            etIngredientUnit.setText("");

        } catch (NumberFormatException e) {
            Toast.makeText(this,
                    "Quantity must be a number",
                    Toast.LENGTH_SHORT).show();
        }
    }


    private void saveRecipe() {
        String name = etRecipeName.getText().toString().trim();
        String method = etRecipeMethod.getText().toString().trim();

        // Check that the recipe name and method are entered
        if (name.isEmpty() || method.isEmpty()) {
            Toast.makeText(this,
                    "Enter the recipe name and method",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that at least one ingredient has been added
        if (ingredients.isEmpty()) {
            Toast.makeText(this,
                    "Add at least one ingredient",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Save the recipe and its ingredients to the database
        long result = dbHelper.addRecipe(name, method, ingredients);

        if (result != -1) {
            Toast.makeText(this,
                    "Recipe saved successfully!",
                    Toast.LENGTH_SHORT).show();

            finish();
        } else {
            Toast.makeText(this,
                    "Could not save recipe",
                    Toast.LENGTH_SHORT).show();
        }
    }
}