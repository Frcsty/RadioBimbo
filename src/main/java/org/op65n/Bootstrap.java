package org.op65n;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.op65n.audio.AudioProcessor;
import org.op65n.command.CommandProcessor;
import org.op65n.listener.GuildVoiceJoinListener;
import org.op65n.listener.GuildVoiceLeaveListener;

import static net.dv8tion.jda.api.requests.GatewayIntent.GUILD_MESSAGES;
import static net.dv8tion.jda.api.requests.GatewayIntent.GUILD_VOICE_STATES;

public final class Bootstrap {

    private static final AudioProcessor AUDIO_PROCESSOR = new AudioProcessor();
    private static JDA discord;

    public static void main(final String[] arguments) throws Exception {
        final String token = arguments.length != 1 ? null : arguments[0];

        if (token == null) {
            throw new RuntimeException("When running the executable, no token was provided!");
        }

        discord = JDABuilder
                .create(token, GUILD_MESSAGES, GUILD_VOICE_STATES)
                .disableCache(CacheFlag.ACTIVITY, CacheFlag.EMOTE, CacheFlag.CLIENT_STATUS, CacheFlag.ONLINE_STATUS)
                .setActivity(Activity.watching("some big booty bitches"))
                .build();

        discord.addEventListener(new CommandProcessor(), new GuildVoiceJoinListener(), new GuildVoiceLeaveListener());
    }

    public static AudioProcessor audioProcessor() {
        return AUDIO_PROCESSOR;
    }

    public static JDA discord() { return discord; }

}
