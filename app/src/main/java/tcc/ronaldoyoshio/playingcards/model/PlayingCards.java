package tcc.ronaldoyoshio.playingcards.model;

import android.os.SystemClock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class PlayingCards {

    private ArrayList<Card> deck = new ArrayList<>(Arrays.asList(
            new Card("As"), new Card("2s"), new Card("3s"), new Card("4s"), new Card("5s"),
            new Card("6s"), new Card("7s"), new Card("8s"), new Card("9s"), new Card("Ts"),
            new Card("Js"), new Card("Qs"), new Card("Ks"), new Card("Ah"), new Card("2h"),
            new Card("3h"), new Card("4h"), new Card("5h"), new Card("6h"), new Card("7h"),
            new Card("8h"), new Card("9h"), new Card("Th"), new Card("Jh"), new Card("Qh"),
            new Card("Kh"), new Card("Ac"), new Card("2c"), new Card("3c"), new Card("4c"),
            new Card("5c"), new Card("6c"), new Card("7c"), new Card("8c"), new Card("9c"),
            new Card("Tc"), new Card("Jc"), new Card("Qc"), new Card("Kc"), new Card("Ad"),
            new Card("2d"), new Card("3d"), new Card("4d"), new Card("5d"), new Card("6d"),
            new Card("7d"), new Card("8d"), new Card("9d"), new Card("Td"), new Card("Jd"),
            new Card("Qd"), new Card("Kd"), new Card("Joker Black"), new Card("Joker Red")
    ));

    public void Shuffle(){
        Random random = new Random(SystemClock.currentThreadTimeMillis());
        for (int k = 0; k < deck.size(); k++) {
            int i = random.nextInt(deck.size());
            int j = random.nextInt(deck.size());
            Card mem = this.deck.get(i);
            this.deck.set(i, this.deck.get(j));
            this.deck.set(j, mem);
        }
    }

    public Card Draw(){
        return deck.remove(0);
    }

    public boolean isEmpty() {
        return deck.isEmpty();
    }
}
