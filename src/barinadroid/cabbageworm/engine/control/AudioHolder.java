package barinadroid.cabbageworm.engine.control;

import java.io.IOException;
import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import barinadroid.cabbageworm.GameActivity;
import barinadroid.cabbageworm.tools.LogWorm;

public abstract class AudioHolder
{
	private AudioState aState;
	private final GameActivity aActivity;

	protected AudioHolder(GameActivity activity)
	{
		this.aActivity = activity;
		this.aState = AudioState.Stopped;
	}

	protected Sound loadSound(String path)
	{
		try
		{
			return SoundFactory.createSoundFromAsset(getActivity().getEngine().getSoundManager(), getActivity(), path);
		}
		catch(final IOException e)
		{
			LogWorm.e("Can't load sound file '" + path + "'", e);
		}
		return null;
	}

	protected Music loadMusic(String path)
	{
		try
		{
			return MusicFactory.createMusicFromAsset(getActivity().getEngine().getMusicManager(), getActivity(), path);
		}
		catch(final IOException e)
		{
			LogWorm.e("Can't load music file '" + path + "'", e);
		}
		return null;
	}

	public final boolean isPlaying()
	{
		return this.aState == AudioState.Playing;
	}

	public final boolean isPaused()
	{
		return this.aState == AudioState.Paused;
	}

	public final boolean isStopped()
	{
		return this.aState == AudioState.Stopped;
	}

	protected final void setState(AudioState state)
	{
		this.aState = state;
	}

	protected GameActivity getActivity()
	{
		return this.aActivity;
	}

	abstract void play();

	abstract void pause();

	abstract void resume();

	abstract void stop(boolean force);

	abstract void unload();

	abstract float getVolume();
}