package tcc.ronaldoyoshio.playingcards.model;

public class Card {
    public float x;
    public float y;
    public float z;
    public String type;

    public Card(String type){
        x = y = z = 0;
        this.type = type;
    }
}
