package blocks;

import flounder.devices.*;
import flounder.engine.*;
import flounder.maths.matrices.*;
import flounder.maths.vectors.*;

import static org.lwjgl.opengl.GL11.*;

public class BlockRenderer extends IRenderer {
	private BlockShader shader;
	private Matrix4f modelMatrix;

	public BlockRenderer() {
		this.shader = new BlockShader();
		this.modelMatrix = new Matrix4f();
	}

	@Override
	public void renderObjects(final Vector4f clipPlane, final ICamera camera) {
		prepareRendering(clipPlane, camera);

		WorldManager.getChunkList().forEach(chunk -> {
			if (!chunk.isEmpty() && chunk.isVisible()) {
				renderChunk(chunk);
			}
		});

		endRendering();
	}

	private void prepareRendering(final Vector4f clipPlane, final ICamera camera) {
		shader.start();
		shader.projectionMatrix.loadMat4(FlounderEngine.getProjectionMatrix());
		shader.viewMatrix.loadMat4(camera.getViewMatrix());
		shader.clipPlane.loadVec4(clipPlane);

		OpenglUtils.antialias(ManagerDevices.getDisplay().isAntialiasing());
		OpenglUtils.cullBackFaces(true);
		OpenglUtils.enableDepthTesting();
		OpenglUtils.enableAlphaBlending();
	}

	private void renderChunk(final Chunk chunk) {
		OpenglUtils.bindVAO(chunk.getVAO(), 0, 1, 2, 3);
		shader.modelMatrix.loadMat4(chunk.updateModelMatrix(modelMatrix));
		glDrawElements(GL_TRIANGLES, chunk.getVAOLength(), GL_UNSIGNED_INT, 0);
		OpenglUtils.unbindVAO(0, 1, 2, 3);
	}

	private void endRendering() {
		shader.stop();
	}

	@Override
	public void dispose() {
		shader.dispose();
	}
}
