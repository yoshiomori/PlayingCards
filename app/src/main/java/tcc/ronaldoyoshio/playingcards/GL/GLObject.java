package tcc.ronaldoyoshio.playingcards.GL;

import java.util.ArrayList;
import java.util.HashMap;

public class GLObject {
    private HashMap<String, float[]> floats = new HashMap<>();

    public void bind(HashMap<String, GLUniform> uniforms, ArrayList<String> uniformNames) {
        for (String uniformName :
                uniformNames) {
            GLUniform uniform = uniforms.get(uniformName);
            if (floats.containsKey(uniformName)) {
                uniform.setValue(floats.get(uniformName));
                uniform.define();
            }
            else {
                throw new RuntimeException("Uniform de objeto não definido! (" + uniformName + ")");
            }
        }
    }

    public void set(String uniformName, float... floats) {
        if (floats.length > 1) {
            this.floats.put(uniformName, floats);
        }
        else {
            throw new RuntimeException("Caso não implementado!");
        }
    }

    public float[] getFloats(String uniformName) {
        return floats.get(uniformName);
    }
}
