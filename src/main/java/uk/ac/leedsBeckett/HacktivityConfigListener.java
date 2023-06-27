package uk.ac.leedsBeckett;

import dbProcs.Getter;
import dbProcs.Setter;
import org.apache.log4j.Logger;
import utils.ModulePlan;
import utils.ScoreboardStatus;

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
import java.util.ArrayDeque;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HacktivityConfigListener implements ServletContextListener, HttpSessionListener, HttpSessionAttributeListener {

    private static final org.apache.log4j.Logger log = Logger.getLogger(HacktivityConfigListener.class);

    public static final String MODULES_CONFIG_PATH = "/WEB-INF/classes/active-modules";
    public static final String FLAGS_CONFIG_PATH = "/WEB-INF/classes/flags";
    private static List<Module> modules;
    private static Queue<String> flags;

    private String applicationRoot;

    public HacktivityConfigListener() {
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        applicationRoot = getApplicationRoot(event.getServletContext());

        //Change mode to Open Floor to enable challenges to be completed in any order
        ModulePlan.setOpenFloor();

        //Disable scoreboard
        ScoreboardStatus.disableScoreboard();

        //Close all modules by default
        Setter.closeAllModules(applicationRoot);

        //Check external config files exist and have the same number of lines
        checkConfigFiles();

        //Populate flags from config file
        populateFlags();

        //Activate specified modules and assign flags
        activateModules();
    }

    public static String getFlagForModule(String moduleId) {
        log.debug("Getting flag for module with id " + moduleId);
        Module module = getModuleById(moduleId);
        assert module != null;
        log.debug("Module " + module.getId() + " found.");
        return module.getFlag();
    }

    private void checkConfigFiles() {
        Path moduleConfig = Paths.get(applicationRoot, MODULES_CONFIG_PATH);
        Path flagConfig = Paths.get(applicationRoot, FLAGS_CONFIG_PATH);

        log.debug("File exists: " + Files.exists(moduleConfig));
        log.debug("File exists: " + Files.exists(flagConfig));
        try {
            assert Files.lines(moduleConfig).count() == Files.lines(flagConfig).count();
        } catch (Exception e) {
            log.debug("The number of flags does not correspond to the number of modules open. " +
                    "Please check the configuration files provided.");
            e.printStackTrace();
        }
    }

    private void populateModulesFromDatabase() {
        modules = Getter.getAllModules(applicationRoot);
    }

    private String getApplicationRoot(ServletContext context) {
        return context.getRealPath("");
    }

    private Module getModule(String name) {
        if (modules == null || modules.isEmpty()) {
            log.debug("Populating modules from database.");
            populateModulesFromDatabase();
        }
        return getModuleByName(name);
    }

    private static Module getModuleByName(String name) {
        for (Module module : modules) {
            if (module.getName().equals(name)) {
                return module;
            }
        }
        log.debug("Module " + name + " not found.");
        return null;
    }
    private static Module getModuleById(String id) {
        for (Module module : modules) {
            if (module.getId().equals(id)) {
                return module;
            }
        }
        log.debug("Module " + id + " not found.");
        return null;
    }

    private void activateModules() {
        Path configFilePath = Paths.get(applicationRoot, MODULES_CONFIG_PATH);

        try (Stream<String> lines = Files.lines(configFilePath)) {
            lines.map(this::getModule)
                    .filter(Objects::nonNull)
                    .peek(m -> log.debug("Assigning flag to module: " + m.getName()))
                    .peek(m -> m.setFlag(flags.poll()))
                    .forEach(module -> {
                        Setter.setModuleStatusOpen(applicationRoot, module.getId());
                        log.debug("Module: " + module.getName() + " open "
                                + module.getFlag());
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void populateFlags() {
        Path configFilePath = Paths.get(applicationRoot, FLAGS_CONFIG_PATH);
        log.debug("Populating flags from config file: " + configFilePath);

        try (Stream<String> lines = Files.lines(configFilePath)) {
            flags = lines.collect(Collectors.toCollection(ArrayDeque::new));
        } catch (IOException e) {
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
