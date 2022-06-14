package org.op65n.listener;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.op65n.Bootstrap;

public final class GuildVoiceJoinListener extends ListenerAdapter {

    @Override
    public void onGuildVoiceJoin(final @NotNull GuildVoiceJoinEvent event) {
        final User user = event.getEntity().getUser();
        final VoiceChannel channel = user.getJDA().getVoiceChannelById(event.getChannelJoined().getId());
        if (channel == null) {
            return;
        }

        Bootstrap.audioProcessor().addUserToVoiceCache(user, channel);
    }

}
