package org.op65n.command.impl;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.op65n.Bootstrap;
import org.op65n.command.define.DiscordCommand;

import java.awt.*;
import java.util.List;

public final class QueueCommand implements DiscordCommand {

    @Override
    public @NotNull String command() {
        return "queue";
    }

    @Override
    public void execute(final @NotNull MessageReceivedEvent event) {
        final MessageChannel channel = event.getChannel();

        channel.sendMessageEmbeds(
                constructQueueMessage(Bootstrap.audioProcessor().trackScheduler().queue())
        ).queue();
    }

    private MessageEmbed constructQueueMessage(final List<AudioTrack> queue) {
        final EmbedBuilder builder = new EmbedBuilder();

        builder.setColor(Color.CYAN);
        builder.setAuthor("Current Track Queue");

        final StringBuilder stringBuilder = new StringBuilder();
        for (int index = 0; index < 10; index++) {
            final AudioTrack track = queue.size() > index ? queue.get(index) : null;
            if (track == null) {
                continue;
            }

            stringBuilder.append("-> ").append(track.getInfo().title).append("\n");
        }

        if (queue.isEmpty()) {
            stringBuilder.append("There are no tracks within the queue!");
        }

        return builder.setDescription(stringBuilder.toString()).build();
    }

}
