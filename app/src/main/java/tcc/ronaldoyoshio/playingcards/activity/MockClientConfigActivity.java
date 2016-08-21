package tcc.ronaldoyoshio.playingcards.activity;

import android.os.Bundle;

import tcc.ronaldoyoshio.playingcards.R;

public class MockClientConfigActivity extends AbstractGameConfigurationActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mock);
    }

    @Override
    public void onResume() {
        super.onResume();
        super.findPeers();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
