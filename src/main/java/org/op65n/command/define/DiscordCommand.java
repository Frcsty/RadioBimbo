package org.op65n.command.define;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

public interface DiscordCommand {

    @NotNull String command();

    void execute(final @NotNull MessageReceivedEvent event);

}
