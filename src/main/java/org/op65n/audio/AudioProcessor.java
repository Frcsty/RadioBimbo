package org.op65n.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.lava.common.tools.DaemonThreadFactory;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;
import org.op65n.audio.dispatch.MessageDispatcher;
import org.op65n.audio.dispatch.impl.GlobalDispatcher;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats.COMMON_PCM_S16_BE;

public final class AudioProcessor {

    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1, new DaemonThreadFactory("bot-daemon"));
    private final AudioPlayerManager audioPlayerManager = new DefaultAudioPlayerManager();
    private final MessageDispatcher messageDispatcher = new GlobalDispatcher();

    private final Map<User, VoiceChannel> voiceCache = new HashMap<>();
    private final TrackScheduler trackScheduler;

    public AudioProcessor() {
        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);

        this.audioPlayerManager.getConfiguration().setOutputFormat(COMMON_PCM_S16_BE);

        this.trackScheduler = new TrackScheduler(this);
    }

    public void loadAudio(final @NotNull Message message, final @NotNull String audioIdentifier, final boolean play) {
        this.messageDispatcher.outputChannel(message.getChannel());
        if (!voiceChannelJoin(message)) {
            return;
        }

        this.audioPlayerManager.loadItemOrdered(1, audioIdentifier, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(final @NotNull AudioTrack track) {
                trackScheduler.add(track);

                if (play) {
                    trackScheduler.play(message);
                    return;
                }

                messageDispatcher.sendMessage(
                        "Track has been loaded to the queue!"
                );
            }

            @Override
            public void playlistLoaded(final @NotNull AudioPlaylist playlist) {
                for (final AudioTrack track : playlist.getTracks()) {
                    trackScheduler.add(track);
                }

                messageDispatcher.sendMessage(
                        "The playlist has been loaded to the queue!"
                );
            }

            @Override
            public void noMatches() {
                messageDispatcher.sendMessage(
                        "The given song link does not appear to have a valid song!"
                );
            }

            @Override
            public void loadFailed(final @NotNull FriendlyException throwable) {
                messageDispatcher.sendMessage(
                        "Everything has gone to shit, a bot restart is recommended!"
                );
            }
        });
    }

    public boolean voiceChannelJoin(final @NotNull Message message) {
        final Guild guild = message.getGuild();
        final User user = message.getAuthor();

        final AudioManager audioManager = guild.getAudioManager();
        if (audioManager.isConnected()) {
            return true;
        }

        final VoiceChannel joinedChannel = this.voiceCache.get(user);
        if (joinedChannel == null) {
            this.messageDispatcher.sendMessage(
                    "Please join a voice chat, before queuing songs!"
            );
            return false;
        }

        for (final VoiceChannel voiceChannel : guild.getVoiceChannels()) {
            if (!joinedChannel.equals(voiceChannel)) {
                continue;
            }

            audioManager.setSendingHandler(new AudioPlayerSendHandler(this.trackScheduler.audioPlayer()));
            audioManager.openAudioConnection(voiceChannel);
            return true;
        }

        return false;
    }

    public void disconnect(final @NotNull Message message) {
        final Guild guild = message.getGuild();
        final AudioManager audioManager = guild.getAudioManager();
        if (!audioManager.isConnected()) {
            this.messageDispatcher.sendMessage(
                    "The bot is not connected to a voice channel!"
            );
            return;
        }

        audioManager.closeAudioConnection();
        this.trackScheduler.stop();
        this.messageDispatcher.sendMessage(
                "The bot has been disconnected from the voice channel!"
        );
    }

    public ScheduledExecutorService scheduledExecutorService() {
        return this.scheduledExecutorService;
    }

    public AudioPlayerManager audioPlayerManager() {
        return this.audioPlayerManager;
    }

    public MessageDispatcher messageDispatcher() {
        return this.messageDispatcher;
    }

    public TrackScheduler trackScheduler() {
        return this.trackScheduler;
    }

    public void addUserToVoiceCache(final User user, final VoiceChannel channel) {
        this.voiceCache.put(user, channel);
    }

    public void removeUserFromVoiceCache(final User user) {
        this.voiceCache.remove(user);
    }

}
