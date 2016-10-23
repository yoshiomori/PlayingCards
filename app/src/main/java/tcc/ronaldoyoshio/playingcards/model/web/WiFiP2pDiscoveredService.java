package tcc.ronaldoyoshio.playingcards.model.web;

import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Parcel;
import android.os.Parcelable;

public class WiFiP2pDiscoveredService implements Parcelable {

    private WifiP2pDevice device;
    private int port;
    private String name;
    private String instanceName;

    public WiFiP2pDiscoveredService(String name, WifiP2pDevice device) {
        setDevice(device);
        setName(name);
    }

    public WifiP2pDevice getDevice() {
        return device;
    }

    public void setDevice(WifiP2pDevice device) {
        this.device = device;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    protected WiFiP2pDiscoveredService(Parcel in) {
        device = (WifiP2pDevice) in.readValue(WifiP2pDevice.class.getClassLoader());
        port = in.readInt();
        name = in.readString();
        instanceName = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(device);
        dest.writeInt(port);
        dest.writeString(name);
        dest.writeString(instanceName);
    }

    public static final Parcelable.Creator<WiFiP2pDiscoveredService> CREATOR = new Parcelable.Creator<WiFiP2pDiscoveredService>() {
        @Override
        public WiFiP2pDiscoveredService createFromParcel(Parcel in) {
            return new WiFiP2pDiscoveredService(in);
        }

        @Override
        public WiFiP2pDiscoveredService[] newArray(int size) {
            return new WiFiP2pDiscoveredService[size];
        }
    };
}