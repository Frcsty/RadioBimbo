package org.op65n.listener;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.op65n.Bootstrap;

public final class GuildVoiceLeaveListener extends ListenerAdapter {

    @Override
    public void onGuildVoiceLeave(final @NotNull GuildVoiceLeaveEvent event) {
        final User user = event.getEntity().getUser();

        Bootstrap.audioProcessor().removeUserFromVoiceCache(user);
    }

}
