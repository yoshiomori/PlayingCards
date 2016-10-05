package tcc.ronaldoyoshio.playingcards.model.web;

import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Parcel;
import android.os.Parcelable;

public class WiFiP2pDiscoveredService implements Parcelable {

    private WifiP2pDevice device;
    private String instanceName = null;
    private String serviceRegistrationType = null;
    private int port;
    private String name = null;

    public WiFiP2pDiscoveredService(String name, int port) {
        this.instanceName = instanceName;
        this.port = port;
        this.name = name;
    }

    public WifiP2pDevice getDevice() {
        return device;
    }

    public void setDevice(WifiP2pDevice device) {
        this.device = device;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getServiceRegistrationType() {
        return serviceRegistrationType;
    }

    public void setServiceRegistrationType(String serviceRegistrationType) {
        this.serviceRegistrationType = serviceRegistrationType;
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


    protected WiFiP2pDiscoveredService(Parcel in) {
        device = (WifiP2pDevice) in.readValue(WifiP2pDevice.class.getClassLoader());
        instanceName = in.readString();
        serviceRegistrationType = in.readString();
        port = in.readInt();
        name = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(device);
        dest.writeString(instanceName);
        dest.writeString(serviceRegistrationType);
        dest.writeInt(port);
        dest.writeString(name);
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