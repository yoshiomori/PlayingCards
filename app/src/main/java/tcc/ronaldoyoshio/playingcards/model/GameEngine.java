package tcc.ronaldoyoshio.playingcards.model;

import java.util.ArrayList;

public class GameEngine {
    public static ArrayList<Card> collision(ArrayList<Card> cards, float x, float y) {
        ArrayList<Card> selectCards = new ArrayList<>();
        for(Card card: cards)
            if(Math.abs(card.x - x) <= 0.890552f && Math.abs(card.y - y) <= 0.634646f)
                selectCards.add(card);
        return selectCards;
    }
}
