package ir.anjoman.zeroone.khoshtip;

import androidx.appcompat.app.AppCompatActivity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

import org.json.JSONObject;

import java.io.PrintWriter;
import java.util.HashMap;

public class GameActivity extends AppCompatActivity {

    VideoView videoView;
    TextView txtCountdown, txtScores;
    Button btnClick;

    Handler handler = new Handler();
    int countdown = 3;

    String playerName;
    boolean isHost;
    boolean hasLost = false;

    // نگهداری امتیازها
    HashMap<String, Integer> scores = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        videoView = findViewById(R.id.videoView);
        txtCountdown = findViewById(R.id.txtCountdown);
        txtScores = findViewById(R.id.txtScores);
        btnClick = findViewById(R.id.btnClick);

        playerName = getIntent().getStringExtra("playerName");
        isHost = getIntent().getBooleanExtra("isHost", false);

        scores.put(playerName, 0);

        btnClick.setOnClickListener(v -> {
            if (!hasLost) {
                int s = scores.get(playerName) + 1;
                scores.put(playerName, s);
                updateScores();
                broadcastScore(playerName, s);
            }
        });

        startCountdown();
    }

    private void startCountdown() {
        txtCountdown.setText(String.valueOf(countdown));
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                countdown--;
                if (countdown > 0) {
                    txtCountdown.setText(String.valueOf(countdown));
                    handler.postDelayed(this, 1000);
                } else {
                    txtCountdown.setText("شروع!");
                    playVideo();
                }
            }
        }, 1000);
    }

    private void playVideo() {
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.khoshtips);
        videoView.setVideoURI(videoUri);
        videoView.start();

        videoView.setOnCompletionListener(mp -> {
            checkWinner();
        });
    }

    private void updateScores() {
        StringBuilder sb = new StringBuilder();
        for (String name : scores.keySet()) {
            sb.append(name).append(": ").append(scores.get(name)).append("\n");
        }
        txtScores.setText(sb.toString());
    }

    private void broadcastScore(String name, int score) {
        // اگر هاست هستیم، امتیاز را به کلاینت‌ها می‌فرستیم
        if (isHost) {
            new Thread(() -> {
                try {
                    JSONObject json = new JSONObject();
                    json.put("type", "score");
                    json.put("name", name);
                    json.put("score", score);

                    // متد broadcast همان hostLobby
                    HostLobbyActivity.broadcast(json.toString());
                } catch (Exception ignored) {}
            }).start();
        }
    }

    private void checkWinner() {
        String winner = null;
        int maxScore = -1;
        for (String name : scores.keySet()) {
            int s = scores.get(name);
            if (s > maxScore) {
                maxScore = s;
                winner = name;
            }
        }
        txtCountdown.setText("برنده: " + winner);
        btnClick.setEnabled(false);
    }
}
