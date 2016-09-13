package tcc.ronaldoyoshio.playingcards.activity.deck;

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
            System.out.println(dx * dx + dy * dy);
            if (!motionCardImage.getPointerCards().isEmpty() && dx * dx + dy * dy > 400f) {
                System.out.println("Carta deve ser enviada!");
                motionCardImage.getOnSendCard().onSendCard((int)x, (int)y);

                motionCardImage.getMotionTouchEventHandler().movePointerCard(
                        pointerId,
                        (float) getWidth() / 2 - x,
                        (float) getHeight() / 2 - y
                );

                motionCardImage.getMotionTouchEventHandler().deactivateCards();
            }
        }
        return super.onMove(pointerId, x, y, dx, dy);
    }
}
