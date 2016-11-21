package tcc.ronaldoyoshio.playingcards.activity.select;

import tcc.ronaldoyoshio.playingcards.images.CardImage;
import tcc.ronaldoyoshio.playingcards.touchEventHandler.TouchEventHandler;

/**
 * CardImage usado na seleção de cartas.
 */
public class SelectCardImage extends CardImage {
    public SelectCardImage() {
        addTouchEventHandler(new TouchEventHandler(){
            @Override
            public boolean onDown(int pointerId, float x, float y) {
                int index = findFirstCardIndexAt(x, getWidth(), y, getHeight(), getObjects());
                if (index>=0) {
                    getCards().remove(index);
                    getObjects().remove(index);
                    return true;
                }
                return false;
            }
        });
    }
}
