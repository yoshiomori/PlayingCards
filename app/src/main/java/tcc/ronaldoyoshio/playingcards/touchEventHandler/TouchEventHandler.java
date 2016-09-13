package tcc.ronaldoyoshio.playingcards.touchEventHandler;

import android.opengl.Matrix;
import android.view.MotionEvent;

import java.util.HashMap;
import java.util.List;

import tcc.ronaldoyoshio.playingcards.gl.GLObject;
import tcc.ronaldoyoshio.playingcards.gl.GLScreen;

/**
 * Classe trata de toque na tela.
 * Created by mori on 28/07/16.
 */
public class TouchEventHandler {
    GLScreen screen;
    float[] m = new float[16];
    float[] v = new float[4];
    private HashMap<Integer, Float> mPreviousX = new HashMap<>();
    private HashMap<Integer, Float> mPreviousY = new HashMap<>();

    public float[] getV() {
        return v;
    }

    public boolean onTouchEvent(MotionEvent event) {
        boolean b = false;
        float x, y;
        int pointerId, index;
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_MOVE:
                for (index = 0; index < event.getPointerCount(); index++) {

                    // x, y é a posição do dedo em coordenada de pixel
                    x = event.getX(index);
                    y = event.getY(index);

                    pointerId = event.getPointerId(index);

                    float dx = mPreviousX.containsKey(pointerId) ?
                            x - mPreviousX.get(pointerId) : 0;
                    float dy = mPreviousY.containsKey(pointerId) ?
                            y - mPreviousY.get(pointerId) : 0;

                    b = onMove(pointerId, x, y, dx, dy);

                    mPreviousX.put(pointerId, x);
                    mPreviousY.put(pointerId, y);
                }
                break;
            case MotionEvent.ACTION_DOWN:
                index = event.getActionIndex();

                // x, y é a posição do dedo em coordenada de pixel
                x = event.getX(index);
                y = event.getY(index);

                pointerId = event.getPointerId(index);
                b = onDown(pointerId, x, y);
                mPreviousX.put(pointerId, x);
                mPreviousY.put(pointerId, y);
                break;
            case MotionEvent.ACTION_UP:
                index = event.getActionIndex();
                pointerId = event.getPointerId(index);
                b = onUp();
                mPreviousX.remove(pointerId);
                mPreviousY.remove(pointerId);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                index = event.getActionIndex();

                // x, y é a posição do dedo em coordenada de pixel
                x = event.getX(index);
                y = event.getY(index);

                pointerId = event.getPointerId(index);
                b = onPointerDown(pointerId, x, y);
                mPreviousX.put(pointerId, x);
                mPreviousY.put(pointerId, y);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                index = event.getActionIndex();
                pointerId = event.getPointerId(index);
                b = onPointerUp(pointerId);
                mPreviousX.remove(pointerId);
                mPreviousY.remove(pointerId);
                break;
        }
        return b;
    }

    public boolean onPointerUp(int pointerId) {
        return false;
    }

    public boolean onPointerDown(int pointerId, float x, float y) {
        return false;
    }

    public boolean onUp() {
        return false;
    }

    public boolean onDown(int pointerId, float x, float y) {
        return false;
    }

    public boolean onMove(int pointerId, float x, float y, float dx, float dy) {
        return false;
    }

    protected void requestRender(){
        screen.requestRender();
    }

    public void setScreen(GLScreen screen) {
        this.screen = screen;
    }

    public int getWidth() {
        return screen.getWidth();
    }

    public int getHeight() {
        return screen.getHeight();
    }

    public float getGLX(float x, int width) {
        return (2 * x - width) / width;
    }

    public float getGLY(float y, int height) {
        return (height - 2 * y) / height;
    }

    /* Configurando a matriz de transformação do movimento, matriz para calcular a posição do dedo
     * em relação à projeção */
    protected void setProjectionCoords(float dx, float dy, int width, int height) {
        float ratio = (float) width / height;

        float r_width = ratio > 1f ? 1f / ratio : 1f;
        float r_height = ratio <= 1f ? ratio : 1f;

        // Criando a matriz de projeção do modelo para a tela, idêntico ao do shader.
        setProjectionMatrix(r_width, r_height);
        MultiplyInvMRhsVec(m, new float[]{getGLDx(dx, width), getGLDy(dy, height), 0, 0});
    }

    private void setProjectionMatrix(float r_width, float r_height) {
        Matrix.setIdentityM(m, 0);
        Matrix.scaleM(m, 0, r_width, r_height, 1);
    }

    public float getGLDy(float dy, int height) {
        return  - 2 * dy / height;
    }

    public float getGLDx(float dy, int width) {
        return 2 * dy / width;
    }

    public void setModelMatrix(float[] position, float r_width, float r_height) {
        setProjectionMatrix(r_width, r_height);
        Matrix.translateM(m, 0, position[0], position[1], 1);
        Matrix.rotateM(m, 0, 90f, 0, 0, 1f);
        Matrix.scaleM(m, 0, 0.4f, 0.4f, 1);
    }

    /**
     * Multiplica rhsVec com a inversa da matriz m
     *  @param m float[16] representando uma matriz 4x4
     * @param rhsVec float[4] representando um vetor de dimenção 4
     */
    private void MultiplyInvMRhsVec(float[] m, float[] rhsVec) {
        // Criando uma matriz que transforma coordenadas da tela em coordenadas do modelo.
        Matrix.invertM(m, 0, m, 0);

        // Obtendo a variação do dedo nas coordenadas do modelo.
        Matrix.multiplyMV(v, 0, m, 0, rhsVec, 0);
    }

    /**
     * Transforma as coordenadas x, y em coordenadas do modelo da carta card
     * @param x coordenada da tela
     * @param width Comprimento da tela
     * @param y coordenada da tela
     * @param height Altura da tela
     * @param card GLObject que representa carta e tem como atributos "position" e "card_coord"
     */
    protected void setModelCoord(float x, int width, float y, int height, GLObject card) {
        // Pegando a posição da carta
        float[] position = card.getFloats("position");

        float ratio = (float) width / height;

        float r_width = ratio > 1f ? 1f / ratio : 1f;
        float r_height = ratio <= 1f ? ratio : 1f;

        // Criando a matriz de transformação dos vértices da carta, idêntico ao do
        // shader
        setModelMatrix(position, r_width, r_height);


        final float glx = getGLX(x, width);
        final float gly = getGLY(y, height);

        // x, y são coordenadas da tela, m é uma matriz que transforma coordenadas do
        // modelo da carta em coordenadas da tela, por isso é necessário inverter a
        // matriz.
        MultiplyInvMRhsVec(m, new float[] {glx, gly, 0, 1});
    }

    public int findFirstCardIndexAt(float x, int width, float y, int height, List<GLObject> cards) {
        int index;
        for (index = cards.size() - 1; index >= 0; index--){
            setModelCoord(x, width, y, height, cards.get(index));
            if (cardHit()) {
                break;
            }
        }
        return index;
    }

    protected boolean cardHit() {
        return v[0] * v[0] <= 0.890552f * 0.890552f && v[1] * v[1] <= 0.634646f * 0.634646f;
    }
}
