package ir.anjoman.zeroone.khoshtip;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Enumeration;

public class HostLobbyActivity extends AppCompatActivity {

    TextView txtIP;
    EditText edtName;
    ListView listPlayers;
    Button btnStart;
    Button btnSetName;

    ArrayList<String> players = new ArrayList<>();
    ArrayAdapter<String> adapter;

    ServerSocket serverSocket;
    int PORT = 5050;
    static ArrayList<PrintWriter> clientOutputs = new ArrayList<>();

    boolean hostNameSet = false; // جلوگیری از دوباره اضافه شدن هاست

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_lobby);

        txtIP = findViewById(R.id.txtIP);
        edtName = findViewById(R.id.edtName);
        listPlayers = findViewById(R.id.listPlayers);
        btnStart = findViewById(R.id.btnStart);
        btnSetName = findViewById(R.id.btnSetName);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, players);
        listPlayers.setAdapter(adapter);

        btnStart.setEnabled(false);

        startServer();
        showIPAddress();

        // ثبت نام هاست
        btnSetName.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, "نام را وارد کنید", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!hostNameSet) {
                hostNameSet = true;
                addPlayer(name);            // هاست خودش وارد لیست می‌شود
                btnStart.setEnabled(true);  // دکمه شروع فعال می‌شود
                edtName.setEnabled(false);
                btnSetName.setEnabled(false);
                Toast.makeText(this, "نام شما ثبت شد", Toast.LENGTH_SHORT).show();
            }
        });

        // دکمه شروع بازی
        btnStart.setOnClickListener(v -> {
            new Thread(() -> sendStartSignal()).start();
        });
    }

    // گرفتن آی‌پی واقعی گوشی
    private void showIPAddress() {
        try {
            String ip = null;

            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        ip = inetAddress.getHostAddress();
                    }
                }
            }

            if (ip == null) ip = "IP پیدا نشد";
            txtIP.setText("IP میزبان: " + ip);

        } catch (Exception e) {
            txtIP.setText("IP پیدا نشد");
        }
    }

    // سرور
    private void startServer() {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(PORT);

                while (true) {
                    Socket client = serverSocket.accept();
                    PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                    clientOutputs.add(out);

                    BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

                    new Thread(() -> {
                        try {
                            String line;
                            while ((line = in.readLine()) != null) {
                                handleClientMessage(line);
                            }
                        } catch (Exception ignored) {}
                    }).start();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // پردازش پیام کلاینت
    private void handleClientMessage(String msg) {
        try {
            JSONObject json = new JSONObject(msg);
            String type = json.getString("type");

            if (type.equals("join")) {
                String name = json.getString("name");
                runOnUiThread(() -> addPlayer(name));
            }

        } catch (Exception ignored) {}
    }

    private void addPlayer(String name) {
        if (!players.contains(name)) {
            players.add(name);
            adapter.notifyDataSetChanged();
        }
    }

    // ارسال سیگنال شروع بازی
    private void sendStartSignal() {
        try {
            JSONObject json = new JSONObject();
            json.put("type", "start");

            broadcast(json.toString());  // ارسال به همه کلاینت‌ها

            // هاست خودش وارد بازی می‌شود
            runOnUiThread(() -> {
                Intent i = new Intent(HostLobbyActivity.this, GameActivity.class);
                i.putExtra("isHost", true);
                startActivity(i);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // پخش پیام به همه کلاینت‌ها (در Thread جدا)
    static void broadcast(String msg) {
        new Thread(() -> {
            for (PrintWriter out : clientOutputs) {
                try {
                    out.println(msg);
                    out.flush();
                } catch (Exception ignored) {}
            }
        }).start();
    }
}
