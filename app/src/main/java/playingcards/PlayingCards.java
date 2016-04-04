package playingcards;

import android.os.SystemClock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class PlayingCards {

    private ArrayList<String> deck = new ArrayList<>(Arrays.asList(
            "As", "2s", "3s", "4s", "5s", "6s", "7s", "8s", "9s", "Ts", "Js", "Qs", "Ks",
            "Ah", "2h", "3h", "4h", "5h", "6h", "7h", "8h", "9h", "Th", "Jh", "Qh", "Kh",
            "Ac", "2c", "3c", "4c", "5c", "6c", "7c", "8c", "9c", "Tc", "Jc", "Qc", "Kc",
            "Ad", "2d", "3d", "4d", "5d", "6d", "7d", "8d", "9d", "Td", "Jd", "Qd", "Kd",
            "Joker Black", "Joker Red"
    ));

    public void Shuffle(){
        Random random = new Random(SystemClock.currentThreadTimeMillis());
        for (int k = 0; k < deck.size(); k++) {
            int i = random.nextInt(deck.size());
            int j = random.nextInt(deck.size());
            String mem = this.deck.get(i);
            this.deck.set(i, this.deck.get(j));
            this.deck.set(j, mem);
        }
    }

    public String Draw(){
        if (this.deck.isEmpty())
            return "";
        else
            return this.deck.remove(0);
    }
}
