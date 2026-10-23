package za.co.smartpantry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Locale;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "smart_pantry.db";
    private static final int DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // Create all database tables.
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "CREATE TABLE pantry (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL, " +
                        "quantity REAL NOT NULL, " +
                        "unit TEXT NOT NULL, " +
                        "expiry TEXT)"
        );

        db.execSQL(
                "CREATE TABLE recipes (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL, " +
                        "method TEXT NOT NULL)"
        );

        db.execSQL(
                "CREATE TABLE recipe_ingredients (" +
                        "recipe_id INTEGER NOT NULL, " +
                        "name TEXT NOT NULL, " +
                        "quantity REAL NOT NULL, " +
                        "unit TEXT NOT NULL, " +
                        "FOREIGN KEY(recipe_id) REFERENCES recipes(id) " +
                        "ON DELETE CASCADE)"
        );
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onUpgrade(
            SQLiteDatabase db, int oldVersion, int newVersion) {
        // Add migration code here if the database structure changes.
    }

    // ==================== PANTRY ====================

    // Get all pantry items.
    public ArrayList<PantryItem> getPantry() {

        ArrayList<PantryItem> result = new ArrayList<>();

        Cursor c = getReadableDatabase().rawQuery(
                "SELECT id, name, quantity, unit, expiry " +
                        "FROM pantry ORDER BY name COLLATE NOCASE",
                null
        );

        try {
            while (c.moveToNext()) {
                result.add(new PantryItem(
                        c.getInt(0),
                        c.getString(1),
                        c.getDouble(2),
                        c.getString(3),
                        c.getString(4)
                ));
            }
        } finally {
            c.close();
        }

        return result;
    }

    // Add a new pantry item or update an existing one.
    public long savePantry(
            int id,
            String name,
            double quantity,
            String unit,
            String expiry) {

        if (name == null || name.trim().isEmpty()
                || unit == null || unit.trim().isEmpty()
                || !Double.isFinite(quantity)
                || quantity <= 0) {
            return -1;
        }

        ContentValues values = new ContentValues();
        values.put("name", name.trim());
        values.put("quantity", quantity);
        values.put("unit", unit.trim());
        values.put("expiry", expiry == null ? "" : expiry.trim());

        SQLiteDatabase db = getWritableDatabase();

        if (id < 0) {
            return db.insert("pantry", null, values);
        }

        int updated = db.update(
                "pantry",
                values,
                "id=?",
                new String[]{String.valueOf(id)}
        );

        return updated > 0 ? id : -1;
    }

    // Delete a pantry item.
    public void deletePantry(int id) {
        getWritableDatabase().delete(
                "pantry",
                "id=?",
                new String[]{String.valueOf(id)}
        );
    }

    // ==================== RECIPES ====================

    // Get every saved recipe and its required ingredients.
    public ArrayList<Recipe> getRecipes() {

        ArrayList<Recipe> result = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT id, name, method FROM recipes " +
                        "ORDER BY name COLLATE NOCASE",
                null
        );

        try {
            while (c.moveToNext()) {

                Recipe recipe = new Recipe(
                        c.getInt(0),
                        c.getString(1),
                        c.getString(2)
                );

                Cursor ingredients = db.rawQuery(
                        "SELECT name, quantity, unit " +
                                "FROM recipe_ingredients WHERE recipe_id=?",
                        new String[]{String.valueOf(recipe.id)}
                );

                try {
                    while (ingredients.moveToNext()) {
                        recipe.ingredients.add(new RecipeIngredient(
                                ingredients.getString(0),
                                ingredients.getDouble(1),
                                ingredients.getString(2)
                        ));
                    }
                } finally {
                    ingredients.close();
                }

                result.add(recipe);
            }
        } finally {
            c.close();
        }

        return result;
    }

    // Save a recipe and its ingredients in one transaction.
    public long addRecipe(
            String name,
            String method,
            ArrayList<RecipeIngredient> ingredients) {

        if (name == null || name.trim().isEmpty()
                || method == null || method.trim().isEmpty()
                || ingredients == null || ingredients.isEmpty()) {
            return -1;
        }

        // Validate all ingredients before saving anything.
        for (RecipeIngredient item : ingredients) {
            if (item == null
                    || item.name == null || item.name.trim().isEmpty()
                    || item.unit == null || item.unit.trim().isEmpty()
                    || !Double.isFinite(item.quantity)
                    || item.quantity <= 0) {
                return -1;
            }
        }

        SQLiteDatabase db = getWritableDatabase();
        long recipeId = -1;

        db.beginTransaction();

        try {
            ContentValues recipeValues = new ContentValues();
            recipeValues.put("name", name.trim());
            recipeValues.put("method", method.trim());

            recipeId = db.insert("recipes", null, recipeValues);

            if (recipeId == -1) {
                return -1;
            }

            for (RecipeIngredient item : ingredients) {

                ContentValues ingredientValues = new ContentValues();
                ingredientValues.put("recipe_id", recipeId);
                ingredientValues.put("name", item.name.trim());
                ingredientValues.put("quantity", item.quantity);
                ingredientValues.put("unit", item.unit.trim());

                long ingredientId = db.insert(
                        "recipe_ingredients",
                        null,
                        ingredientValues
                );

                if (ingredientId == -1) {
                    return -1;
                }
            }

            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
        }

        return recipeId;
    }

    // Delete a recipe and its ingredients.
    public void deleteRecipe(int id) {

        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        try {
            db.delete(
                    "recipe_ingredients",
                    "recipe_id=?",
                    new String[]{String.valueOf(id)}
            );

            db.delete(
                    "recipes",
                    "id=?",
                    new String[]{String.valueOf(id)}
            );

            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
        }
    }

    // Get one recipe using its ID.
    public Recipe getRecipe(int id) {

        for (Recipe recipe : getRecipes()) {
            if (recipe.id == id) {
                return recipe;
            }
        }

        return null;
    }

    // ==================== RECIPE SUGGESTIONS ====================

    // Only return recipes for which every required ingredient
    // and its required quantity are available in the pantry.
    public ArrayList<Recipe> getMatchingRecipes() {

        ArrayList<PantryItem> pantry = getPantry();
        ArrayList<Recipe> matches = new ArrayList<>();

        for (Recipe recipe : getRecipes()) {

            // Do not suggest recipes without ingredients.
            if (recipe.ingredients.isEmpty()) {
                continue;
            }

            boolean allAvailable = true;

            for (RecipeIngredient required : recipe.ingredients) {

                if (required.name == null
                        || required.unit == null
                        || required.quantity <= 0) {
                    allAvailable = false;
                    break;
                }

                String requiredName =
                        normalizeIngredient(required.name);

                double availableQuantity = 0;

                for (PantryItem item : pantry) {

                    if (normalizeIngredient(item.name).equals(requiredName)
                            && unitsCompatible(item.unit, required.unit)) {

                        availableQuantity += convertToBaseQuantity(
                                item.quantity, item.unit
                        );
                    }
                }

                double neededQuantity = convertToBaseQuantity(
                        required.quantity, required.unit
                );

                if (availableQuantity + 0.000001 < neededQuantity) {
                    allAvailable = false;
                    break;
                }
            }

            if (allAvailable) {
                matches.add(recipe);
            }
        }

        return matches;
    }

    // Normalize common ingredient names and plurals.
    private String normalizeIngredient(String value) {

        if (value == null) {
            return "";
        }

        String name = value.trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ");

        // Common irregular plurals.
        if (name.equals("tomatoes")) return "tomato";
        if (name.equals("potatoes")) return "potato";
        if (name.equals("leaves")) return "leaf";
        if (name.equals("loaves")) return "loaf";
        if (name.equals("chillies")) return "chilli";
        if (name.equals("chilies")) return "chili";

        // Examples: onions -> onion, carrots -> carrot.
        if (name.endsWith("ies") && name.length() > 3) {
            return name.substring(0, name.length() - 3) + "y";
        }

        if (name.endsWith("oes") && name.length() > 3) {
            return name.substring(0, name.length() - 2);
        }

        if (name.endsWith("s") && !name.endsWith("ss")
                && name.length() > 3) {
            return name.substring(0, name.length() - 1);
        }

        return name;
    }

    // Normalize equivalent unit names.
    private String normalizeUnit(String unit) {

        if (unit == null) {
            return "";
        }

        String u = unit.trim().toLowerCase(Locale.ROOT);

        switch (u) {

            case "gram":
            case "grams":
            case "g":
                return "g";

            case "kilogram":
            case "kilograms":
            case "kg":
                return "kg";

            case "millilitre":
            case "millilitres":
            case "milliliter":
            case "milliliters":
            case "ml":
                return "ml";

            case "litre":
            case "litres":
            case "liter":
            case "liters":
            case "l":
                return "l";

            case "piece":
            case "pieces":
            case "pc":
            case "pcs":
                return "piece";

            case "slice":
            case "slices":
                return "slice";

            case "teaspoon":
            case "teaspoons":
            case "tsp":
                return "tsp";

            case "tablespoon":
            case "tablespoons":
            case "tbsp":
                return "tbsp";

            default:
                return u;
        }
    }

    // Check whether two units can be compared.
    private boolean unitsCompatible(
            String pantryUnit,
            String recipeUnit) {

        String a = normalizeUnit(pantryUnit);
        String b = normalizeUnit(recipeUnit);

        if (a.equals(b)) {
            return true;
        }

        boolean bothMassUnits =
                (a.equals("g") || a.equals("kg"))
                        && (b.equals("g") || b.equals("kg"));

        boolean bothVolumeUnits =
                (a.equals("ml") || a.equals("l"))
                        && (b.equals("ml") || b.equals("l"));

        return bothMassUnits || bothVolumeUnits;
    }

    // Convert kilograms to grams and litres to millilitres.
    private double convertToBaseQuantity(
            double quantity,
            String unit) {

        String u = normalizeUnit(unit);

        switch (u) {
            case "kg":
            case "l":
                return quantity * 1000.0;

            default:
                return quantity;
        }
    }

    // ==================== DATA MODELS ====================

    public static class PantryItem {

        public int id;
        public String name;
        public String unit;
        public String expiry;
        public double quantity;

        public PantryItem(
                int id,
                String name,
                double quantity,
                String unit,
                String expiry) {

            this.id = id;
            this.name = name;
            this.quantity = quantity;
            this.unit = unit;
            this.expiry = expiry == null ? "" : expiry;
        }
    }

    public static class RecipeIngredient {

        public String name;
        public String unit;
        public double quantity;

        public RecipeIngredient(
                String name,
                double quantity,
                String unit) {

            this.name = name;
            this.quantity = quantity;
            this.unit = unit;
        }
    }

    public static class Recipe {

        public int id;
        public String name;
        public String method;

        public ArrayList<RecipeIngredient> ingredients =
                new ArrayList<>();

        public Recipe(int id, String name, String method) {
            this.id = id;
            this.name = name;
            this.method = method;
        }
    }
}
