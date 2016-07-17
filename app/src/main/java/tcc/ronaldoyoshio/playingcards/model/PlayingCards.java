package tcc.ronaldoyoshio.playingcards.model;

import android.os.SystemClock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class PlayingCards extends ArrayList<String> {
    public PlayingCards() {
        super(Arrays.asList(
                "As", "2s", "3s", "4s", "5s", "6s", "7s", "8s", "9s", "Ts", "Js", "Qs", "Ks",
                "Ah", "2h", "3h", "4h", "5h", "6h", "7h", "8h", "9h", "Th", "Jh", "Qh", "Kh",
                "Ac", "2c", "3c", "4c", "5c", "6c", "7c", "8c", "9c", "Tc", "Jc", "Qc", "Kc",
                "Ad", "2d", "3d", "4d", "5d", "6d", "7d", "8d", "9d", "Td", "Jd", "Qd", "Kd",
                "Joker Black", "Joker Red"
        ));
    }

    public void shuffle(){
        Random random = new Random(SystemClock.currentThreadTimeMillis());
        for (int k = 0; k < size(); k++) {
            int i = random.nextInt(size());
            int j = random.nextInt(size());
            String mem = get(i);
            set(i, get(j));
            set(j, mem);
        }
    }
}