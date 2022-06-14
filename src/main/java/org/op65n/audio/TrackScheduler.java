package org.op65n.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;
import org.op65n.Bootstrap;
import org.op65n.audio.dispatch.MessageDispatcher;
import org.op65n.display.TrackDisplayBuilder;

import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public final class TrackScheduler extends AudioEventAdapter implements Runnable {

    private final BlockingDeque<AudioTrack> trackQueue = new LinkedBlockingDeque<>();
    private final AtomicReference<Message> trackMessage = new AtomicReference<>();
    private final AtomicBoolean creatingTrackMessage = new AtomicBoolean();
    private final MessageDispatcher messageDispatcher;
    private final AudioPlayer audioPlayer;

    private final AudioProcessor audioProcessor;

    public TrackScheduler(final @NotNull AudioProcessor audioProcessor) {
        this.messageDispatcher = audioProcessor.messageDispatcher();
        this.audioPlayer = audioProcessor.audioPlayerManager().createPlayer();
        this.audioProcessor = audioProcessor;

        audioProcessor.scheduledExecutorService().scheduleAtFixedRate(this, 0L, 1L, TimeUnit.SECONDS);
    }

    public void add(final @NotNull AudioTrack track) {
        this.trackQueue.add(track);
    }

    public void skip() {
        startNextTrack();

        this.messageDispatcher.sendMessage(
                "Skipped the current song!"
        );
    }

    public void play(final @NotNull Message message) {
        this.audioProcessor.voiceChannelJoin(message);
        startNextTrack();
    }

    public void stop() {
        this.audioPlayer.stopTrack();
        if (this.trackMessage.get() != null) this.trackMessage.get().delete().queue();
        this.trackQueue.clear();
    }

    private void startNextTrack() {
        final AudioTrack nextTrack = this.trackQueue.pollFirst();

        if (nextTrack == null) {
            this.audioPlayer.stopTrack();
            this.messageDispatcher.sendMessage(
                    "The queue has been depleted!"
            );
            return;
        }

        this.audioPlayer.playTrack(nextTrack);
        this.messageDispatcher.sendMessage(
                "Started playing: " + nextTrack.getInfo().title
        );
    }

    @Override
    public void onTrackStart(final @NotNull AudioPlayer player, final @NotNull AudioTrack track) {
        this.messageDispatcher.sendMessage(
                String.format("Started track %s, with volume of %s", track.getInfo().title, player.getVolume())
        );

        updateTrackBox(true);
        Bootstrap.discord().getPresence().setActivity(Activity.listening(track.getInfo().title));
    }

    @Override
    public void onPlayerPause(final @NotNull AudioPlayer player) {
        updateTrackBox(false);

        Bootstrap.discord().getPresence().setActivity(Activity.watching("some big booty bitches"));
    }

    @Override
    public void onPlayerResume(final @NotNull AudioPlayer player) {
        updateTrackBox(false);

        Bootstrap.discord().getPresence().setActivity(Activity.listening(player.getPlayingTrack().getInfo().title));
    }

    @Override
    public void onTrackEnd(final @NotNull AudioPlayer player, final @NotNull AudioTrack track, final @NotNull AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            startNextTrack();
            this.messageDispatcher.sendMessage(
                    "The track has finished."
            );
            return;
        }

        Bootstrap.discord().getPresence().setActivity(Activity.watching("some big booty bitches"));
    }

    @Override
    public void onTrackStuck(final @NotNull AudioPlayer player, final @NotNull AudioTrack track, final long thresholdMs) {
        this.messageDispatcher.sendMessage(
                "Track got stuck, starting next one."
        );
        startNextTrack();
    }

    @Override
    public void run() {
        updateTrackBox(false);
    }

    public void updateTrackBox(final boolean newMessage) {
        final AudioTrack track = this.audioPlayer.getPlayingTrack();

        if (track == null || newMessage) {
            final Message message = this.trackMessage.getAndSet(null);

            if (message != null) {
                message.delete().queue();
            }

            return;
        }

        final Message message = this.trackMessage.get();
        final String box = TrackDisplayBuilder.buildTrackBox(80, track, this.audioPlayer.isPaused(), this.audioPlayer.getVolume());

        if (message != null) {
            message.editMessage(box).queue();
            return;
        }

        if (this.creatingTrackMessage.compareAndSet(false, true)) {
            messageDispatcher.sendMessage(box, created -> {
                this.trackMessage.set(created);
                this.creatingTrackMessage.set(false);
            }, error -> this.creatingTrackMessage.set(false));
        }

    }

    public List<AudioTrack> queue() {
        return this.trackQueue.stream().toList();
    }

    public AudioPlayer audioPlayer() {
        return this.audioPlayer;
    }
}
