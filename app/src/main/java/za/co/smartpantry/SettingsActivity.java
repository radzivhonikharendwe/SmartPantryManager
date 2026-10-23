package za.co.smartpantry;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;

public class SettingsActivity extends Activity {
    private SharedPreferences preferences;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state); setContentView(R.layout.activity_settings);
        preferences = getPreferences(MODE_PRIVATE);
        CheckBox alerts = findViewById(R.id.checkExpiryAlerts);
        alerts.setChecked(preferences.getBoolean("expiry_alerts", true));
        alerts.setOnCheckedChangeListener((button, checked) -> preferences.edit().putBoolean("expiry_alerts", checked).apply());

        findViewById(R.id.btnBackSettings).setOnClickListener(v -> finish());
        findViewById(R.id.btnNavPantry).setOnClickListener(v -> startActivity(new android.content.Intent(this, MainActivity.class)));
        findViewById(R.id.btnNavRecipes).setOnClickListener(v -> startActivity(new android.content.Intent(this, SuggestionsActivity.class)));
        findViewById(R.id.btnNavSettings).setOnClickListener(v -> { });
    }
}
