package tcc.ronaldoyoshio.playingcards.activity.clientConfig;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import tcc.ronaldoyoshio.playingcards.activity.BackGround;
import tcc.ronaldoyoshio.playingcards.activity.deck.DeckActivity;
import tcc.ronaldoyoshio.playingcards.activity.deck.MotionCardImage;
import tcc.ronaldoyoshio.playingcards.activity.deck.OnSendCard;
import tcc.ronaldoyoshio.playingcards.gl.GLActivity;
import tcc.ronaldoyoshio.playingcards.model.Hand;

/**
 * Configura para onde a carta será enviada.
 * Created by mori on 10/09/16.
 */
public class TouchConfigActivity extends GLActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /* AddImage deve ser chamado antes do onCreate */
        addImage(new BackGround());
        MotionCardImage card = new MotionCardImage(this);
        Hand hand = new Hand();
        hand.add("Back");
        card.setCards(hand);
        addImage(card);

        Bundle extras = getIntent().getExtras();
        ArrayList<String> playersName;
        if (extras.containsKey("playersName")) {
            playersName = extras.getStringArrayList("playersName");
            System.out.println(playersName);
            if (playersName != null && !playersName.isEmpty()) {
                /* Configurando a direção para cada jogador */
                addImage(new NameImage("Arraste a carta até a borda.", 100, 0, 2f / 3f));
                addImage(new NameImage("Em direção:", 100, 0, 0));
                Iterator<String> playerName = playersName.iterator();
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
                card.setOnSendCard(new OnSendCard(){
                    @Override
                    public void onSendCard(int x, int y) {
                        directions.add(x);
                        directions.add(y);
                        System.out.println("Carta sendo configurada: " + x + ", " + y);
                        currentNameImage[0].disable();

                        /* Verificando se a direção de todos os jogadores já foi configurado */
                        if (nameImageQueue.isEmpty()) {
                            /*
                            TODO
                             Se todos os nomes já foram configurados, então iremos chamar a
                             próxima activity.
                             */
                            Intent intent = new Intent(
                                    TouchConfigActivity.this,
                                    DeckActivity.class
                            );
                            intent.putStringArrayListExtra(
                                    "playersName",
                                    new ArrayList<>(
                                            Arrays.asList(new String[]{"João", "Maria", "Bruxa"})
                                    )
                            );
                            intent.putIntegerArrayListExtra("directions", directions);
                            TouchConfigActivity.this.startActivity(intent);
                            finish();
                        }
                        else {
                            currentNameImage[0] = nameImageQueue.remove(0);
                            currentNameImage[0].enable();
                        }
                    }
                });
            }
        }

        super.onCreate(savedInstanceState);
    }
}
