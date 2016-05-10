package blocks;

import flounder.maths.matrices.*;
import flounder.maths.vectors.*;
import flounder.physics.*;

public class Block {
	public static final Vector3f ROTATION_REUSABLE = new Vector3f(0, 0, 0);
	public static final Vector3f POSITION_REUSABLE = new Vector3f(0, 0, 0);
	public static final Vector3f SCALE_REUSABLE = new Vector3f(0, 0, 0);

	private final BlockTypes type;
	private final Vector3f position;
	private final AABB aabb;
	private final BlockVisible[] faces;
	private boolean visible;

	public Block(final BlockTypes type, final Vector3f position) {
		this.type = type;
		this.position = position;
		this.aabb = new AABB();
		this.visible = false;

		updateBlockAABB(this, aabb);

		this.faces = new BlockVisible[6];
		this.faces[0] = new BlockVisible(BlockFaces.FRONT);
		this.faces[1] = new BlockVisible(BlockFaces.BACK);
		this.faces[2] = new BlockVisible(BlockFaces.LEFT);
		this.faces[3] = new BlockVisible(BlockFaces.RIGHT);
		this.faces[4] = new BlockVisible(BlockFaces.UP);
		this.faces[5] = new BlockVisible(BlockFaces.DOWN);
	}

	private static AABB updateBlockAABB(final Block block, AABB aabb) {
		aabb.setMinExtents(block.position.x - BlockTypes.BLOCK_EXTENT, block.position.y - BlockTypes.BLOCK_EXTENT, block.position.z - BlockTypes.BLOCK_EXTENT);
		aabb.setMaxExtents(block.position.x + BlockTypes.BLOCK_EXTENT, block.position.y + BlockTypes.BLOCK_EXTENT, block.position.z + BlockTypes.BLOCK_EXTENT);
		return aabb;
	}

	protected static Matrix4f blockModelMatrix(final Block block, final int face, Matrix4f modelMatrix) {
		POSITION_REUSABLE.set(block.getPosition());
		ROTATION_REUSABLE.set(0.0f, 0.0f, 0.0f);

		if (block.getFaces()[face].getStretch() != null) {
			SCALE_REUSABLE.set(block.getFaces()[face].getStretch());
		} else {
			SCALE_REUSABLE.set(BlockTypes.BLOCK_EXTENT, BlockTypes.BLOCK_EXTENT, BlockTypes.BLOCK_EXTENT);
		}

		blockPaneUpdate(block.getFaces()[face].getFace(), ROTATION_REUSABLE, POSITION_REUSABLE);

		return Matrix4f.transformationMatrix(POSITION_REUSABLE, ROTATION_REUSABLE, SCALE_REUSABLE, modelMatrix);
	}

	private static void blockPaneUpdate(final BlockFaces faces, final Vector3f rotation, final Vector3f position) {
		switch (faces) {
			case FRONT:
				rotation.x = 90.0f;
				position.z += BlockTypes.BLOCK_EXTENT;
				break;
			case BACK:
				rotation.x = 90.0f;
				position.z -= BlockTypes.BLOCK_EXTENT;
				break;
			case LEFT:
				rotation.z = 90.0f;
				position.x -= BlockTypes.BLOCK_EXTENT;
				break;
			case RIGHT:
				rotation.z = 90.0f;
				position.x += BlockTypes.BLOCK_EXTENT;
				break;
			case UP:
				position.y += BlockTypes.BLOCK_EXTENT;
				break;
			case DOWN:
				position.y -= BlockTypes.BLOCK_EXTENT;
				break;
		}
	}

	public int getVisibleFaces() {
		if (!visible) {
			return 0;
		}

		int count = 0;

		for (int f = 0; f < 6; f++) {
			if (!faces[f].isCovered()) {
				count++;
			}
		}

		return count;
	}

	public BlockTypes getType() {
		return type;
	}

	public Vector3f getPosition() {
		return position;
	}

	public BlockVisible[] getFaces() {
		return faces;
	}

	public AABB getAABB() {
		return aabb;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(final boolean visible) {
		this.visible = visible;
	}

	public class BlockVisible {
		private final BlockFaces face;
		private Vector3f stretch;
		private boolean covered;
		private boolean blockNearby;

		public BlockVisible(final BlockFaces face) {
			this.face = face;
			this.stretch = null;
			this.covered = true;
			this.blockNearby = false;
		}

		public BlockFaces getFace() {
			return face;
		}

		public Vector3f getStretch() {
			return stretch;
		}

		public void setStretch(final float x, final float y, final float z) {
			if (stretch == null) {
				stretch = new Vector3f(BlockTypes.BLOCK_EXTENT, BlockTypes.BLOCK_EXTENT, BlockTypes.BLOCK_EXTENT);
			}

			stretch.set(x, y, z);
		}

		public boolean isStretched() {
			return stretch != null && (stretch.x != 1.0f || stretch.y != 1.0f || stretch.z != 1.0f);
		}

		public boolean isCovered() {
			return covered;
		}

		public void setCovered(final boolean covered) {
			this.covered = covered;
		}

		public boolean isBlockNearby() {
			return blockNearby;
		}

		public void setBlockNearby(final boolean blockNearby) {
			this.blockNearby = blockNearby;
		}
	}
}
