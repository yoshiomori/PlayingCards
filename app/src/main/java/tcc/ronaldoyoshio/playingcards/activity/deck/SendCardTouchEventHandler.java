package tcc.ronaldoyoshio.playingcards.activity.deck;

import java.util.ArrayList;

import tcc.ronaldoyoshio.playingcards.activity.TouchEventHandler;
import tcc.ronaldoyoshio.playingcards.gl.GLActivity;
import tcc.ronaldoyoshio.playingcards.gl.GLScreen;

/**
 * Classe que lida com o evento de toque para enviar carta para outros jogadores.
 * Created by mori on 13/09/16.
 */
public class SendCardTouchEventHandler extends TouchEventHandler{
    private MotionCardImage motionCardImage;
    private GLActivity glActivity;

    public SendCardTouchEventHandler(MotionCardImage motionCardImage, GLActivity glActivity) {
        this.motionCardImage = motionCardImage;
        this.glActivity = glActivity;
    }

    @Override
    public boolean onMove(int pointerId, float x, float y, float dx, float dy) {
        GLScreen screen = glActivity.getScreen();
        if(screen.getHeight() - y < 50 || y < 50 || screen.getWidth() - x < 50 || x < 50) {
            /* Se a carta for empurrada rápido o suficiente para a borda, então a carta será enviada */
            if (!motionCardImage.getPointerCards().isEmpty()) {
                if (motionCardImage.getOnSendCard() == null) {
                    throw new RuntimeException("onSendCard deve ser configurado com o método" +
                            " SetOnSendCard");
                }
                System.out.println("I will call onSendCard method");
                motionCardImage.getOnSendCard().onSendCard(pointerId, (int)x, (int)y);
            }
        }
        return true;
    }

    public String computeNearestPlayerName(
            ArrayList<String> playersName,
            ArrayList<Integer> directions,
            int x,
            int y
    ) {
        float nearestDirection = Float.POSITIVE_INFINITY;
        int bestIndex = -1;
        System.out.println(directions);
        System.out.println(x + ", " + y);
        for (int index = 0; index < playersName.size(); index++) {
            float direction = Math.abs(directions.get(index * 2) - x)
                    + Math.abs(directions.get(index * 2 + 1) - y);
            System.out.println(direction);
            if (nearestDirection > direction) {
                nearestDirection = direction;
                bestIndex = index;
            }
        }
        if (bestIndex == -1) {
            return "";
        }
        return playersName.get(bestIndex);
    }
}
