package uk.ac.leedsBeckett;

import com.google.gson.Gson;
import dbProcs.Setter;
import org.apache.log4j.Logger;
import utils.ModulePlan;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class InitConfigListener implements ServletContextListener, HttpSessionListener, HttpSessionAttributeListener {

    private static org.apache.log4j.Logger log = Logger.getLogger(InitConfigListener.class);

    public InitConfigListener() {
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        //Change mode to Open Floor to enable challenges to be completed in any order
        ModulePlan.setOpenFloor();

        String applicationRoot = sce.getServletContext().getRealPath("");
        Path configFilePath = Paths.get(applicationRoot + "/WEB-INF/classes/config.json");
        log.debug("File path is: " + configFilePath);
        log.debug("File path exists: " + Files.exists(configFilePath));

        //Read from config file and close modules where needed
        try (Reader reader = Files.newBufferedReader(configFilePath)) {
            log.debug("Reader is ready: " + reader.ready());
            Gson gson = new Gson();
            Module[] modules = gson.fromJson(reader, Module[].class);

            Arrays.stream(modules)
                    .peek(module -> log.debug(module.getName() + " = " + (module.isClosed() ? "closed" : "open")))
                    .filter(Module::isClosed)
                    .forEach(m -> Setter.setModuleStatusClosed(applicationRoot, m.getId()));

        } catch (IOException e) {
            log.debug(e.getMessage());
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
