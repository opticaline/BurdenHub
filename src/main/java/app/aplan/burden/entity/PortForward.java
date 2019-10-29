package app.aplan.burden.entity;

import org.slf4j.helpers.MessageFormatter;

public class PortForward {
    private String listenAddress;
    private int listenPort;
    private String connectAddress;
    private int connectPort;

    public String getListenAddress() {
        return listenAddress;
    }

    public void setListenAddress(String listenAddress) {
        this.listenAddress = listenAddress;
    }

    public int getListenPort() {
        return listenPort;
    }

    public void setListenPort(int listenPort) {
        this.listenPort = listenPort;
    }

    public String getConnectAddress() {
        return connectAddress;
    }

    public void setConnectAddress(String connectAddress) {
        this.connectAddress = connectAddress;
    }

    public int getConnectPort() {
        return connectPort;
    }

    public void setConnectPort(int connectPort) {
        this.connectPort = connectPort;
    }

    @Override
    public String toString() {
        return MessageFormatter.arrayFormat(
                "{}:{} -> {}:{}",
                new Object[]{listenAddress, listenPort, connectAddress, connectPort}
        ).getMessage();
    }
}
