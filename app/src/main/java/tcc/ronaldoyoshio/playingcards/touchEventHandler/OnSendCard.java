package tcc.ronaldoyoshio.playingcards.touchEventHandler;

import java.util.ArrayList;

/**
 * Interface que possui o método que será chamado quando ocorrer o evento de enviar uma carta.
 */
public interface OnSendCard {
    void onSendCard(int pointerId, ArrayList<String> cards, int x, int y);
}
