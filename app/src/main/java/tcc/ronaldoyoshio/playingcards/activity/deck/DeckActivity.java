package tcc.ronaldoyoshio.playingcards.activity.deck;

import android.os.Bundle;
import android.view.KeyEvent;

import java.util.ArrayList;

import tcc.ronaldoyoshio.playingcards.gl.GLActivity;
import tcc.ronaldoyoshio.playingcards.gl.GLObject;
import tcc.ronaldoyoshio.playingcards.images.BackGround;
import tcc.ronaldoyoshio.playingcards.images.CardImage;
import tcc.ronaldoyoshio.playingcards.images.MotionCardImage;
import tcc.ronaldoyoshio.playingcards.model.Hand;
import tcc.ronaldoyoshio.playingcards.model.PlayingCards;
import tcc.ronaldoyoshio.playingcards.touchEventHandler.TouchEventHandler;

public class DeckActivity extends GLActivity {
    Hand cards;
    private ArrayList<String> playersName;
    private ArrayList<Integer> directions;
    MotionCardImage cardImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /* AddImage  deve Ser chamando antes de onCreate */
        addImage(new BackGround());

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            if (extras.containsKey("playersName")) {
            /* Recuperando a configuração do TouchConfigActivity, activity anterior */
                playersName = extras.getStringArrayList("playersName");
            }
            if (extras.containsKey("directions")) {
                directions = extras.getIntegerArrayList("directions");
            }
            if (extras.containsKey("cards")) {
                cards = new PlayingCards(extras.getStringArrayList("cards"));
            }
            else {
                cards = new PlayingCards();
            }
        }

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("cards")) {
                cards = new PlayingCards(savedInstanceState.getStringArrayList("cards"));
            }
        }

        cardImage = new MotionCardImage(this);

        /* Quando der duplo taps a carta vira */
        cardImage.addTouchEventHandler(new TouchEventHandler() {
            public long previousDownTime = Long.MIN_VALUE;
            public float previousX = Float.POSITIVE_INFINITY;
            public float previousY = Float.POSITIVE_INFINITY;
            public boolean doubleTap;
            public GLObject previousCard;
            @Override
            public boolean onDown(int pointerId, float x, float y) {
                // Verificando se é double tap
                long downTime = System.currentTimeMillis();
                int index = findFirstCardIndexAt(
                        x, getWidth(), y, getHeight(), cardImage.getObjects());
                if (index >= 0) {
                    GLObject currentCard = cardImage.getObjects().get(index);
                    doubleTap = isDoubleTap(
                            downTime - previousDownTime, x - previousX, y - previousY, currentCard);
                    previousDownTime = downTime;
                    previousX = x;
                    previousY = y;
                    previousCard = currentCard;

                    if (doubleTap) {
                        doubleTap = false;
                        if (cardImage.getActiveCards().isEmpty()) {
                            flipCard(cardImage.getObjects().get(index), index);
                        } else {
                            if (cardImage.getActiveCards().contains(
                                    cardImage.getObjects().get(index))) {
                                for (GLObject card :
                                        cardImage.getActiveCards()) {
                                    flipCard(card, cardImage.getObjects().indexOf(card));
                                }

                            }
                        }
                    }
                }

                return false;
            }

            public boolean isDoubleTap(long dt, float dx, float dy, GLObject card) {
                return dx * dx + dy * dy <= 1000 && dt * dt <= 100000 && previousCard == card;
            }

            private void flipCard(GLObject card, int index) {
                CardImage.CardData cardData = cardImage.getCardData();
                if (cardData.getCardCoord("Back") == card.getFloats("card_coord")) {
                    card.set("card_coord", cardData.getCardCoord(cards.get(index)));
                }
                else {
                    card.set("card_coord", cardData.getCardCoord("Back"));
                }
            }
        });
        cardImage.setOnSendCard(new SendCard(cardImage, playersName, directions));
        addImage(cardImage);

        super.onCreate(savedInstanceState);

        print(cards);
//        cards.shuffle();
//        deckCardImage.print(cards);
//        cards.remove("Joker Black");
//        deckCardImage.print(cards);
//        cards.clear();
//        cards.add("Joker Black");
//        deckCardImage.print(cards);
    }

    private void print(Hand cards) {
        cardImage.setCards(cards);
        cardImage.setMode(CardImage.CENTERED);
        getScreen().requestRender();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putStringArrayList("cards", cards);
        outState.putStringArrayList("playersName", playersName);
        outState.putIntegerArrayList("directions", directions);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
