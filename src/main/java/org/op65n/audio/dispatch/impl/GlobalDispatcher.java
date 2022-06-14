package org.op65n.audio.dispatch.impl;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.jetbrains.annotations.NotNull;
import org.op65n.audio.dispatch.MessageDispatcher;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public final class GlobalDispatcher implements MessageDispatcher {

    private final AtomicReference<MessageChannel> outputChannel = new AtomicReference<>();

    @Override
    public void outputChannel(final MessageChannel channel) {
        this.outputChannel.set(channel);
    }

    @Override
    public void sendMessage(final @NotNull String message, final @NotNull Consumer<Message> success, final @NotNull Consumer<Throwable> failure) {
        final MessageChannel channel = outputChannel.get();
        if (channel == null) {
            return;
        }

        channel.sendMessage(message).queue(success, failure);
    }

    @Override
    public void sendMessage(final @NotNull String message) {
        final MessageChannel channel = outputChannel.get();
        if (channel == null) {
            return;
        }


        channel.sendMessage(message).queue();
    }

}