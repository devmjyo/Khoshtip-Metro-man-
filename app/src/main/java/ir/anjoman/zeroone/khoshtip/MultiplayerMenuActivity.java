package ir.anjoman.zeroone.khoshtip;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MultiplayerMenuActivity extends AppCompatActivity {

    Button btnCreate, btnJoin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_menu);

        btnCreate = findViewById(R.id.btnCreate);
        btnJoin = findViewById(R.id.btnJoin);

        // ساخت بازی → Host
        btnCreate.setOnClickListener(v -> {
            Intent i = new Intent(MultiplayerMenuActivity.this, HostLobbyActivity.class);
            startActivity(i);
        });

        // پیوستن به بازی → Client
        btnJoin.setOnClickListener(v -> {
            Intent i = new Intent(MultiplayerMenuActivity.this, JoinLobbyActivity.class);
            startActivity(i);
        });
    }
}
