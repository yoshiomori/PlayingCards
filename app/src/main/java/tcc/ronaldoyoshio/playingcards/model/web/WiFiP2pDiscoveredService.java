package tcc.ronaldoyoshio.playingcards.model.web;

import android.net.wifi.p2p.WifiP2pDevice;

public class WiFiP2pDiscoveredService {

    private WifiP2pDevice device;
    private String instanceName = null;
    private String serviceRegistrationType = null;

    private String name = null;

    public WiFiP2pDiscoveredService(WifiP2pDevice device, String instanceName, String serviceRegistrationType ) {
        this.device = device;
        this.instanceName = instanceName;
        this.serviceRegistrationType = serviceRegistrationType;
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
}