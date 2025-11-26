package ir.anjoman.zeroone.khoshtip;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button btnSingle, btnMulti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSingle = findViewById(R.id.btnSingle);
        btnMulti = findViewById(R.id.btnMulti);

        // تک نفره
        btnSingle.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, SinglePlayerActivity.class);
            startActivity(i);
        });

        // چند نفره
        btnMulti.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, MultiplayerMenuActivity.class);
            startActivity(i);
        });
    }
}
