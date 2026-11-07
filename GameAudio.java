package com.mycompany.maze1;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.net.URL;

final class GameAudio {

    private final Clip music = loadSound("music");
    private final Clip change = loadSound("change");
    private final Clip confirm = loadSound("confirm");
    private final Clip footsteps = loadSound("step");
    private final Clip level = loadSound("level");
    private final Clip ready = loadSound("ready");
    private final Clip lose = loadSound("lose");

    private boolean muted = false;
    private long lastStepTime = 0;

    GameAudio() {

        if (music != null) {
            music.loop(Clip.LOOP_CONTINUOUSLY);
        }

    }

    private Clip loadSound(String name) {

        URL file = GameAudio.class.getResource("/audio/" + name + ".wav");
        
        if (file == null) {

            System.err.println("Missing sound: " + name);
            return null;

        }

        Clip sound = null;

        try (AudioInputStream stream = AudioSystem.getAudioInputStream(file)) {

            sound = AudioSystem.getClip();
            sound.open(stream);
            return sound;

        } catch (Exception error) {

            if (sound != null) {

                sound.close();

            }

            System.err.println("Could not load sound: " + name);
            return null;

        }

    }

    private void play(Clip sound) {

        if (muted || sound == null) {

            return;

        }

        sound.stop();
        sound.setFramePosition(0);
        sound.start();

    }

    void cue(String name) {

        switch (name) {

            case "step":

                play(footsteps);
                break;

            case "confirm":

                play(confirm);
                break;

            case "level":

                play(level);
                break;

            case "ready":

                play(ready);
                break;

            case "lose":

                play(lose);
                break;

            default:

                play(change);
                break;

        }
    }

    void step() {

        long now = System.currentTimeMillis();
        
        if (now - lastStepTime >= 160) {
            lastStepTime = now;
            play(footsteps);
        }

    }

    void toggle() {

        muted = !muted;

        if (muted) {

            Clip[] sounds = {music, change, confirm, footsteps, level, ready, lose};

            for (Clip sound : sounds) {
                if (sound != null) {
                    sound.stop();
                }

            }

        } else if (music != null) {

            music.loop(Clip.LOOP_CONTINUOUSLY);

        }

    }

    boolean isMuted() {

        return muted;
        
    }
}