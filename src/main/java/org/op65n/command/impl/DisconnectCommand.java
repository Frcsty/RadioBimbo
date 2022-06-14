package org.op65n.command.impl;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.op65n.Bootstrap;
import org.op65n.command.define.DiscordCommand;

public final class DisconnectCommand implements DiscordCommand {

    @Override
    public @NotNull String command() {
        return "disconnect";
    }

    @Override
    public void execute(final @NotNull MessageReceivedEvent event) {
        Bootstrap.audioProcessor().disconnect(event.getMessage());
    }
}
