package barinadroid.cabbageworm.engine.control;

import org.andengine.audio.music.Music;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import barinadroid.cabbageworm.GameActivity;
import barinadroid.cabbageworm.engine.control.MusicVolumeReducerUpdateHandler.IOnVolumeReachedZeroCallBack;
import barinadroid.cabbageworm.tools.LogWorm;

public class MusicHolder extends AudioHolder
{
	private final Music mMusic;
	private MusicVolumeReducerUpdateHandler mMusicReducerHandler;

	public MusicHolder(GameActivity activity, String path)
	{
		this(activity, path, 3, null);
	}

	public MusicHolder(GameActivity activity, String path, float stopDuration)
	{
		this(activity, path, stopDuration, null);
	}

	public MusicHolder(GameActivity activity, String path, final MusicHolder loop)
	{
		this(activity, path, 3, loop);
	}

	public MusicHolder(GameActivity activity, String path, float stopDuration, final MusicHolder loop)
	{
		super(activity);
		if((this.mMusic = loadMusic(path)) != null)
		{
			this.mMusicReducerHandler = new MusicVolumeReducerUpdateHandler(this.mMusic, stopDuration);
			if(loop != null)
			{
				mMusic.setLooping(false);
				mMusic.setOnCompletionListener(new OnCompletionListener()
				{
					@Override
					public void onCompletion(MediaPlayer mp)
					{
						setState(AudioState.Stopped);
						if(mMusic.getVolume() >= 1)
							loop.play(mMusic.getVolume());
					}
				});
			}
			else
				mMusic.setLooping(true);
		}
	}

	@Override
	public void play()
	{
		play(1);
	}

	public void play(float vol)
	{
		if(mMusic != null)
		{
			getActivity().getEngine().unregisterUpdateHandler(mMusicReducerHandler);
			mMusic.setVolume(vol);
			mMusic.play();
			setState(AudioState.Playing);
		}
	}

	@Override
	public void pause()
	{
		if(mMusicReducerHandler != null && mMusicReducerHandler.isActive())
			mMusicReducerHandler.forceReduce();
		else
			if(isPlaying() || isPaused())
				if(mMusic != null && !mMusic.isReleased())
				{
					mMusic.pause();
					setState(AudioState.Paused);
				}
	}

	@Override
	public void resume()
	{
		if(mMusic != null && !mMusic.isReleased())
			if(isPaused())
			{
				mMusic.resume();
				setState(AudioState.Playing);
			}
	}

	@Override
	public void stop(boolean force)
	{
		if(mMusic != null)
			if(isPlaying() || isPaused())
			{
				setState(AudioState.Stopped);
				if(force)
				{
					if(mMusicReducerHandler.isActive())
						mMusicReducerHandler.forceReduce();
					else
					{
						mMusic.pause();
						mMusic.seekTo(0);
						setState(AudioState.Stopped);
					}
				}
				else
				{
					mMusicReducerHandler.reset();
					getActivity().getEngine().registerUpdateHandler(mMusicReducerHandler);
					mMusicReducerHandler.setOnVolumeReachedZeroCallBack(new IOnVolumeReachedZeroCallBack()
					{
						@Override
						public void onVolumeReachedZero()
						{
							mMusic.pause();
							mMusic.seekTo(0);
							setState(AudioState.Stopped);
							getActivity().getEngine().unregisterUpdateHandler(mMusicReducerHandler);
						}
					});
				}
			}
	}

	@Override
	public void unload()
	{
		try
		{
			if(mMusic != null && !mMusic.isReleased())
				mMusic.release();
		}
		catch(Exception e)
		{
			LogWorm.w("Can't release Music for some reason.", e);
		}
	}

	@Override
	public float getVolume()
	{
		if(mMusic != null && !mMusic.isReleased())
			return mMusic.getVolume();
		return 0;
	}
}