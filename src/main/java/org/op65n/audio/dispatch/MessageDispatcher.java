package org.op65n.audio.dispatch;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface MessageDispatcher {
    void outputChannel(final MessageChannel channel);

    void sendMessage(final @NotNull String message, final @NotNull Consumer<Message> success, final @NotNull Consumer<Throwable> failure);

    void sendMessage(final @NotNull String message);
}
