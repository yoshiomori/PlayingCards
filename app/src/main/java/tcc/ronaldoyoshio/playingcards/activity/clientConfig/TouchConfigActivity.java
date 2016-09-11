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
            int w = 500;
            int h = 200;
            Bitmap b = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);
            if (playersName != null && !playersName.isEmpty()) {
                c.drawText(playersName.get(0), w / 2, 2 * h / 3, paint);
                addImage(new TextureImage(b, this));
            }
        }

        super.onCreate(savedInstanceState);
    }
}
