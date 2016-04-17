package tcc.model;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Classe Hand manipula as cartas recebidas do servidor
 */
public class Hand {
    private ArrayList<String> hand = new ArrayList<>();

    public ArrayList<String> Show(){
        return hand;
    }

    public boolean Discard(String card){
        return hand.remove(card);
    }

    public void Draw(String card){
        hand.add(card);
    }
}
