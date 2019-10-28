package app.aplan.burden.config;

import app.aplan.burden.entity.PortForward;

import java.util.ArrayList;
import java.util.List;

public class Configuration {
    private List<PortForward> disabled = new ArrayList<>();

    public List<PortForward> getDisabled() {
        return disabled;
    }

    public void setDisabled(List<PortForward> disabled) {
        this.disabled = disabled;
    }
}
