package tcc.ronaldoyoshio.playingcards.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import tcc.ronaldoyoshio.playingcards.R;
import tcc.ronaldoyoshio.playingcards.model.GameEngine;
import tcc.ronaldoyoshio.playingcards.model.Hand;

/**
 * Provides drawing instructions for a GLSurfaceView object. This class
 * must override the OpenGL ES drawing lifecycle methods:
 * <ul>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceCreated}</li>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onDrawFrame}</li>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceChanged}</li>
 * </ul>
 */
public class HandGLRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "HandGLRenderer";
    private final Context mActivityContext;
    private Card mCarta;
    private Hand hand = new Hand();
    private ArrayList<tcc.ronaldoyoshio.playingcards.model.Card> activeCards = new ArrayList<>();

    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    float[] invMMVPMatrix = new float[16];

    public HandGLRenderer(Context activityContext) {mActivityContext = activityContext;}

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);


        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;	// No pre-scaling

        // Read in the resource
        final Bitmap bitmap = BitmapFactory.decodeResource(mActivityContext.getResources(), R.drawable.playing_cards, options);

        mCarta = new Card(bitmap);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // Adjust the viewport based on geometry changes,
        // such as screen rotation
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        float[] scratch = new float[16];

        // Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -6, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        Matrix.invertM(invMMVPMatrix, 0 , mMVPMatrix, 0);

        // Create a rotation for the triangle

        // Use the following code to generate constant rotation.
        // Leave this code out when using TouchEvents.
        // long time = SystemClock.uptimeMillis() % 4000L;
        // float angle = 0.090f * ((int) time);
        for (tcc.ronaldoyoshio.playingcards.model.Card card: hand.show()) {
            // Matrix.setRotateM(mRotationMatrix, 0, mAngle, 0, 0, 1.0f);
            Matrix.translateM(scratch, 0, mMVPMatrix, 0, card.x, card.y, card.z);

            // Combine the rotation matrix with the projection and camera view
            // Note that the mMVPMatrix factor *must be first* in order
            // for the matrix multiplication product to be correct.
            // Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);

            // Draw square
            mCarta.draw(scratch, card.type);
        }
    }

    /**
     * Utility method for compiling a OpenGL shader.
     *
     * <p><strong>Note:</strong> When developing shaders, use the checkGlError()
     * method to debug shader coding errors.</p>
     *
     * @param type - Vertex or fragment shader type.
     * @param shaderCode - String containing the shader code.
     * @return - Returns an id for the shader.
     */
    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    /**
     * Utility method for debugging OpenGL calls. Provide the name of the call
     * just after making it:
     *
     * <pre>
     * mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
     * HandGLRenderer.checkGlError("glGetUniformLocation");</pre>
     *
     * If the operation is not successful, the check throws an error.
     *
     * @param glOperation - Name of the OpenGL call to check.
     */
    public static void checkGlError(String glOperation) {
        int error;
        //noinspection LoopStatementThatDoesntLoop
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }

    public void addCard(tcc.ronaldoyoshio.playingcards.model.Card card){
        hand.Draw(card);
    }

    public void setPosition(float dx, float dy, float x, float y) {
        float[] d = new float[4];
        Matrix.multiplyMV(d, 0, invMMVPMatrix, 0, new float[]{dx, dy, 0, 0}, 0);
        for(tcc.ronaldoyoshio.playingcards.model.Card card: activeCards){
            card.x += d[0] * 6f;
            card.y += d[1] * 6f;
        }
    }

    public void activateCard(float x, float y) {
        float[] v = new float[4];
        Matrix.multiplyMV(v, 0, invMMVPMatrix, 0, new float[]{x, y, 0, 0}, 0);
        Matrix.multiplyMV(v, 0, invMMVPMatrix, 0, new float[]{x, y, 0, 0}, 0);
        ArrayList<tcc.ronaldoyoshio.playingcards.model.Card> cards = GameEngine.collision(hand.show(), v[0] * 6f, v[1] * 6f);
        if(!cards.isEmpty())
            activeCards.add(cards.get(cards.size() - 1));
    }

    public void deactivateCards() {
        activeCards.clear();
    }
}
