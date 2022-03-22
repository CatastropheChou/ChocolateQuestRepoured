package team.cqr.cqrepoured.client.model.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class ModelPentagram {

	private static boolean initialized;
	private static int vertexBuffer;
	private static int indexBuffer;

	private static void init() {
		if (!initialized) {
			FloatBuffer vertexByteBuffer = ByteBuffer.allocateDirect(96).order(ByteOrder.nativeOrder()).asFloatBuffer();
			vertexByteBuffer.put(new float[] { 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 1, 1, 1, 0, 0, 1, 0, 1, 1, 1, 0, 1, 1, 1 });
			vertexByteBuffer.rewind();
			ByteBuffer indexByteBuffer = ByteBuffer.allocateDirect(14).order(ByteOrder.nativeOrder());
			indexByteBuffer.put(new byte[] { 0, 4, 1, 5, 7, 4, 6, 0, 2, 1, 3, 7, 2, 6 });
			indexByteBuffer.rewind();

			vertexBuffer = GL15.glGenBuffers();
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBuffer);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexByteBuffer, GL15.GL_STATIC_DRAW);
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

			indexBuffer = GL15.glGenBuffers();
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
			GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indexByteBuffer, GL15.GL_STATIC_DRAW);
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

			initialized = true;
		}
	}

	public static void render(MatrixStack matrix, int corners, float radius, float lineWidth, float lineHeight) {
		if (!initialized) {
			init();
		}

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBuffer);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
		GL20.glEnableVertexAttribArray(0);
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);

		float f1 = 360.0F / corners;
		Vector3d v1 = new Vector3d(0.0D, 0.0D, radius + lineWidth * 0.5F);
		Vector3d v2 = v1.yRot((float) Math.toRadians(f1));
		float f2 = (float) v1.distanceTo(v2);

		float f3 = f1 * (corners / 2);
		Vector3d v3 = new Vector3d(0.0D, 0.0D, radius);
		Vector3d v4 = v3.yRot((float) Math.toRadians(f3));
		float f4 = (float) v3.distanceTo(v4);

		for (int i = 0; i < corners; i++) {
			matrix.pushPose();
			//GlStateManager.pushMatrix();
			matrix.mulPose(Vector3f.YP.rotationDegrees(f1 * i));
			//GlStateManager.rotate(f1 * i, 0.0F, 1.0F, 0.0F);
			matrix.translate(0F, 0F, radius);
			//GlStateManager.translate(0.0F, 0.0F, radius);

			matrix.pushPose();
			//GlStateManager.pushMatrix();
			matrix.translate(0.0F, 0.0F, lineWidth * 0.5F);
			//GlStateManager.translate(0.0F, 0.0F, lineWidth * 0.5F);
			matrix.mulPose(Vector3f.YP.rotationDegrees(90.0F + f1 * 0.5F));
			//GlStateManager.rotate(90.0F + f1 * 0.5F, 0.0F, 1.0F, 0.0F);
			matrix.translate(0.0F, -lineHeight * 0.5F, 0.0F);
			//GlStateManager.translate(0.0F, -lineHeight * 0.5F, 0.0F);
			matrix.scale(lineWidth, lineHeight, f2);
			//GlStateManager.scale(lineWidth, lineHeight, f2);
			GL11.glDrawElements(GL11.GL_TRIANGLE_STRIP, 14, GL11.GL_UNSIGNED_BYTE, 0);
			matrix.popPose();
			//GlStateManager.popMatrix();

			matrix.pushPose();
			//GlStateManager.pushMatrix();
			matrix.mulPose(Vector3f.YP.rotationDegrees(90.0F + f3 * 0.5F));
			//GlStateManager.rotate(90.0F + f3 * 0.5F, 0.0F, 1.0F, 0.0F);
			matrix.translate(-lineWidth * 0.5F, -lineHeight * 0.5F, 0.0F);
			//GlStateManager.translate(-lineWidth * 0.5F, -lineHeight * 0.5F, 0.0F);
			matrix.scale(lineWidth, lineHeight, f4);
			//GlStateManager.scale(lineWidth, lineHeight, f4);
			GL11.glDrawElements(GL11.GL_TRIANGLE_STRIP, 14, GL11.GL_UNSIGNED_BYTE, 0);
			matrix.popPose();
			//GlStateManager.popMatrix();

			matrix.popPose();
			//GlStateManager.popMatrix();
		}

		GL20.glDisableVertexAttribArray(0);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

}
