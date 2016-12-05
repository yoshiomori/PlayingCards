package tcc.ronaldoyoshio.playingcards.activity.config.touch;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Iterator;

import tcc.ronaldoyoshio.playingcards.gl.GLActivity;
import tcc.ronaldoyoshio.playingcards.gl.GLObject;
import tcc.ronaldoyoshio.playingcards.images.BackGroundImage;
import tcc.ronaldoyoshio.playingcards.images.MotionCardImage;
import tcc.ronaldoyoshio.playingcards.model.Cards;
import tcc.ronaldoyoshio.playingcards.touchEventHandler.OnSendCard;

/**
 * Configura para onde a carta será enviada.
 */
public class TouchConfigActivity extends GLActivity{
    Class nextActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /* AddImage deve ser chamado antes do onCreate */
        addImage(new BackGroundImage());
        final MotionCardImage motionCardImage = new MotionCardImage(this);
        Cards cards = new Cards();
        cards.clear();
        cards.add("Back");
        motionCardImage.setCards(cards);
        motionCardImage.setTotalCards(cards.size());
        addImage(motionCardImage);

        Bundle extras = getIntent().getExtras();
        if (extras.containsKey("nextActivity")) {
            nextActivity = (Class) extras.get("nextActivity");
        }

        final ArrayList<String> playersName;
        if (extras.containsKey("playersName")) {
            playersName = extras.getStringArrayList("playersName");
            if (playersName != null && !playersName.isEmpty()) {
                /* Configurando a direção para cada jogador */
                addImage(new NameImage("Arraste a carta até a borda.", 100, 0, 2f / 3f));
                addImage(new NameImage("Em direção:", 100, 0, 0));
                final Iterator<String> playerName = playersName.iterator();
                final NameImage[] currentNameImage = {
                        new NameImage(playerName.next(), 100, 0, -2f / 3f)
                };
                addImage(currentNameImage[0]);
                final ArrayList<NameImage> nameImageQueue = new ArrayList<>();
                while (playerName.hasNext()) {
                    NameImage nameImage = new NameImage(playerName.next(), 100, 0, -2f / 3f);
                    addImage(nameImage);
                    nameImageQueue.add(nameImage);
                    nameImage.disable();
                }

                /* Quando o usuário especifica a direção, empurrando a carta para a borda, o método
                 * onSendCard é chamado */
                final ArrayList<Integer> directions = new ArrayList<>();
                motionCardImage.setOnSendCard(new OnSendCard(){
                    @Override
                    public void onSendCard(int pointerId, ArrayList<String> cards, int x, int y, ArrayList<Boolean> upsidedown) {
                        if (directions.size() < playersName.size() * 2) {
                            directions.add(x);
                            directions.add(y);
                            currentNameImage[0].disable();

                        /* Verificando se a direção de todos os jogadores já foi configurado */
                            if (nameImageQueue.isEmpty()) {
                            /*
                             Se todos os nomes já foram configurados, então iremos chamar a
                             próxima activity.
                             */
                                Intent intent = new Intent(
                                        TouchConfigActivity.this,
                                        nextActivity
                                );
                                intent.putStringArrayListExtra("playersName", playersName);
                                intent.putIntegerArrayListExtra("directions", directions);
                                TouchConfigActivity.this.startActivity(intent);
                                finish();
                            }
                            else {
                                currentNameImage[0] = nameImageQueue.remove(0);
                                currentNameImage[0].enable();


                                GLObject card = motionCardImage.getPointerCards().get(pointerId);
                                card.set("position", 0, 0);
                                motionCardImage.getMotionTouchEventHandler().deactivateCards();
                            }
                        }
                    }
                });
            }
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
