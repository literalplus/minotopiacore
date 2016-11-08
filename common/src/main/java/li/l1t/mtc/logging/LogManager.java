/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.logging;

import com.google.common.base.Preconditions;
import li.l1t.mtc.api.MTCPlugin;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.spi.LoggerContext;
import org.bukkit.Bukkit;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * This class helps with registering and managing loggers for MTC modules, as well as initialising a
 * custom Log4J context specifically for MTC with a mixture of pre-defined and user-defined
 * behaviour. If a plugin has not yet been set at initialisation time, static sane defaults are used
 * for the data folder.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 07/07/15
 */
public class LogManager {
    private static LoggerContext context;
    private static MTCPlugin plugin;

    private LogManager() {

    }

    /**
     * Obtains a logger in the MTC context for a specific class.
     *
     * @param clazz the class to use
     * @return the obtained logger
     */
    public static Logger getLogger(Class<?> clazz) {
        return getLogger(clazz.getName());
    }

    /**
     * Obtains a logger in the MTC context for a specific id.
     *
     * @param id the id to use
     * @return the obtained logger
     */
    public static Logger getLogger(String id) {
        if (context == null) {
            initialiseContext();
        }

        return context.getLogger(id);
    }

    /**
     * Sets the plugin used by the log manager. Once set, it cannot be re-set.
     *
     * @param mtcPlugin the plugin to use
     */
    public static void setPlugin(MTCPlugin mtcPlugin) {
        Preconditions.checkState(plugin == null || mtcPlugin == null || mtcPlugin == plugin, "Cannot re-set singleton plugin!");
        plugin = mtcPlugin;
    }

    /**
     * Initialises the MTC {@link LoggerContext logger context} for Log4J. This works by merging the
     * pre-defined static configuration (log4j2-mtc.xml) and the customisable configuration
     * (log4j2-custom.xml) from the MTC data folder. If no plugin is accessible to get the data
     * folder from, {@code plugins/MinoTopiaCore/} relative to the current working directory is
     * used. Initialising logger contexts can take quite some time. You shouldn't need to call this
     * manually since {@link #getLogger(Class)} calls this method on demand.
     */
    private static void initialiseContext() {
        if (System.getProperty("li.l1t.mtc.unittest") != null) { //Unit Tests
            context = org.apache.logging.log4j.LogManager.getContext();
            return;
        }

        Preconditions.checkState(context == null, "Already initialised!");
        if (plugin == null) {
            Bukkit.getLogger().warning("[MTC] Initialising log4j2 context before plugin has been set!");
            Bukkit.getLogger().warning("[MTC] Called from: " + Thread.currentThread().getStackTrace()[2].toString());
        }
        System.setProperty("Log4jContextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
        System.setProperty("mtc.datadir", getDataFolder().getAbsolutePath());
        System.setProperty("mtc.logsdir", getDataFolder().getAbsolutePath() + "/logs2/");

        File configFile = new File(plugin.getDataFolder(), "log4j2-mtc.xml");
        prepareLog4jConfig(configFile);

        context = org.apache.logging.log4j.LogManager.getContext(
                LogManager.class.getClassLoader(), false, configFile.toURI()
        );
    }

    private static File getDataFolder() {
        if (plugin == null) {
            return new File("plugins/MinoTopiaCore/");
        } else {
            return plugin.getDataFolder();
        }
    }

//     the rest of this file is extracting and merging Log4J configuration files

    private static boolean prepareLog4jConfig(File configFile) {
        try (InputStream configIn = plugin.getResource("log4j2-mtc.xml")) {

            saveXML(
                    mergeLog4j2Configs(extractResource("log4j2-custom.xml"), configIn),
                    configFile
            );

        } catch (IOException | TransformerException e) {
            plugin.getLogger().log(java.util.logging.Level.SEVERE, "Error copying log4j2 config", e);
        } catch (ParserConfigurationException | SAXException e) {
            plugin.getLogger().log(java.util.logging.Level.SEVERE, "Could not parse log4j2 configuration!", e);
        }

        return configFile.exists();
    }

    private static File extractResource(String filename) throws IOException {
        File file = new File(plugin.getDataFolder(), filename);
        Path filePath = file.toPath();

        if (!file.exists()) {
            Files.createDirectories(filePath.getParent());
            Files.createFile(filePath);
            try (InputStream is = plugin.getResource(filename)) {
                Files.copy(is, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
        }

        return file;
    }

    private static void saveXML(Document doc, File file) throws TransformerException, IOException {
        if (!file.exists()) {
            Files.createFile(file.toPath());
        }

        doc.normalizeDocument();

        TransformerFactory.newInstance().newTransformer()
                .transform(new DOMSource(doc), new StreamResult(file));
    }

    private static Document mergeLog4j2Configs(File from, InputStream to)
            throws ParserConfigurationException, SAXException, IOException, TransformerException {

        /*
        Don't fucking touch any of this. It works now. Change one line, it breaks. I can speak out of hour-long
        experience with this shitty XML 'API'. Seriously. Go away right now.
         */

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document fromDoc = builder.parse(from);
        Element fromRoot = fromDoc.getDocumentElement();
        Document toDoc = builder.parse(to);
        Element toRoot = toDoc.getDocumentElement();

        Node node = fromRoot.getFirstChild();
        Node nextNode;
        do {
            nextNode = node.getNextSibling();
            NodeList childNodes = node.getChildNodes();
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                NodeList toNodes = toRoot.getElementsByTagName(node.getNodeName());
                if (toNodes.getLength() != 0) {
                    Node toNode = toNodes.item(0);
                    for (int i = 0; i < childNodes.getLength(); i++) {
                        Node child = childNodes.item(i);
                        Node nextChild;
                        do {
                            nextChild = child.getNextSibling();
                            toNode.appendChild(toDoc.importNode(child, true));
                        } while ((child = nextChild) != null);
                    }
                    continue;
                }
            }
            toDoc.adoptNode(node);
            toRoot.appendChild(node);
        } while ((node = nextNode) != null);

        return toDoc;
    }


}
