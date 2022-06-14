package org.op65n.command;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.op65n.command.define.DiscordCommand;
import org.op65n.command.impl.*;

import java.util.Optional;
import java.util.Set;

public final class CommandProcessor extends ListenerAdapter {

    private static final String PREFIX = "-";
    private static final Set<DiscordCommand> REGISTERED_COMMANDS = Set.of(
            new LoadTrackCommand(), new SkipCommand(), new DisconnectCommand(), new QueueCommand()
    );

    @Override
    public void onMessageReceived(final @NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        final Message message = event.getMessage();
        final String messageLine = message.getContentRaw();
        if (!messageLine.startsWith(PREFIX)) return;

        final String commandLine = messageLine.split(" ")[0].replace("-", "");
        final Optional<DiscordCommand> command = REGISTERED_COMMANDS.stream().filter(it -> {
            final String commandString = it.command();

            return commandLine.equalsIgnoreCase(commandString);
        }).findFirst();

        if (command.isEmpty()) return;
        command.get().execute(event);
    }

}
