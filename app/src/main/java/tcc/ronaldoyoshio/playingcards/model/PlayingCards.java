package tcc.ronaldoyoshio.playingcards.model;

import java.util.ArrayList;
import java.util.Collections;

public class PlayingCards extends ArrayList<String> {
    public PlayingCards() {
        super();
        final String[] strings = {
                "As", "2s", "3s", "4s", "5s", "6s", "7s", "8s", "9s", "Ts", "Js", "Qs", "Ks",
                "Ah", "2h", "3h", "4h", "5h", "6h", "7h", "8h", "9h", "Th", "Jh", "Qh", "Kh",
                "Ac", "2c", "3c", "4c", "5c", "6c", "7c", "8c", "9c", "Tc", "Jc", "Qc", "Kc",
                "Ad", "2d", "3d", "4d", "5d", "6d", "7d", "8d", "9d", "Td", "Jd", "Qd", "Kd",
                "Joker Black", "Joker Red"
        };
        Collections.addAll(this, strings);
    }

    public PlayingCards(ArrayList<String> cards) {
        this.addAll(cards);
    }

    public PlayingCards(String[] cards) {
        Collections.addAll(this, cards);
    }

    public void shuffle(){
        Collections.shuffle(this);
    }
}