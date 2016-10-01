package tcc.ronaldoyoshio.playingcards.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class WebMessage implements Serializable {
    private Integer tag;

    private Map<String, String> args;

    public WebMessage () {
        args = new HashMap<>();
    }

    public Integer getTag() {
        return tag;
    }

    public void setTag(Integer tag) {
        this.tag = tag;
    }


    public Map<String, String> getArgs() {
        return args;
    }

    public void setArgs(Map<String, String> args) {
        this.args = args;
    }

    public void insertMessage(String type, String message) {
        args.put(type, message);
    }

    public String getMessage(String type) {
        return args.containsKey(type) ? args.get(type) : "";
    }
}
