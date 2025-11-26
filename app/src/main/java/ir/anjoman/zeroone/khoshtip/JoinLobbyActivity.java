package ir.anjoman.zeroone.khoshtip;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class JoinLobbyActivity extends AppCompatActivity {

    EditText edtName, edtHostIP;
    Button btnJoin;

    Socket socket;
    PrintWriter out;
    BufferedReader in;
    int PORT = 5050;

    String playerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_lobby);

        edtName = findViewById(R.id.edtName);
        edtHostIP = findViewById(R.id.edtHostIP);
        btnJoin = findViewById(R.id.btnJoin);

        btnJoin.setOnClickListener(v -> {
            playerName = edtName.getText().toString().trim();
            String hostIP = edtHostIP.getText().toString().trim();

            if (playerName.isEmpty() || hostIP.isEmpty()) {
                Toast.makeText(this, "نام و IP میزبان را وارد کنید", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> joinServer(hostIP)).start();
        });
    }

    private void joinServer(String hostIP) {
        try {
            socket = new Socket(hostIP, PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // ارسال پیام join
            JSONObject json = new JSONObject();
            json.put("type", "join");
            json.put("name", playerName);
            out.println(json.toString());
            out.flush();

            // گوش دادن به پیام‌ها
            String line;
            while ((line = in.readLine()) != null) {
                JSONObject msg = new JSONObject(line);
                String type = msg.getString("type");

                if (type.equals("start")) {
                    runOnUiThread(() -> {
                        Intent i = new Intent(JoinLobbyActivity.this, GameActivity.class);
                        i.putExtra("isHost", false);
                        startActivity(i);
                    });
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            runOnUiThread(() ->
                    Toast.makeText(this, "اتصال به هاست موفق نبود", Toast.LENGTH_SHORT).show());
        }
    }
}
