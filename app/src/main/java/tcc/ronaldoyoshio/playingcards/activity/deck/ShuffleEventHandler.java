package tcc.ronaldoyoshio.playingcards.activity.deck;

import tcc.ronaldoyoshio.playingcards.activity.TouchEventHandler;

/**
 * Classe que reconhece e lida com eventos de toque para embaralhar as cartas.
 * Created by mori on 04/08/16.
 */
public class ShuffleEventHandler extends TouchEventHandler {
    @Override
    public boolean onDown(int pointerId, float x, float y, int width, int height) {
        
        System.out.println(x + ", " + y);
        return super.onDown(pointerId, x, y, width, height);
    }
}
