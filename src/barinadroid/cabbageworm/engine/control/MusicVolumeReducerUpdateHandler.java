package barinadroid.cabbageworm.engine.control;

import org.andengine.audio.music.Music;
import org.andengine.engine.handler.IUpdateHandler;

public class MusicVolumeReducerUpdateHandler implements IUpdateHandler
{
	private final float timeToReduce;
	private final Music music;
	private float timeRemain;
	private boolean isActive;
	private IOnVolumeReachedZeroCallBack callBack;

	public MusicVolumeReducerUpdateHandler(Music music, float timeToReduce, IOnVolumeReachedZeroCallBack callback)
	{
		super();
		this.music = music;
		this.timeToReduce = timeToReduce;
		this.callBack = callback;
		this.isActive = true;
	}

	public MusicVolumeReducerUpdateHandler(Music music, float timeToReduce)
	{
		this(music, timeToReduce, null);
	}

	public MusicVolumeReducerUpdateHandler(Music music)
	{
		this(music, 3, null);
	}

	@Override
	public void onUpdate(float pSecondsElapsed)
	{
		timeRemain -= pSecondsElapsed;
		reduce();
	}

	private void reduce()
	{
		music.setVolume(timeRemain / timeToReduce);
		if(music.getVolume() <= 0)
		{
			this.isActive = false;
			if(this.callBack != null)
				this.callBack.onVolumeReachedZero();
		}
	}

	public void forceReduce()
	{
		timeRemain = 0;
		reduce();
	}

	@Override
	public void reset()
	{
		music.setVolume(1);
		timeRemain = timeToReduce;
		callBack = null;
		isActive = true;
	}

	public boolean isActive()
	{
		return isActive;
	}

	public void setOnVolumeReachedZeroCallBack(IOnVolumeReachedZeroCallBack callBack)
	{
		this.callBack = callBack;
	}

	public interface IOnVolumeReachedZeroCallBack
	{
		void onVolumeReachedZero();
	}
}