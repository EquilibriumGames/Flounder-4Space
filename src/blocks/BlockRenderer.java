package blocks;

import flounder.devices.*;
import flounder.engine.*;
import flounder.loaders.*;
import flounder.maths.*;
import flounder.maths.matrices.*;
import flounder.maths.vectors.*;
import org.lwjgl.*;

import java.nio.*;

import static org.lwjgl.opengl.ARBDrawInstanced.*;
import static org.lwjgl.opengl.GL11.*;

public class BlockRenderer extends IRenderer {
	private static final int MAX_INSTANCES = 100000;
	private static final int INSTANCE_DATA_LENGTH = 19;
	private final int VAO;
	private final int VAO_LENGTH;
	private final FloatBuffer BUFFER;
	private final int VBO;
	private BlockShader shader;
	private Matrix4f modelMatrix;
	private int pointer;

	public BlockRenderer() {
		this.shader = new BlockShader();
		this.modelMatrix = new Matrix4f();
		this.pointer = 0;

		// Creates the basic pane.
		//	final int[] indicies = new int[]{1, 3, 2, 0, 1, 2};
		final float[] verticies = new float[]{-1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, -1.0f, 0.0f, -1.0f, 1.0f, 0.0f, -1.0f};
		//	final float[] textureCoords = new float[]{0.9999f, 1.00016594E-4f, 1.0E-4f, 1.00016594E-4f, 0.9999f, 0.9999f, 1.0E-4f, 0.9999f};
		//	final float[] normals = new float[]{0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f};
		//	final float[] tangents = new float[]{-1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f};
		VAO = Loader.createInterleavedVAO(verticies, 3);
		VAO_LENGTH = verticies.length;

		// Creates the instanced array stuff.
		BUFFER = BufferUtils.createFloatBuffer(MAX_INSTANCES * INSTANCE_DATA_LENGTH);
		VBO = Loader.createEmptyVBO(INSTANCE_DATA_LENGTH * MAX_INSTANCES);

		Loader.addInstancedAttribute(VAO, VBO, 1, 4, INSTANCE_DATA_LENGTH, 0);  // Model Mat A
		Loader.addInstancedAttribute(VAO, VBO, 2, 4, INSTANCE_DATA_LENGTH, 4);  // Model Mat B
		Loader.addInstancedAttribute(VAO, VBO, 3, 4, INSTANCE_DATA_LENGTH, 8);  // Model Mat C
		Loader.addInstancedAttribute(VAO, VBO, 4, 4, INSTANCE_DATA_LENGTH, 12); // Model Mat D
		Loader.addInstancedAttribute(VAO, VBO, 5, 3, INSTANCE_DATA_LENGTH, 16); // Colours
	}

	@Override
	public void renderObjects(final Vector4f clipPlane, final ICamera camera) {
		final int instances = WorldManager.renderableChunkFaces();

		if (instances < 1) {
			return;
		}

		pointer = 0;
		final float[] vboData = new float[instances * INSTANCE_DATA_LENGTH];

		for (final Chunk chunk : WorldManager.getChunkList()) {
			if (!Chunk.isEmpty(chunk) && Chunk.isVisible(chunk)) {
				final Block[][][] blocks = Chunk.getBlocks(chunk);

				for (int x = 0; x < blocks.length; x++) {
					for (int z = 0; z < blocks[x].length; z++) {
						for (int y = 0; y < blocks[z].length; y++) {
							loadBlockFaces(blocks[x][z][y], vboData);
						}
					}
				}
			}
		}

		prepareRendering(clipPlane, camera);

		try {
			Loader.updateVBO(VBO, vboData, BUFFER);
		} catch (final BufferOverflowException e) {
			FlounderLogger.exception(e);
		}

		glDrawArraysInstancedARB(GL_TRIANGLE_STRIP, 0, VAO_LENGTH, instances);
		endRendering();
	}

	private void prepareRendering(final Vector4f clipPlane, final ICamera camera) {
		shader.start();
		shader.projectionMatrix.loadMat4(FlounderEngine.getProjectionMatrix());
		shader.viewMatrix.loadMat4(camera.getViewMatrix());
		shader.clipPlane.loadVec4(clipPlane);

		OpenglUtils.antialias(ManagerDevices.getDisplay().isAntialiasing());
		OpenglUtils.cullBackFaces(false);
		OpenglUtils.enableDepthTesting();
		OpenglUtils.enableAlphaBlending();

		OpenglUtils.bindVAO(VAO, 0, 1, 2, 3, 4, 5);
	}

	private void loadBlockFaces(final Block block, final float[] vboData) {
		if (block == null || !block.isVisible()) {
			return;
		}

		for (int f = 0; f < 6; f++) {
			if (!block.getFaces()[f].isCovered()) {
				Block.blockModelMatrix(block, f, modelMatrix);
				final Colour colour = block.getType().getColour();

				vboData[pointer++] = modelMatrix.m00;
				vboData[pointer++] = modelMatrix.m01;
				vboData[pointer++] = modelMatrix.m02;
				vboData[pointer++] = modelMatrix.m03;
				vboData[pointer++] = modelMatrix.m10;
				vboData[pointer++] = modelMatrix.m11;
				vboData[pointer++] = modelMatrix.m12;
				vboData[pointer++] = modelMatrix.m13;
				vboData[pointer++] = modelMatrix.m20;
				vboData[pointer++] = modelMatrix.m21;
				vboData[pointer++] = modelMatrix.m22;
				vboData[pointer++] = modelMatrix.m23;
				vboData[pointer++] = modelMatrix.m30;
				vboData[pointer++] = modelMatrix.m31;
				vboData[pointer++] = modelMatrix.m32;
				vboData[pointer++] = modelMatrix.m33;
				vboData[pointer++] = colour.r;
				vboData[pointer++] = colour.g;
				vboData[pointer++] = colour.b;
			}
		}
	}

	private void endRendering() {
		OpenglUtils.unbindVAO(0, 1, 2, 3, 4, 5);
		shader.stop();
	}

	@Override
	public void dispose() {
		shader.dispose();
	}
}
