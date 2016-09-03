package tcc.ronaldoyoshio.playingcards.activity;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager.GroupInfoListener;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.os.Bundle;
import android.text.LoginFilter;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import tcc.ronaldoyoshio.playingcards.R;

public class MockServerConfigActivity extends AbstractGameConfigurationActivity {
    private static final String TAG = "ServerConfigActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mock);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void createGroup (View view) {
        manager.createGroup(channel, new ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Grupo criado com sucesso");
            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG, "Grupo criado sem sucesso");
            }
        });
    }

    public void getGroupInfo (View view) {
        manager.requestGroupInfo(channel, new GroupInfoListener() {
            @Override
            public void onGroupInfoAvailable(WifiP2pGroup group) {
                List<WifiP2pDevice> list = new ArrayList<>();
                list.addAll(group.getClientList());
                Log.d("GrouInfo", list.get(0).deviceName);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}