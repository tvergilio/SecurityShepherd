package uk.ac.leedsBeckett;

import org.apache.log4j.Logger;

import java.util.Objects;

public class Module {
    private static org.apache.log4j.Logger log = Logger.getLogger(Module.class);
    private String id;
    private String name;
    private boolean isClosed;

    public Module() {
        super();
        log.debug("Instance of Module created using no-argument constructor.");
    }

    public Module(String id, String name, boolean isClosed) {
        this.id = id;
        this.name = name;
        this.isClosed = isClosed;
        log.debug("Instance of Module created using three arguments constructor.");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Module module = (Module) o;
        return Objects.equals(id, module.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        this.isClosed = closed;
    }
}
