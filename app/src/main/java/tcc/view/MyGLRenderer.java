package tcc.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import java.util.HashMap;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import tcc.playingcards.R;

/**
 * Provides drawing instructions for a GLSurfaceView object. This class
 * must override the OpenGL ES drawing lifecycle methods:
 * <ul>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceCreated}</li>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onDrawFrame}</li>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceChanged}</li>
 * </ul>
 */
public class MyGLRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "MyGLRenderer";
    private final Context mActivityContext;
    private Carta mCarta;

    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mRotationMatrix = new float[16];

    HashMap<String, float[]> imagemCarta = new HashMap<>();

    private float mAngle;

    public MyGLRenderer(Context activityContext) {
        mActivityContext = activityContext;

        imagemCarta.put("Joker Black", new float[] {0f, 0f});
        imagemCarta.put("As", new float[] {1f, 0f});
        imagemCarta.put("Ah", new float[] {2f, 0f});
        imagemCarta.put("Ad", new float[] {3f, 0f});
        imagemCarta.put("Ac", new float[] {4f, 0f});
        imagemCarta.put("Joker Red", new float[] {0f, 1f});
        imagemCarta.put("2s", new float[] {1f, 1f});
        imagemCarta.put("2h", new float[] {2f, 1f});
        imagemCarta.put("2d", new float[] {3f, 1f});
        imagemCarta.put("2c", new float[] {4f, 1f});
        imagemCarta.put("Back", new float[] {0f, 2f});
        imagemCarta.put("3s", new float[] {1f, 2f});
        imagemCarta.put("3h", new float[] {2f, 2f});
        imagemCarta.put("3d", new float[] {3f, 2f});
        imagemCarta.put("3c", new float[] {4f, 2f});
        imagemCarta.put("4s", new float[] {1f, 3f});
        imagemCarta.put("4h", new float[] {2f, 3f});
        imagemCarta.put("4d", new float[] {3f, 3f});
        imagemCarta.put("4c", new float[] {4f, 3f});
        imagemCarta.put("5s", new float[] {1f, 4f});
        imagemCarta.put("5h", new float[] {2f, 4f});
        imagemCarta.put("5d", new float[] {3f, 4f});
        imagemCarta.put("5c", new float[] {4f, 4f});
        imagemCarta.put("6s", new float[] {1f, 5f});
        imagemCarta.put("6h", new float[] {2f, 5f});
        imagemCarta.put("6d", new float[] {3f, 5f});
        imagemCarta.put("6c", new float[] {4f, 5f});
        imagemCarta.put("7s", new float[] {1f, 6f});
        imagemCarta.put("7h", new float[] {2f, 6f});
        imagemCarta.put("7d", new float[] {3f, 6f});
        imagemCarta.put("7c", new float[] {4f, 6f});
        imagemCarta.put("8s", new float[] {1f, 7f});
        imagemCarta.put("8h", new float[] {2f, 7f});
        imagemCarta.put("8d", new float[] {3f, 7f});
        imagemCarta.put("8c", new float[] {4f, 7f});
        imagemCarta.put("9s", new float[] {1f, 8f});
        imagemCarta.put("9h", new float[] {2f, 8f});
        imagemCarta.put("9d", new float[] {3f, 8f});
        imagemCarta.put("9c", new float[] {4f, 8f});
        imagemCarta.put("Ts", new float[] {1f, 9f});
        imagemCarta.put("Th", new float[] {2f, 9f});
        imagemCarta.put("Td", new float[] {3f, 9f});
        imagemCarta.put("Tc", new float[] {4f, 9f});
        imagemCarta.put("Js", new float[] {1f, 10f});
        imagemCarta.put("Jh", new float[] {2f, 10f});
        imagemCarta.put("Jd", new float[] {3f, 10f});
        imagemCarta.put("Jc", new float[] {4f, 10f});
        imagemCarta.put("Qs", new float[] {1f, 11f});
        imagemCarta.put("Qh", new float[] {2f, 11f});
        imagemCarta.put("Qd", new float[] {3f, 11f});
        imagemCarta.put("Qc", new float[] {4f, 11f});
        imagemCarta.put("Ks", new float[] {1f, 12f});
        imagemCarta.put("Kh", new float[] {2f, 12f});
        imagemCarta.put("Kd", new float[] {3f, 12f});
        imagemCarta.put("Kc", new float[] {4f, 12f});
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);


        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;	// No pre-scaling

        // Read in the resource
        final Bitmap bitmap = BitmapFactory.decodeResource(mActivityContext.getResources(), R.drawable.playing_cards, options);

        mCarta = new Carta(bitmap);
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

        // Create a rotation for the triangle

        // Use the following code to generate constant rotation.
        // Leave this code out when using TouchEvents.
        // long time = SystemClock.uptimeMillis() % 4000L;
        // float angle = 0.090f * ((int) time);

        Matrix.setRotateM(mRotationMatrix, 0, mAngle, 0, 0, 1.0f);

        // Combine the rotation matrix with the projection and camera view
        // Note that the mMVPMatrix factor *must be first* in order
        // for the matrix multiplication product to be correct.
        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);

        // Draw square
        mCarta.draw(scratch, "Joker Black");
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
     * MyGLRenderer.checkGlError("glGetUniformLocation");</pre>
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

    /**
     * Returns the rotation angle of the triangle shape (mTriangle).
     *
     * @return - A float representing the rotation angle.
     */
    public float getAngle() {
        return mAngle;
    }

    /**
     * Sets the rotation angle of the triangle shape (mTriangle).
     */
    public void setAngle(float angle) {
        mAngle = angle;
    }

}
