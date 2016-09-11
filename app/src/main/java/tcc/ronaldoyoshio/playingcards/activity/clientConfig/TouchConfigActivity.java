package tcc.ronaldoyoshio.playingcards.activity.clientConfig;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextPaint;

import java.util.ArrayList;

import tcc.ronaldoyoshio.playingcards.activity.BackGround;
import tcc.ronaldoyoshio.playingcards.gl.GLActivity;

/**
 * Configura para onde a carta será enviada.
 * Created by mori on 10/09/16.
 */
public class TouchConfigActivity extends GLActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /* AddImage deve ser chamado antes do onCreate */
        addImage(new BackGround());

        Bundle extras = getIntent().getExtras();
        ArrayList<String> playersName;
        if (extras.containsKey("playersName")) {
            playersName = extras.getStringArrayList("playersName");
            System.out.println(playersName);
            TextPaint paint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
            paint.setTextSize(100);
            paint.setColor(Color.WHITE);
            paint.setTextAlign(TextPaint.Align.CENTER);
            paint.setTextScaleX(0.5f);
            if (playersName != null && !playersName.isEmpty()) {
                int index = 2;
                int w = (int) paint.measureText(playersName.get(index));
                int h = (int) (paint.descent() - paint.ascent());
                Bitmap b = Bitmap.createBitmap(w * 2, h, Bitmap.Config.ARGB_8888);
                Canvas c = new Canvas(b);
                c.drawText(playersName.get(index), w, - paint.ascent(), paint);
                addImage(new TextureImage(b, this));
            }
        }

        super.onCreate(savedInstanceState);
    }
}
