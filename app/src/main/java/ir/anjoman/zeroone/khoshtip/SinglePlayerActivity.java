package ir.anjoman.zeroone.khoshtip;

import androidx.appcompat.app.AppCompatActivity;
import android.net.Uri;
import android.os.Bundle;
import android.media.MediaPlayer;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

public class SinglePlayerActivity extends AppCompatActivity {

    final int[] clickCount = {0};
    final long[] lastClickTime = {0};
    VideoView videoView;
    Button btnPlay;
    Uri videoUri;

    Handler handler = new Handler();
    long chunkTime = 1000;

    int currentPosition = 0;

    final Runnable noClickRunnable = new Runnable() {
        @Override
        public void run() {
            btnPlay.setText("بزن خوشتیپپپپپپ");
            videoView.pause();
            clickCount[0] = 0;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_player);

        TextView txtt = findViewById(R.id.textt);
        videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.khoshtip2);
        videoView = findViewById(R.id.videoView);
        btnPlay = findViewById(R.id.btnPlay);

        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.khoshtips));
        txtt.setText("READY");
        videoView.start();

        videoView.setOnCompletionListener(mp -> {
            txtt.setText("GO");
            if (!btnPlay.isEnabled()) btnPlay.setEnabled(true);
        });

        btnPlay.setOnClickListener(v -> {
            long now = System.currentTimeMillis();
            if (now - lastClickTime[0] < 500) {
                clickCount[0]++;
            } else {
                clickCount[0] = 1;
            }
            lastClickTime[0] = now;

            if (clickCount[0] >= 3) {
                videoView.start();
            }

            handler.removeCallbacks(noClickRunnable);
            handler.postDelayed(noClickRunnable, 500);

            if (!videoView.isPlaying()) playNextChunk();
        });
    }

    private void playNextChunk() {

        videoView.stopPlayback();
        videoView.setVideoURI(videoUri);

        videoView.setOnPreparedListener(mp -> {

            if (currentPosition >= mp.getDuration()) {
                currentPosition = 0;
            }

            videoView.seekTo(currentPosition);
            videoView.start();
            handler.postDelayed(() -> {
                if (videoView.isPlaying()) {
                    videoView.pause();
                    currentPosition += chunkTime;
                }
            }, chunkTime);

        });
    }
}
