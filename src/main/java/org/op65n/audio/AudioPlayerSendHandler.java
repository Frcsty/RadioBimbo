package org.op65n.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;

import java.nio.ByteBuffer;

public class AudioPlayerSendHandler implements AudioSendHandler {
    private final AudioPlayer audioPlayer;
    private AudioFrame lastFrame;

    public AudioPlayerSendHandler(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
    }

    @Override
    public boolean canProvide() {
        lastFrame = audioPlayer.provide();
        return lastFrame != null;
    }

    @Override
    public ByteBuffer provide20MsAudio() {
        return ByteBuffer.wrap(lastFrame.getData());
    }

    @Override
    public boolean isOpus() {
        return true;
    }
}

    /*
    private final AudioPlayer audioPlayer;
    private final ByteBuffer byteBuffer;
    private final MutableAudioFrame mutableAudioFrame;

    public AudioPlayerSendHandler(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
        this.byteBuffer = ByteBuffer.allocate(12000);
        this.mutableAudioFrame = new MutableAudioFrame();
        this.mutableAudioFrame.setBuffer(byteBuffer);
    }

    @Override
    public boolean canProvide() {
        return this.audioPlayer.provide(this.mutableAudioFrame);
    }

    @Override
    public ByteBuffer provide20MsAudio() {
        return this.byteBuffer.flip();
    }

    @Override
    public boolean isOpus() {
        return true;
    }
}


     */