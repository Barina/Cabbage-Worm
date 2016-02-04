package barinadroid.cabbageworm.engine.model;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;

public class WormLink implements IUpdateHandler
{
	private final WormLink parent;
	private final Sprite sprite;
	private final Body linkBody;
	private final PhysicsConnector physicsConnector;
	private MouseJointDef jointDef;
	private MouseJoint joint;

	public WormLink(WormLink parent, Sprite sprite, Body linkBody, PhysicsConnector physicsConnector, PhysicsWorld world)// , BodyChase chase)// ,SpriteChaseModifier moveModifier)
	{
		this.parent = parent;
		this.sprite = sprite;
		this.linkBody = linkBody;
		this.physicsConnector = physicsConnector;
		if(parent != null)
		{
			this.jointDef = new MouseJointDef();
			this.jointDef.bodyA = parent.getLinkBody();
			this.jointDef.bodyB = linkBody;
			this.jointDef.maxForce = Float.MAX_VALUE;
			this.jointDef.collideConnected = false;
			this.jointDef.target.set(parent.getLinkBody().getPosition());
			this.joint = (MouseJoint)world.createJoint(jointDef);
		}
	}

	@Override
	public void onUpdate(float pSecondsElapsed)
	{
		if(parent != null)
			setTarget(parent.getLinkBody().getPosition());
	}

	@Override
	public void reset()
	{}

	private void setTarget(Vector2 target)
	{
		if(this.joint != null)
			this.joint.setTarget(target);
	}

	public void destroyJoint(PhysicsWorld world)
	{
		if(this.joint != null)
		{
			world.destroyJoint(this.joint);
			this.joint = null;
		}
	}

	public WormLink getParent()
	{
		return parent;
	}

	public Sprite getSprite()
	{
		return sprite;
	}

	public Body getLinkBody()
	{
		return linkBody;
	}

	public PhysicsConnector getPhysicsConnector()
	{
		return physicsConnector;
	}
}