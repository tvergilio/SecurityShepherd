package uk.ac.leedsBeckett;

import org.apache.log4j.Logger;

import java.util.Objects;

public class Module {
    private static org.apache.log4j.Logger log = Logger.getLogger(Module.class);
    private String id;
    private String name;
    private boolean isOpen;

    private String flag;

    public Module() {
        super();
    }

    public Module(String id, String name, boolean isOpen) {
        this.id = id;
        this.name = name;
        this.isOpen = isOpen;
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

    public boolean isOpen() {
        return isOpen;
    }
    public void setOpen(boolean open) {
        this.isOpen = open;
    }
    public String getFlag() {
        return flag;
    }
    public void setFlag(String flag) {
        this.flag = flag;
    }
}
