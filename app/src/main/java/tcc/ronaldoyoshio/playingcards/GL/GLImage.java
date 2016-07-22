package tcc.ronaldoyoshio.playingcards.GL;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.nio.Buffer;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Abstração de dados usados para renderizar com a biblioteca gráfica.
 * Created by mori on 09/07/16.
 */
public abstract class GLImage extends ArrayList<GLImage.GLObject> {

    private int arrayIndex = -1;
    private int elementArrayIndex = -1;
    private HashMap<String, GLAttribute> attributes = new HashMap<>();
    private HashMap<String, GLUniform> uniforms = new HashMap<>();
    private float[] array = null;
    private short[] elementArray = null;
    private String fragmentShaderCode = null;
    private String vertexShaderCode = null;
    private int mode = -1;
    private int first = -1;
    private Buffer indices = null;
    private int count = -1;
    private Bitmap bitmap;
    private int textureIndex = -1;
    private Resources resources;
    private int bitmapId = -1;
    private String positionName = "";
    private String colorName = "";
    private float ratio;
    private String ratioName = "";
    private String scaleName = "";
    private String orientationName = "";

    public void setRatioName(String uniformName) {
        ratioName = uniformName;
    }

    public void setTexture(String name, int id) {
        setUniform(name, 0);
        bitmapId = id;
    }

    protected void setAttribute(String name, Boolean normalized, int stride, int offset){
        GLAttribute attribute;
        if (attributes.containsKey(name)) {
            attribute = attributes.get(name);
        }
        else {
            attribute = new GLAttribute();
            attributes.put(name, attribute);
        }
        attribute.setNormalized(normalized);
        attribute.setStride(stride);
        attribute.setValue(offset);
    }

    protected void setAttribute(String name, Boolean normalized, int stride, float... array){
        GLAttribute attribute;
        if (attributes.containsKey(name)) {
            attribute = attributes.get(name);
        }
        else {
            attribute = new GLAttribute();
            attributes.put(name, attribute);
        }
        attribute.setNormalized(normalized);
        attribute.setStride(stride);
        attribute.setValue(array);
    }

    protected void setUniform(String name, float... array){
        GLUniform uniform;
        if (uniforms.containsKey(name)) {
            uniform = uniforms.get(name);
        }
        else {
            uniform = new GLUniform();
            uniforms.put(name, uniform);
        }
        uniform.setValue(array);
    }

    protected void setUniform(String name, int x){
        GLUniform uniform;
        if (uniforms.containsKey(name)) {
            uniform = uniforms.get(name);
        }
        else {
            uniform = new GLUniform();
            uniforms.put(name, uniform);
        }
        uniform.setValue(x);
    }

    public void setUniform(String name, float x) {
        GLUniform uniform;
        if (uniforms.containsKey(name)) {
            uniform = uniforms.get(name);
        }
        else {
            uniform = new GLUniform();
            uniforms.put(name, uniform);
        }
        uniform.setValue(x);
    }

    protected void setArray(float... array) {
        assert array != null;
        this.array = array;
    }

    protected void setElementArray(short... elementArray) {
        assert elementArray != null;
        this.elementArray = elementArray;
    }

    /**
     * Quando o element array buffer é declarado, o argumento first é usado como offset
     * @param mode modo de desenho
     * @param first ou offset, quando element array buffer é declarado
     * @param count número de vértices a ser desenhado.
     */
    protected void setShader(String vertexShaderCode, String fragmentShaderCode, int mode, int first, int count) {
        this.vertexShaderCode = vertexShaderCode;
        this.fragmentShaderCode = fragmentShaderCode;
        this.mode = mode;
        this.first = first;
        this.count = count;
    }

    protected void setShader(String vertexShaderCode, String fragmentShaderCode, int mode, int count,  short... indices) {
        this.vertexShaderCode = vertexShaderCode;
        this.fragmentShaderCode = fragmentShaderCode;
        this.mode = mode;
        this.count = count;
        this.indices = GLBuffers.adapt(indices);
    }

    public String getFragmentShaderCode() {
        return fragmentShaderCode;
    }

    public String getVertexShaderCode() {
        return vertexShaderCode;
    }


    public int getArrayIndex() {
        return arrayIndex;
    }

    public void setArrayIndex(int arrayIndex){
        this.arrayIndex = arrayIndex;
    }

    public float[] getArray() {
        return array;
    }

    public short[] getElementArray() {
        return elementArray;
    }

    public void setElementArrayIndex(int elementArrayIndex) {
        this.elementArrayIndex = elementArrayIndex;
    }

    public int getElementArrayIndex() {
        return elementArrayIndex;
    }

    private int program;
    private final int[] programInfo = new int[6]; /* 6 é o número de MACROS */

    private final int GL_ACTIVE_ATTRIBUTES = 0;
    private final int GL_ACTIVE_ATTRIBUTE_MAX_LENGTH = 1;
    private final int GL_ACTIVE_UNIFORMS = 2;
    private final int GL_ACTIVE_UNIFORM_MAX_LENGTH = 3;

    public void initProgram() {

        program = GL.glCreateProgram();
        if (program == 0)
            throw new RuntimeException("Falha ao criar o programa");
        int vertexShader = loadShader(GL.GL_VERTEX_SHADER, vertexShaderCode);
        GL.glAttachShader(program, vertexShader);
        int fragmentShader = loadShader(GL.GL_FRAGMENT_SHADER, fragmentShaderCode);
        GL.glAttachShader(program, fragmentShader);
        GL.glLinkProgram(program);

        GL.glGetProgramiv(program, GL.GL_ACTIVE_ATTRIBUTES, programInfo, GL_ACTIVE_ATTRIBUTES);
        GL.glGetProgramiv(program, GL.GL_ACTIVE_ATTRIBUTE_MAX_LENGTH,
                programInfo, GL_ACTIVE_ATTRIBUTE_MAX_LENGTH);
        GL.glGetProgramiv(program, GL.GL_ACTIVE_UNIFORMS, programInfo, GL_ACTIVE_UNIFORMS);
        GL.glGetProgramiv(program, GL.GL_ACTIVE_UNIFORM_MAX_LENGTH, programInfo,
                GL_ACTIVE_UNIFORM_MAX_LENGTH);
        int GL_LINK_STATUS = 4;
        GL.glGetProgramiv(program, GL.GL_LINK_STATUS, programInfo,
                GL_LINK_STATUS);
        int GL_ATTACHED_SHADERS = 5;
        GL.glGetProgramiv(program, GL.GL_ATTACHED_SHADERS, programInfo,
                GL_ATTACHED_SHADERS);


        if (programInfo[GL_LINK_STATUS] == GL.GL_FALSE) {
            throw new RuntimeException("Link error: attached shader: " + programInfo[GL_ATTACHED_SHADERS]);
        }

        initAttributeLocation();
        initUniforms();
    }

    private int loadShader(int type, String shaderCode){

        int shader = GL.glCreateShader(type);
        GL.glShaderSource(shader, shaderCode);
        GL.glCompileShader(shader);
        return shader;
    }

    private void initUniforms() {

        byte[] name = new byte[programInfo[GL_ACTIVE_UNIFORM_MAX_LENGTH]];
        int[] length = new int[1];
        int[] size = new int[1];
        int[] type = new int[1];
        for (int location = 0; location < programInfo[GL_ACTIVE_UNIFORMS]; location++) {
            GL.glGetActiveUniform(
                    program, location, name.length, length, 0, size, 0, type, 0, name, 0);
            String uniformName = new String(name, 0, length[0]);
            GLUniform uniform;
            if (uniforms.containsKey(uniformName)) {
                uniform = uniforms.get(uniformName);
            }
            else {
                uniform = new GLUniform();
                uniforms.put(uniformName, uniform);
            }
            uniform.setLocation(location);
            uniform.setType(type[0]);
        }
    }

    private void initAttributeLocation() {

        byte[] name = new byte[programInfo[GL_ACTIVE_ATTRIBUTE_MAX_LENGTH]];
        int[] length = new int[1];
        int[] size = new int[1];
        int[] type = new int[1];
        for (int location = 0; location < programInfo[GL_ACTIVE_ATTRIBUTES]; location++) {
            GL.glGetActiveAttrib(
                    program, location, name.length, length, 0, size, 0, type, 0, name, 0);
            String attributeName = new String(name, 0, length[0]);
            GLAttribute attribute;
            if (attributes.containsKey(attributeName)) {
                attribute = attributes.get(attributeName);
            }
            else {
                attribute = new GLAttribute();
                attributes.put(attributeName, attribute);
            }
            attribute.setLocation(location);
            attribute.setType(type[0]);
        }
    }

    public void render(GLBuffers buffers, GLTextures textures) {
        GL.glUseProgram(program);
        buffers.bindArrayBuffer(arrayIndex);
        buffers.bindElementArrayBuffer(elementArrayIndex);
        textures.bindTextures(textureIndex);
        defineAttributes();
        defineUniforms();
        for (GLObject object :
                this) {
            GLUniform uniform;
            if (!positionName.isEmpty()) {
                uniform = uniforms.get(positionName);
                uniform.setValue(object.getPosition());
                uniform.define();
            }
            if (!colorName.isEmpty()) {
                uniform = uniforms.get(colorName);
                uniform.setValue(object.getColor());
                uniform.define();
            }
            if (!scaleName.isEmpty()) {
                uniform = uniforms.get(scaleName);
                uniform.setValue(object.getScale());
                uniform.define();
            }
            if (!orientationName.isEmpty()) {
                uniform = uniforms.get(orientationName);
                uniform.setValue(object.getOrientation());
                uniform.define();
            }
            draw();
        }
        if (this.isEmpty() & positionName.isEmpty() & colorName.isEmpty() & scaleName.isEmpty()
                & orientationName.isEmpty()) {
            draw();
        }
    }

    private void draw() {
        if (indices != null) {
            GL.glDrawElements(mode, count, GL.GL_UNSIGNED_SHORT, indices);
        } else if (elementArrayIndex >= 0) {
            GL.glDrawElements(mode, count, GL.GL_UNSIGNED_SHORT, first);
        } else {
            GL.glDrawArrays(mode, first, count);
        }
    }

    private void defineAttributes() {
        for (GLAttribute attribute :
                attributes.values()) {
            attribute.define();
        }
    }

    private void defineUniforms() {
        for (String uniformName :
                uniforms.keySet()) {
            if (!uniformName.equals(positionName) & !uniformName.equals(colorName)
                    & !uniformName.equals(scaleName) & !uniformName.equals(orientationName)) {
                uniforms.get(uniformName).define();
            }
        }
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setTextureIndex(int textureIndex) {
        this.textureIndex = textureIndex;
    }

    public int getTextureIndex() {
        return textureIndex;
    }

    public void setRatio(float ratio) {
        this.ratio = ratio;
        if (!ratioName.isEmpty()) {
            setUniform(ratioName, ratio);
        }
    }

    public void setResources(Resources resources) {
        this.resources = resources;
    }

    public void loadDatas() {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;	// No pre-scaling
        this.bitmap = bitmapId < 0 ? null : BitmapFactory.decodeResource(resources, bitmapId, options);
    }

    public int getBitmapId() {
        return bitmapId;
    }

    public abstract void onMove(float dx, float dy);

    public abstract void onDown(float x, float y);

    public abstract void onUp();

    public float getRatio() {
        return ratio;
    }

    public class GLObject {
        private float[] position = new float[] {0f, 0f, 0f, 1f};
        private float[] color = new float[] {0f, 0f, 0f, 1f};
        private float[] scale = new float[] {1f, 1f, 1f, 1f};
        private float[] orientation = new float[] {0f, 0f, 0f, 0f};

        public float[] getPosition() {
            return position;
        }

        public float[] getColor() {
            return color;
        }

        public void setPosition(float... position) {
            System.arraycopy(position, 0, this.position, 0, position.length);
        }

        public void setColor(float... color) {
            System.arraycopy(color, 0, this.color, 0, color.length);
        }

        public void setScale(float... scale) {
            System.arraycopy(scale, 0, this.scale, 0, scale.length);
        }

        public float[] getScale() {
            return scale;
        }

        public float[] getOrientation() {
            return orientation;
        }

        public void setOrientation(float... orientation) {
            System.arraycopy(orientation, 0, this.orientation, 0, orientation.length);
        }
    }

    protected void setPositionName(String uniformName) {
        positionName = uniformName;
        if (uniforms.containsKey(uniformName)) {
            throw new RuntimeException("Uniform " + uniformName + " em uso!");
        }
        uniforms.put(uniformName, new GLUniform());
    }

    protected void setColorName(String uniformName) {
        colorName = uniformName;
        if (uniforms.containsKey(uniformName)) {
            throw new RuntimeException("Uniform " + uniformName + " em uso!");
        }
        uniforms.put(uniformName, new GLUniform());
    }

    protected void setScaleName(String uniformName) {
        scaleName = uniformName;
        if (uniforms.containsKey(uniformName)) {
            throw new RuntimeException("Uniform " + uniformName + " em uso!");
        }
        uniforms.put(uniformName, new GLUniform());
    }

    protected void setOrientationName(String uniformName) {
        orientationName = uniformName;
        if (uniforms.containsKey(uniformName)) {
            throw new RuntimeException("Uniform " + uniformName + " em uso!");
        }
        uniforms.put(uniformName, new GLUniform());
    }
}
