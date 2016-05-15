package blocks;

import flounder.engine.*;
import flounder.lights.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.noise.*;
import flounder.physics.*;

import java.util.*;

public class WorldManager {
	private static final List<Chunk> CHUNK_LIST = new ArrayList<>();
	private static final Light LIGHT_SUN = new Light(new Colour(0.6f, 0.6f, 0.6f), new Vector3f(10000, 10000, -10000), new Attenuation(1.0f, 0.0f, 0.0f));
	private static final PerlinNoise NOISE = new PerlinNoise((int) GameSeed.getSeed());

	public static void init() {
		for (int x = -1; x < 0; x++) {
			for (int z = -1; z < 0; z++) {
				for (int y = -1; y < 2; y++) {
					FlounderLogger.log("Creating Chunk At: " + x + ", " + y + ", " + z);
					CHUNK_LIST.add(new Chunk(new Vector3f(x * (Chunk.CHUNK_SIZE * (2.0f * BlockTypes.BLOCK_EXTENT)), y * (Chunk.CHUNK_SIZE * (2.0f * BlockTypes.BLOCK_EXTENT)), z * (Chunk.CHUNK_SIZE * (2.0f * BlockTypes.BLOCK_EXTENT))), NOISE, Maths.RANDOM));
				}
			}
		}
	}

	public static void update() {
		for (final Chunk chunk : CHUNK_LIST) {
			chunk.update();

			if (chunk.isVisible()) {
				AABBManager.addAABBRender(chunk);
			}
		}

		SortingAlgorithms.quickSort(CHUNK_LIST); // Sorts furthest to nearest.
		Collections.reverse(CHUNK_LIST); // Reverses so closer chunks are first.
	}

	public static boolean blockExists(final float x, final float y, final float z) {
		for (final Chunk chunk : CHUNK_LIST) {
			final int bx = Chunk.inverseBlock(chunk.getPosition().x, x);
			final int by = Chunk.inverseBlock(chunk.getPosition().y, y);
			final int bz = Chunk.inverseBlock(chunk.getPosition().z, z);

			if (Chunk.inBounds(bx, by, bz)) {
				return chunk.blockExists(bx, by, bz);
			}
		}

		return false;
	}

	public static Block getBlock(final float x, final float y, final float z) {
		for (final Chunk chunk : CHUNK_LIST) {
			final int bx = Chunk.inverseBlock(chunk.getPosition().x, x);
			final int by = Chunk.inverseBlock(chunk.getPosition().y, y);
			final int bz = Chunk.inverseBlock(chunk.getPosition().z, z);

			if (Chunk.inBounds(bx, by, bz)) {
				return chunk.getBlock(bx, by, bz);
			}
		}

		return null;
	}

	public static void setBlock(final float x, final float y, final float z, final BlockTypes type) {
		for (final Chunk chunk : CHUNK_LIST) {
			final int bx = Chunk.inverseBlock(chunk.getPosition().x, x);
			final int by = Chunk.inverseBlock(chunk.getPosition().y, y);
			final int bz = Chunk.inverseBlock(chunk.getPosition().z, z);

			if (Chunk.inBounds(bx, by, bz)) {
				final Block block = Chunk.createBlock(chunk, bx, by, bz, type);
				chunk.putBlock(block, bx, by, bz);
			}
		}
	}

	public static boolean insideBlock(final Vector3f point) {
		for (final Chunk chunk : CHUNK_LIST) {
			if (chunk.contains(point)) {
				final int bx = Chunk.inverseBlock(chunk.getPosition().x, point.x);
				final int by = Chunk.inverseBlock(chunk.getPosition().y, point.y);
				final int bz = Chunk.inverseBlock(chunk.getPosition().z, point.z);

				if (Chunk.inBounds(bx, by, bz)) {
					return chunk.getBlock(bx, by, bz) != null;
				}
			}
		}

		return false;
	}

	public static List<Chunk> getChunkList() {
		return CHUNK_LIST;
	}
}
