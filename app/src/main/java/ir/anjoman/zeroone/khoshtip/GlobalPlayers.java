package ir.anjoman.zeroone.khoshtip;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GlobalPlayers {
    public static final ArrayList<String> players = new ArrayList<>();
    public static final ArrayList<PrintWriter> outputs = new ArrayList<>();
    public static final ArrayList<BufferedReader> readers = new ArrayList<>();
    public static final ArrayList<Socket> sockets = new ArrayList<>();

    private static final List<String> messageQueue =
            Collections.synchronizedList(new ArrayList<>());

    public static void addMessage(String msg) {
        messageQueue.add(msg);
    }

    public static List<String> drainMessages() {
        List<String> copy;
        synchronized (messageQueue) {
            copy = new ArrayList<>(messageQueue);
            messageQueue.clear();
        }
        return copy;
    }
}
