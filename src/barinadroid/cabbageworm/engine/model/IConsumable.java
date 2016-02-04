package barinadroid.cabbageworm.engine.model;

import com.badlogic.gdx.math.Vector2;

public interface IConsumable
{
	boolean isAvailable();
	Vector2 getCurrentLocation();
	void respawn(float scale);
	float despawn(boolean eaten);
}