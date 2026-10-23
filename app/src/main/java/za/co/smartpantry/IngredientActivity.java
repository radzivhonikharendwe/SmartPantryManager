package za.co.smartpantry;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class IngredientActivity extends Activity {
    private DBHelper db;
    private int itemId = -1;
    private EditText name, quantity, unit, expiry;


    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        db = new DBHelper(this);
        setContentView(R.layout.activity_ingredient);
        name = findViewById(R.id.editName);
        quantity = findViewById(R.id.editQuantity);
        unit = findViewById(R.id.editUnit);
        expiry = findViewById(R.id.editExpiry);
        TextView title = findViewById(R.id.txtIngredientTitle);


        if (getIntent().hasExtra("id")) {
            itemId = getIntent().getIntExtra("id", -1);
            title.setText("Edit Ingredient");
            name.setText(getIntent().getStringExtra("name"));
            double savedQuantity = getIntent().getDoubleExtra("quantity", 0);
            quantity.setText(savedQuantity == Math.floor(savedQuantity) ? String.valueOf((long) savedQuantity) : String.valueOf(savedQuantity));
            unit.setText(getIntent().getStringExtra("unit"));
            expiry.setText(getIntent().getStringExtra("expiry"));
        }

        findViewById(R.id.btnSaveIngredient).setOnClickListener(v -> saveIngredient());
        findViewById(R.id.btnCancelIngredient).setOnClickListener(v -> finish());

    }

    private void saveIngredient() {
        String n = name.getText().toString().trim();
        String q = quantity.getText().toString().trim();
        String u = unit.getText().toString().trim();
        String e = expiry.getText().toString().trim();

        if (n.isEmpty()) { name.setError("Enter an ingredient name"); return; }
        if (q.isEmpty()) { quantity.setError("Enter a quantity"); return; }
        if (u.isEmpty()) { unit.setError("Enter a unit"); return; }
        double amount;
        try { amount = Double.parseDouble(q); if (amount <= 0) throw new NumberFormatException(); }
        catch (NumberFormatException ex) { quantity.setError("Quantity must be greater than zero"); return; }
        if (!e.isEmpty() && !e.matches("\\d{4}-\\d{2}-\\d{2}")) { expiry.setError("Use YYYY-MM-DD"); return; }

        long result = db.savePantry(itemId, n, amount, u, e);
        if (result >= 0) { Toast.makeText(this, "Ingredient saved", Toast.LENGTH_SHORT).show(); finish(); }
        else Toast.makeText(this, "Could not save ingredient", Toast.LENGTH_LONG).show();
    }
}
