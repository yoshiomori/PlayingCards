package tcc.ronaldoyoshio.playingcards.model;

import android.os.SystemClock;

import java.util.ArrayList;
import java.util.Random;

/**
 * Classe Hand manipula as cartas recebidas do servidor
 */
public class Hand extends ArrayList<String> {
    public Hand() {
        super();
    }

    public void shuffle(){
        Random random = new Random(SystemClock.currentThreadTimeMillis());
        for (int k = 0; k < size(); k++) {
            int i = random.nextInt(size());
            int j = random.nextInt(size());
            String mem = get(i);
            set(i, get(j));
            set(j, mem);
        }
    }
}
