package app.aplan.burden.service;

import app.aplan.burden.Utils;
import app.aplan.burden.config.Configuration;
import app.aplan.burden.entity.PortForward;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PortForwardService {
    private static final Logger logger = LoggerFactory.getLogger(PortForwardService.class);

    private static final String CMD_GET_PROXY = "netsh interface portproxy show all";
    private static final String CMD_SET_PROXY = "netsh interface portproxy add v4tov4 listenaddress=%s listenport=%d connectaddress=%s connectport=%d";
    private static final String CMD_DEL_PROXY = "netsh interface portproxy del v4tov4 listenaddress=%s listenport=%d";
    private static final String[] PROXY_TYPE = new String[]{
            "v4tov4",// Shows parameters for proxying IPv4 connections to another IPv4 port.
            "v4tov6",// Shows parameters for proxying IPv4 connections to IPv6.
            "v6tov4",// Shows parameters for proxying IPv6 connections to IPv4.
            "v6tov6",// Shows parameters for proxying IPv6 connections to another IPv6 port.
    };

    public ObservableList<PortForward> getPortForwards() {
        try {
            List<String> lines = Utils.call(CMD_GET_PROXY);
            if (lines.size() > 1) {
                return FXCollections.observableList(getProxy(lines.subList(5, lines.size() - 1)));
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return FXCollections.observableArrayList();
    }

    @NonNull
    private List<PortForward> getProxy(List<String> lines) {
        return lines.stream()
                .filter((Predicate<String>) Objects::nonNull)
                .map((Function<String, PortForward>) input -> {
                    List<String> params = Splitter.on(Pattern.compile("\\s+")).splitToList(input);
                    PortForward pf = new PortForward();
                    pf.setListenAddress(params.get(0));
                    pf.setListenPort(Integer.parseInt(params.get(1)));
                    pf.setConnectAddress(params.get(2));
                    pf.setConnectPort(Integer.parseInt(params.get(3)));
                    return pf;
                }).collect(Collectors.toList());
    }

    public void create(PortForward pf) throws IOException, InterruptedException {
        String cmd = String.format(CMD_SET_PROXY, pf.getListenAddress(), pf.getListenPort(),
                pf.getConnectAddress(), pf.getConnectPort());
        Utils.call(cmd);
        logger.info("Create v4tov4 proxy {}", pf);
    }

    public void remove(PortForward pf) throws IOException, InterruptedException {
        String cmd = String.format(CMD_DEL_PROXY, pf.getListenAddress(), pf.getListenPort());
        Utils.call(cmd);
        logger.info("Remove v4tov4 proxy {}", pf);
    }

    public void enable(PortForward pf) {
        Configuration configuration = Utils.readConfig();
        configuration.getDisabled().removeIf((Predicate<PortForward>) input -> Objects.requireNonNull(input).toString().equals(pf.toString()));
        Utils.writeConfig(configuration);
        logger.info("Enable v4tov4 proxy {}", pf);
    }

    public void disable(PortForward pf) {
        Configuration configuration = Utils.readConfig();
        configuration.getDisabled().add(pf);
        Utils.writeConfig(configuration);
        logger.info("Disabled v4tov4 proxy {}", pf);
    }

    public List<String> getAddress() {
        Enumeration<NetworkInterface> nets = null;
        List<String> ans = Lists.newArrayList("0.0.0.0");
        try {
            nets = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface netint : Collections.list(nets)) {
                for (InetAddress ia : Collections.list(netint.getInetAddresses())) {
                    ans.add(ia.getHostAddress());
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        ans.sort(Comparator.comparingInt(String::length));
        return ans;
    }

    public List<PortForward> loadDisabledProxy() {
        return Utils.readConfig().getDisabled();
    }
}
