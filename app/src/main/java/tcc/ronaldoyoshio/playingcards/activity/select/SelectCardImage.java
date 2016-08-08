package tcc.ronaldoyoshio.playingcards.activity.select;

import tcc.ronaldoyoshio.playingcards.activity.CardImage;
import tcc.ronaldoyoshio.playingcards.activity.TouchEventHandler;

/**
 * CardImage usado na seleção de cartas.
 * Created by mori on 30/07/16.
 */
public class SelectCardImage extends CardImage {
    public SelectCardImage() {
        addTouchEventHandler(new TouchEventHandler(){
            @Override
            public boolean onDown(int pointerId, float x, float y, int width, int height) {
                int index = findFirstCardIndexAt(getGLX(x, width), getGLY(y, height));
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
