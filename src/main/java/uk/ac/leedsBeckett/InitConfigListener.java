package uk.ac.leedsBeckett;

import dbProcs.Getter;
import dbProcs.Setter;
import org.apache.log4j.Logger;
import utils.ModulePlan;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class InitConfigListener implements ServletContextListener, HttpSessionListener, HttpSessionAttributeListener {

    private static final org.apache.log4j.Logger log = Logger.getLogger(InitConfigListener.class);
    private static List<Module> modules;

    public InitConfigListener() {
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        String applicationRoot = getApplicationRoot(event.getServletContext());

        //Change mode to Open Floor to enable challenges to be completed in any order
        ModulePlan.setOpenFloor();

        //Close all modules by default
        Setter.closeAllModules(applicationRoot);

        //Activate only specified modules
        activateModules(applicationRoot);
    }

    private void populateModulesFromDatabase(String applicationRoot) {
        modules = Getter.getAllModules(applicationRoot);
    }

    private String getApplicationRoot(ServletContext context) {
        return context.getRealPath("");
    }

    private Module getModuleByName(String name, String applicationRoot) {
        if (modules == null || modules.isEmpty()) {
            log.debug("Populating modules from database.");
            populateModulesFromDatabase(applicationRoot);
        }
        for (Module module : modules) {
            if (module.getName().equals(name)) {
                return module;
            }
        }
        log.debug("Module " + name + " not found.");
        return null;
    }

    private void activateModules(String applicationRoot) {
        Path configFilePath = Paths.get(applicationRoot + "/WEB-INF/classes/active-modules");
        log.debug("File path is: " + configFilePath);
        log.debug("File path exists: " + Files.exists(configFilePath));

        try (Stream<String> lines = Files.lines(configFilePath)) {
            lines.map(s -> getModuleByName(s, applicationRoot))
                    .filter(Objects::nonNull)
                    .peek(m -> log.debug("Module to open: " + m.getName()))
                    .forEach(module -> Setter.setModuleStatusOpen(applicationRoot, module.getId()));
        } catch (IOException e) {
            log.debug("Could not activate modules from config file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        /* This method is called when the servlet Context is undeployed or Application Server shuts down. */
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        /* Session is created. */
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        /* Session is destroyed. */
    }

    @Override
    public void attributeAdded(HttpSessionBindingEvent sbe) {
        /* This method is called when an attribute is added to a session. */
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent sbe) {
        /* This method is called when an attribute is removed from a session. */
    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent sbe) {
        /* This method is called when an attribute is replaced in a session. */
    }
}
