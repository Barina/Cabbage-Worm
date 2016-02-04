package barinadroid.cabbageworm.engine.control;

import org.andengine.audio.sound.Sound;
import barinadroid.cabbageworm.GameActivity;
import barinadroid.cabbageworm.tools.LogWorm;

public class SoundHolder extends AudioHolder
{
	private Sound sSound;

	public SoundHolder(GameActivity activity, String path)
	{
		super(activity);
		this.sSound = loadSound(path);
	}

	@Override
	public void play()
	{
		if(sSound != null)
		{
			sSound.play();
			setState(AudioState.Playing);
		}
	}

	@Override
	public void pause()
	{
		if(sSound != null && !sSound.isReleased() && isPlaying())
		{
			sSound.pause();
			setState(AudioState.Paused);
		}
	}

	@Override
	public void resume()
	{
		if(sSound != null && !sSound.isReleased())
		{
			if(isPaused())
			{
				sSound.resume();
				setState(AudioState.Playing);
			}
		}
	}

	@Override
	public void stop(boolean force)
	{
		if(sSound != null)
			if(isPlaying() || isPaused())
			{
				sSound.stop();
				setState(AudioState.Stopped);
			}
	}

	@Override
	public void unload()
	{
		try
		{
			if(sSound != null && !sSound.isReleased())
				sSound.release();
		}
		catch(Exception e)
		{
			LogWorm.w("Can't release Sound for some reason.", e);
		}
	}

	@Override
	float getVolume()
	{
		if(sSound != null && !sSound.isReleased())
			return sSound.getVolume();
		return 0;
	}
}