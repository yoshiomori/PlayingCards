package tcc.ronaldoyoshio.playingcards.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class WebMessage implements Serializable {
    private Map<String, String> map;

    public WebMessage () {
        map = new HashMap<>();
    }

    public void insertMessage(String type, String message) {
        map.put(type, message);
    }

    public String getMessage(String type) {
        return map.containsKey(type) ? map.get(type) : "";
    }
}
