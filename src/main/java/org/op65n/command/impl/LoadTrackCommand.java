package org.op65n.command.impl;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.op65n.Bootstrap;
import org.op65n.command.define.DiscordCommand;

public final class LoadTrackCommand implements DiscordCommand {

    @Override
    public @NotNull String command() {
        return "play";
    }

    @Override
    public void execute(@NotNull MessageReceivedEvent event) {
        final Message message = event.getMessage();
        final MessageChannel channel = message.getChannel();

        final String[] contents = message.getContentRaw().split(" ");
        if (contents.length < 2) {
            channel.sendMessage(
                    "The given arguments length does not suffice the required length!"
            ).queue();
            return;
        }

        final String trackString = contents[1];
        Bootstrap.audioProcessor().loadAudio(
                message, trackString, true
        );
    }

}
