package tcc.ronaldoyoshio.playingcards.model.web;

import android.net.wifi.p2p.WifiP2pDevice;

public class WiFiP2pDiscoveredService {

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
}