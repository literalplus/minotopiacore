/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.pex.bulk;

import li.l1t.common.exception.InternalException;
import li.l1t.common.exception.UserException;
import li.l1t.common.util.task.ImprovedBukkitRunnable;
import li.l1t.lanatus.api.LanatusClient;
import li.l1t.mtc.api.chat.MessageType;
import li.l1t.mtc.api.command.CommandExecution;
import li.l1t.mtc.api.command.UserPermissionException;
import li.l1t.mtc.command.BukkitExecutionExecutor;
import li.l1t.mtc.hook.XLoginHook;
import li.l1t.mtc.logging.LogManager;
import li.l1t.mtc.module.lanatus.base.MTCLanatusClient;
import li.l1t.mtc.module.lanatus.pex.LanatusAccountMigrator;
import li.l1t.mtc.module.lanatus.pex.LanatusPexModule;
import org.apache.logging.log4j.Logger;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.scheduler.BukkitTask;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;

import java.time.LocalDate;
import java.util.Collection;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Executes the command for invoking manual conversion of existing PEx data.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-11-04
 */
public class BulkMigrationCommand extends BukkitExecutionExecutor {
    private final Logger LOGGER = LogManager.getLogger(BulkMigrationCommand.class);
    private final AtomicBoolean migrationRunning = new AtomicBoolean(false);
    private final LanatusAccountMigrator migrator;
    private LanatusClient lanatus;
    private final PermissionManager pex;
    private final LanatusPexModule module;

    public BulkMigrationCommand(LanatusPexModule module, PermissionManager pex, MTCLanatusClient lanatus) {
        this.module = module;
        migrator = new LanatusAccountMigrator(lanatus);
        this.lanatus = lanatus;
        this.pex = pex;
    }

    @Override
    public boolean execute(CommandExecution exec) throws UserException, InternalException {
        requireSenderIsConsole(exec);
        if (exec.hasArg(0) && exec.arg(0).equalsIgnoreCase("start")) {
            requireNoMigrationRunning();
            invokeMigration(exec);
            module.disableBulkConversion();
        } else {
            respondUsage(exec);
        }
        return true;
    }

    private void invokeMigration(CommandExecution exec) {
        LOGGER.warn(" --- Starting bulk migration...");
        if (!migrationRunning.compareAndSet(false, true)) {
            LOGGER.warn(" --- Migration FAILED: Compare-and-set failed (false, true)");
            throw new InternalException("Es läuft bereits eine Migration!");
        }
        PexReadTask readTask = new PexReadTask(pex, this::hasImportRelevantGroup);
        readTask.getFuture()
                .thenCompose(stage(this::filterKnownUsersAsync))
                .thenCompose(stage(this::migrateUsersWithUniqueIdAsync))
                .thenCompose(stage(this::migrateNonUniqueIdUsersAsync))
                .thenAccept((leftovers) -> logLeftoverUsers(leftovers, exec));
    }

    private <I, R> Function<I, CompletionStage<R>> stage(Function<I, CompletableTask<R>> taskSupplier) {
        return input -> taskSupplier.andThen(CompletableTask::getFuture).apply(input);
    }

    private FilterLanatusKnownUsersTask filterKnownUsersAsync(Collection<PexImportUser> input) {
        LOGGER.info("Found {} users in auto-migrate groups", input.size());
        FilterLanatusKnownUsersTask task = new FilterLanatusKnownUsersTask(input, 5, lanatus);
        startTaskAsync(task);
        return task;
    }

    private KnownIdUserMigrationTask migrateUsersWithUniqueIdAsync(Collection<PexImportUser> input) {
        LOGGER.info("There are {} users left with no account in Lanatus", input.size());
        KnownIdUserMigrationTask task = new KnownIdUserMigrationTask(input, 5, lanatus, pex);
        startTaskAsync(task);
        return task;
    }

    private UsernameOnlyMigrationTask migrateNonUniqueIdUsersAsync(Collection<PexImportUser> input) {
        LOGGER.info("There are {} users left without a unique id", input.size());
        LOGGER.info("Names: {}", input.stream().map(PexImportUser::getUserName).collect(Collectors.toList()));
        UsernameOnlyMigrationTask task = new UsernameOnlyMigrationTask(input, 5, LocalDate.of(2014, 10, 31), new XLoginHook(module.getPlugin()), lanatus);
        startTaskAsync(task);
        return task;
    }

    private BukkitTask startTaskAsync(ImprovedBukkitRunnable task) {
        return task.runTaskTimerAsynchronously(module.getPlugin(), 2L);
    }

    private void logLeftoverUsers(Collection<PexImportUser> leftovers, CommandExecution exec) {
        LOGGER.info("Could not convert these users: {}", leftovers);
        if (leftovers.isEmpty()) {
            exec.respond(MessageType.RESULT_LINE_SUCCESS, "Converted all users with relevant groups.");
        } else {
            exec.respond(MessageType.LIST_HEADER, "Unable to convert these users:");
            leftovers.forEach(user -> exec.respond(MessageType.LIST_ITEM, user.toString()));
        }
    }

    private boolean hasImportRelevantGroup(PermissionUser user) {
        return migrator.findAutoConvertLanatusRank(user).isPresent();
    }

    private void requireSenderIsConsole(CommandExecution exec) {
        if (!(exec.sender() instanceof ConsoleCommandSender)) {
            throw new UserPermissionException("You are not permitted to do this.");
        }
    }

    private void requireNoMigrationRunning() {
        if (migrationRunning.get()) {
            throw new UserException("A migration is already running.");
        }
    }

    private void respondUsage(CommandExecution exec) {
        exec.respond(MessageType.RESULT_LINE, "Lanatus Manual PEx File Migration");
        respondMigrationStatus(exec);
        exec.respond(MessageType.WARNING, "Only proceed if you know exactly what you are doing!");
        exec.respondUsage("start", "", "Immediately starts the migration");
    }

    private void respondMigrationStatus(CommandExecution exec) {
        exec.respond(MessageType.RESULT_LINE, "There is currently " + (migrationRunning.get() ? "a" : "no") + " migration running.");
    }
}
