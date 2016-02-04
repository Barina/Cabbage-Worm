package barinadroid.cabbageworm.engine.control;

import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.sprite.vbo.ITiledSpriteVertexBufferObject;
import org.andengine.opengl.shader.ShaderProgram;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.DrawType;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class TiledSpriteMenuItem extends TiledSprite implements IMenuItem
{
	private final int pId;

	public TiledSpriteMenuItem(int pId, float pX, float pY, float pWidth, float pHeight, ITiledTextureRegion pTiledTextureRegion,
			ITiledSpriteVertexBufferObject pTiledSpriteVertexBufferObject, ShaderProgram pShaderProgram)
	{
		super(pX, pY, pWidth, pHeight, pTiledTextureRegion, pTiledSpriteVertexBufferObject, pShaderProgram);
		this.pId = pId;
	}

	public TiledSpriteMenuItem(int pId, float pX, float pY, float pWidth, float pHeight, ITiledTextureRegion pTiledTextureRegion,
			ITiledSpriteVertexBufferObject pTiledSpriteVertexBufferObject)
	{
		super(pX, pY, pWidth, pHeight, pTiledTextureRegion, pTiledSpriteVertexBufferObject);
		this.pId = pId;
	}

	public TiledSpriteMenuItem(int pId, float pX, float pY, float pWidth, float pHeight, ITiledTextureRegion pTiledTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager, DrawType pDrawType, ShaderProgram pShaderProgram)
	{
		super(pX, pY, pWidth, pHeight, pTiledTextureRegion, pVertexBufferObjectManager, pDrawType, pShaderProgram);
		this.pId = pId;
	}

	public TiledSpriteMenuItem(int pId, float pX, float pY, float pWidth, float pHeight, ITiledTextureRegion pTiledTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager, DrawType pDrawType)
	{
		super(pX, pY, pWidth, pHeight, pTiledTextureRegion, pVertexBufferObjectManager, pDrawType);
		this.pId = pId;
	}

	public TiledSpriteMenuItem(int pId, float pX, float pY, float pWidth, float pHeight, ITiledTextureRegion pTiledTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager, ShaderProgram pShaderProgram)
	{
		super(pX, pY, pWidth, pHeight, pTiledTextureRegion, pVertexBufferObjectManager, pShaderProgram);
		this.pId = pId;
	}

	public TiledSpriteMenuItem(int pId, float pX, float pY, float pWidth, float pHeight, ITiledTextureRegion pTiledTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager)
	{
		super(pX, pY, pWidth, pHeight, pTiledTextureRegion, pVertexBufferObjectManager);
		this.pId = pId;
	}

	public TiledSpriteMenuItem(int pId, float pX, float pY, ITiledTextureRegion pTiledTextureRegion,
			ITiledSpriteVertexBufferObject pTiledSpriteVertexBufferObject, ShaderProgram pShaderProgram)
	{
		super(pX, pY, pTiledTextureRegion, pTiledSpriteVertexBufferObject, pShaderProgram);
		this.pId = pId;
	}

	public TiledSpriteMenuItem(int pId, float pX, float pY, ITiledTextureRegion pTiledTextureRegion,
			ITiledSpriteVertexBufferObject pTiledSpriteVertexBufferObject)
	{
		super(pX, pY, pTiledTextureRegion, pTiledSpriteVertexBufferObject);
		this.pId = pId;
	}

	public TiledSpriteMenuItem(int pId, float pX, float pY, ITiledTextureRegion pTiledTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager, DrawType pDrawType, ShaderProgram pShaderProgram)
	{
		super(pX, pY, pTiledTextureRegion, pVertexBufferObjectManager, pDrawType, pShaderProgram);
		this.pId = pId;
	}

	public TiledSpriteMenuItem(int pId, float pX, float pY, ITiledTextureRegion pTiledTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager, DrawType pDrawType)
	{
		super(pX, pY, pTiledTextureRegion, pVertexBufferObjectManager, pDrawType);
		this.pId = pId;
	}

	public TiledSpriteMenuItem(int pId, float pX, float pY, ITiledTextureRegion pTiledTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager, ShaderProgram pShaderProgram)
	{
		super(pX, pY, pTiledTextureRegion, pVertexBufferObjectManager, pShaderProgram);
		this.pId = pId;
	}

	public TiledSpriteMenuItem(int pId, float pX, float pY, ITiledTextureRegion pTiledTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager)
	{
		super(pX, pY, pTiledTextureRegion, pVertexBufferObjectManager);
		this.pId = pId;
	}

	@Override
	public int getID()
	{
		return this.pId;
	}

	@Override
	public void onSelected()
	{
		setCurrentTileIndex(1);
	}

	@Override
	public void onUnselected()
	{
		setCurrentTileIndex(0);
	}

}