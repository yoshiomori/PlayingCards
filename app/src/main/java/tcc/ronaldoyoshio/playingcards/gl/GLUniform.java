package tcc.ronaldoyoshio.playingcards.gl;

/**
 * Abstração de uniform.
 * Created by mori on 09/07/16.
 */
public class GLUniform {
    private int xi = 0;
    private float[] array = null;
    private float xf;
    private int type = -1;
    private int location = -1;

    public void setLocation(int location) {
        this.location = location;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void define() {
        if (type == GL.GL_FLOAT_VEC2) {
            GL.glUniform2fv(location, 1, array, 0);
        }
        else if (type == GL.GL_FLOAT_VEC3) {
            GL.glUniform3fv(location, 1, array, 0);
        }
        else if (type == GL.GL_FLOAT_VEC4) {
            GL.glUniform4fv(location, 1, array, 0);
        }
        else if (type == GL.GL_FLOAT_MAT2) {
            GL.glUniformMatrix2fv(location, 1, false, array, 0);
        }
        else if (type == GL.GL_FLOAT_MAT3) {
            GL.glUniformMatrix3fv(location, 1, false, array, 0);
        }
        else if (type == GL.GL_FLOAT_MAT4) {
            GL.glUniformMatrix4fv(location, 1, false, array, 0);
        }
        else if (type == GL.GL_SAMPLER_2D) {
            GL.glUniform1i(location, xi);
        }
        else if (type == GL.GL_INT) {
            GL.glUniform1i(location, xi);
        }
        else if (type == GL.GL_FLOAT) {
            GL.glUniform1f(location, xf);
        }
        else {
            throw new RuntimeException("Caso não implementado: " + type);
        }
    }

    public void setValue(int x) {
        xi = x;
    }

    public void setValue(float x) {
        xf = x;
    }

    public void setValue(float[] array) {
        this.array = array;
    }
}
