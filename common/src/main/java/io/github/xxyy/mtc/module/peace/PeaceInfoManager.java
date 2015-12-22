package io.github.xxyy.mtc.module.peace;

import com.google.common.base.Joiner;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PeaceInfoManager {

    private static final int MAXIMUM_CACHE_SIZE = 200;
    private static final Joiner SEMICOLON_JOINER = Joiner.on(';');

    @NotNull
    private final PeaceModule module;
    private final ExecutorService asyncSaverThread = Executors
        .newSingleThreadScheduledExecutor(
            new ThreadFactoryBuilder()
                .setNameFormat("PeaceInfo Save Thread #%d")
                .build());
    private PreparedStatement fetchStmt;
    private PreparedStatement saveStmt;
    private boolean disableFlush = false;

    private final LoadingCache<UUID, PeaceInfo> peaceInfoCache = CacheBuilder.newBuilder()
        .initialCapacity(30)
        .concurrencyLevel(2)
        .maximumSize(MAXIMUM_CACHE_SIZE)
        .removalListener(new RemovalListener<UUID, PeaceInfo>() {
            @Override
            public void onRemoval(@NotNull RemovalNotification<UUID, PeaceInfo> notification) {
                if (disableFlush) {
                    return;
                }
                switch (notification.getCause()) {
                    case EXPIRED:
                    case COLLECTED: //<- should not happen, but to be sure
                    case EXPLICIT:
                    case SIZE: {
                        PeaceInfo value = notification.getValue();
                        if (value != null && value.isDirty()) {
                            asyncSaverThread.execute(new FlushRunnable(value));
                        }
                        break;
                    }
                    default:
                        break;
                }
            }
        })
        .build(CacheLoader.from(this::fetch));

    public PeaceInfoManager(@NotNull PeaceModule module) throws SQLException {
        this.module = module;
        createFetchStmt();
        createSaveStmt();
    }

    private void createFetchStmt() throws SQLException {
        fetchStmt = module.getPlugin().getSql().prepareStatement("SELECT * FROM `mt_main`.`module_peaceinfo` WHERE `uuid`=?");
    }

    private void createSaveStmt() throws SQLException {
        saveStmt = module.getPlugin().getSql().prepareStatement("INSERT INTO `mt_main`.`module_peaceinfo` (`uuid`,`peaceWith`,`requestsGot`,`requestsSent`) VALUES (?,?,?,?) " +
            "ON DUPLICATE KEY UPDATE SET `peaceWith`=?,`requestsGot`=?,`requestsSent`=? WHERE `uuid`=?");
    }

    /**
     * Waits until all save tasks are stopped or 10 seconds elapsed
     * (if really 10 seconds elapse, there is an error somewhere)
     */
    void syncStop() {
        try {
            fetchStmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            asyncSaverThread.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * If you are planning to use {@link #get(UUID)} after this method, consider using {@link #getIfCached(UUID)}, as its faster
     *
     * @param uuid the uuid to get the {@link PeaceInfo} from.
     * @return whether the {@link PeaceInfo} of the given uuid is cached currently
     */
    public boolean isCached(UUID uuid) {
        return peaceInfoCache.getIfPresent(uuid) != null;
    }

    /**
     * @param uuid the uuid to get the {@link PeaceInfo} from.
     * @return the cache {@link PeaceInfo} or null if its not cached
     */
    @Nullable
    public PeaceInfo getIfCached(UUID uuid) {
        return peaceInfoCache.getIfPresent(uuid);
    }

    /**
     * Gets either the cached value or performes a database lookup
     *
     * @param uuid the uuid to get the {@link PeaceInfo} from.
     * @return the PeaceInfo
     */
    public PeaceInfo get(UUID uuid) {
        return peaceInfoCache.getUnchecked(uuid);
    }

    /**
     * Gets either the cached value or performes a database lookup and then removes it from the cache.
     *
     * @param uuid the uuid to get the {@link PeaceInfo} from.
     * @return the PeaceInfo
     */
    public PeaceInfo getAndFlush(UUID uuid) {
        PeaceInfo peaceInfo = get(uuid);
        flush(uuid);
        return peaceInfo;
    }

    /**
     * Removes the {@link PeaceInfo} from the cache and saves it asynchroniously to the database
     *
     * @param uuid the uuid which {@link PeaceInfo} should be removed
     */
    public void flush(UUID uuid) {
        peaceInfoCache.invalidate(uuid); //this calls our custom removal listener which saves if the PeaceInfo is modified
    }

    /**
     * Removes the {@link PeaceInfo} from the cache and does not save it
     *
     * @param uuid the uuid which {@link PeaceInfo} should be removed
     */
    public void discard(UUID uuid) {
        disableFlush = true;
        peaceInfoCache.invalidate(uuid);
        disableFlush = false;
    }

    /**
     * Clears the {@link PeaceInfo} cache and saves it asynchroniously to the database
     */
    public void flushAll() {
        peaceInfoCache.invalidateAll(); //also saves if needed
    }

    /**
     * Clears the {@link PeaceInfo} cache and does not save it
     */
    public void discardAll() {
        disableFlush = true;
        peaceInfoCache.invalidateAll();
        disableFlush = false;
    }

    private PeaceInfo fetch(UUID uuid) { //TODO implement
        ArrayList<UUID> peaceWith = new ArrayList<>(), requestsGot = new ArrayList<>(), requestsSent = new ArrayList<>();
        try {
            if (fetchStmt == null) {
                createFetchStmt();
            }
            fetchStmt.clearParameters();
            fetchStmt.setString(1, uuid.toString());
            try (ResultSet rs = fetchStmt.executeQuery()) {
                if (rs.next()) {
                    String[] peaceWithArray = rs.getString("peaceWith").split(";");
                    convertUuidAndAdd(peaceWithArray, peaceWith);

                    String[] requestsGotArray = rs.getString("requestsGot").split(";");
                    convertUuidAndAdd(requestsGotArray, requestsGot);

                    String[] requestsSentArray = rs.getString("requestsSent").split(";");
                    convertUuidAndAdd(requestsSentArray, requestsSent);
                    return new PeaceInfo(uuid, peaceWith, requestsGot, requestsSent);
                } else {
                    PeaceInfo peaceInfo = createNew(uuid);
                    peaceInfo.setDirty();
                    asyncSaverThread.execute(new FlushRunnable(peaceInfo)); //TODO should we save empty peace data in sql?
                    return peaceInfo;
                }
            } catch (SQLException ex) {
                module.getPlugin().getLogger().severe("[PeaceModule] Error while fetching peace data for " + uuid);
                ex.printStackTrace();
            }
        } catch (SQLException ex) {
            module.getPlugin().getLogger().severe("[PeaceModule] Error while fetching peace data for " + uuid);
            ex.printStackTrace();
        }
        return null;
    }

    private void save(PeaceInfo pi) {
        UUID uuid = pi.getUuid();

        List<UUID> peaceWith = pi.getPeaceWithInternal();
        String peaceWithStr = SEMICOLON_JOINER.join(peaceWith);

        List<UUID> requestsGot = pi.getRequestsGotInternal();
        String requestsGotStr = SEMICOLON_JOINER.join(requestsGot);

        List<UUID> requestsSent = pi.getRequestsSentInternal();
        String requestsSentStr = SEMICOLON_JOINER.join(requestsSent);

        try {
            saveStmt.setString(1, uuid.toString());
            saveStmt.setString(2, peaceWithStr);
            saveStmt.setString(3, requestsGotStr);
            saveStmt.setString(4, requestsSentStr);
            saveStmt.setString(5, peaceWithStr);
            saveStmt.setString(6, requestsGotStr);
            saveStmt.setString(7, requestsSentStr);
            saveStmt.setString(8, uuid.toString());
            int affectedRows = saveStmt.executeUpdate();
            if (affectedRows != 0 && affectedRows != 1) {
                module.getPlugin().getLogger().severe("[PeaceModule] Error while saving peace data for " + uuid + " : affected rows is not 1 or 0 but " + affectedRows);
            }
            pi.setDirty(false);
        } catch (SQLException ex) {
            module.getPlugin().getLogger().severe("[PeaceModule] Error while saving peace data for " + uuid + " :");
            ex.printStackTrace();
        }
    }

    private static void convertUuidAndAdd(String[] toAdd, ArrayList<UUID> list) {
        list.ensureCapacity(toAdd.length);
        for (String uuidStr : toAdd) {
            list.add(UUID.fromString(uuidStr));
        }
    }

    private static PeaceInfo createNew(UUID uuid) {
        return new PeaceInfo(uuid, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    private final class FlushRunnable implements Runnable {

        @NotNull
        private final PeaceInfo peaceInfo;

        private FlushRunnable(@NotNull PeaceInfo peaceInfo) {
            this.peaceInfo = peaceInfo;
        }

        @Override
        public void run() {
            if (!peaceInfo.isDirty()) {
                return;
            }
            save(peaceInfo);
        }
    }

    @NotNull
    public PeaceModule getModule() {
        return module;
    }
}
