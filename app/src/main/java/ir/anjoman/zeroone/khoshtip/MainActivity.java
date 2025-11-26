package ir.anjoman.zeroone.khoshtip;

import androidx.appcompat.app.AppCompatActivity;
import android.net.Uri;
import android.os.Bundle;
import android.media.MediaPlayer;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {
    final int[] clickCount = {0};
    final long[] lastClickTime = {0};
    VideoView videoView;
    Button btnPlay;
    Uri videoUri;

    Handler handler = new Handler();
    long chunkTime = 1000;

    int currentPosition = 0; // محل فعلی ویدیو
    final Runnable noClickRunnable = new Runnable() {
        @Override
        public void run() {
            btnPlay.setText("بزن خوشتیپپپپپپ");
            videoView.pause();
            // اگر بعد از 1 ثانیه کلیک جدیدی نشد
            clickCount[0] = 0; // شمارش کلیک‌ها ریست می‌شه
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        TextView txtt = findViewById(R.id.textt);
        videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.khoshtip2);
        videoView = findViewById(R.id.videoView);
        btnPlay = findViewById(R.id.btnPlay);
        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.khoshtips));
        txtt.setText("READY");
        videoView.start();


        // وقتی ویدیو تمام شد دوباره از اول
        videoView.setOnCompletionListener(mp -> {
            txtt.setText("GO");
            if (!btnPlay.isEnabled())btnPlay.setEnabled(true);
            //videoView.start();
        });

        btnPlay.setOnClickListener(v ->{
            long now = System.currentTimeMillis();
            if (now - lastClickTime[0] < 500) { // اگر کمتر از 0.5 ثانیه بین کلیک‌ها باشه
                clickCount[0]++;
            } else {
                clickCount[0] = 1;
            }
            lastClickTime[0] = now;

            if (clickCount[0] >= 3) { // مثلا بعد از 3 کلیک سریع;

                videoView.start();
            }
            handler.removeCallbacks(noClickRunnable);
            handler.postDelayed(noClickRunnable, 500); // اگر 1 ثانیه کلیک نشد
            if (!videoView.isPlaying()) playNextChunk();
        });
    }

    private void playNextChunk() {


        videoView.stopPlayback();
        videoView.setVideoURI(videoUri);

        videoView.setOnPreparedListener(mp -> {

            // اگر از ویدیو جلوتر زدیم → برگرد اول
            if (currentPosition >= mp.getDuration()) {
                currentPosition = 0;
            }

            videoView.seekTo(currentPosition);
            videoView.start();
            handler.postDelayed(() -> {
                if (videoView.isPlaying()) {
                    videoView.pause();
                    currentPosition += chunkTime; // برو به قطعه بعدی
                }
            }, chunkTime);

        });
    }
}