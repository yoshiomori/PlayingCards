package tcc.ronaldoyoshio.playingcards.model;

import java.util.ArrayList;

/**
 * Classe Hand manipula as cartas recebidas do servidor
 */
public class Hand {
    private ArrayList<Card> hand = new ArrayList<>();

    public ArrayList<Card> show(){
        return hand;
    }

    public boolean Discard(Card card){
        return hand.remove(card);
    }

    public void Draw(Card card){
        hand.add(card);
    }

    public int size() {
        return hand.size();
    }
}
