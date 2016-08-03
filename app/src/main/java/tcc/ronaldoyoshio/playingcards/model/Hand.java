package tcc.ronaldoyoshio.playingcards.model;

import java.util.ArrayList;
import java.util.Collections;

public class Hand extends ArrayList<String> {
    public Hand() {
        super();
    }

    public void shuffle(){
        Collections.shuffle(this);
    }
}
