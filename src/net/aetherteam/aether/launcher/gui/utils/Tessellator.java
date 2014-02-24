package net.aetherteam.aether.launcher.gui.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBBufferObject;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

public class Tessellator {

	private static int nativeBufferSize = 0x200000;

	private static int trivertsInBuffer = (nativeBufferSize / 48) * 6;

	public static boolean renderingWorldRenderer = false;

	public boolean defaultTexture = false;

	private int rawBufferSize = 0;

	public int textureID = 0;

	/**
	 * Boolean used to check whether quads should be drawn as two triangles.
	 * Initialized to false and never changed.
	 */
	private static boolean convertQuadsToTriangles;

	/**
	 * Boolean used to check if we should use vertex buffers. Initialized to
	 * false and never changed.
	 */
	private static boolean tryVBO;

	/** The byte buffer used for GL allocation. */
	private static ByteBuffer byteBuffer = BufferUtils.createByteBuffer(nativeBufferSize * 4);

	/** The same memory as byteBuffer, but referenced as an integer buffer. */
	private static IntBuffer intBuffer = byteBuffer.asIntBuffer();

	/** The same memory as byteBuffer, but referenced as an float buffer. */
	private static FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();

	/** Raw integer array. */
	private int[] rawBuffer;

	/**
	 * The number of vertices to be drawn in the next draw call. Reset to 0
	 * between draw calls.
	 */
	private int vertexCount;

	/** The first coordinate to be used for the texture. */
	private double textureU;

	/** The second coordinate to be used for the texture. */
	private double textureV;

	/** The color (RGBA) value to be used for the following draw call. */
	private int color;

	/**
	 * Whether the current draw object for this tessellator has color values.
	 */
	private boolean hasColor;

	/**
	 * Whether the current draw object for this tessellator has texture
	 * coordinates.
	 */
	private boolean hasTexture;

	/** The index into the raw buffer to be used for the next data. */
	private int rawBufferIndex;

	/**
	 * The number of vertices manually added to the given draw call. This
	 * differs from vertexCount because it adds extra vertices when converting
	 * quads to triangles.
	 */
	private int addedVertices;

	/** Disables all color information for the following draw call. */
	private boolean isColorDisabled;

	/** The draw mode currently being used by the tessellator. */
	public int drawMode;

	/**
	 * An offset to be applied along the x-axis for all vertices in this draw
	 * call.
	 */
	public double xOffset;

	/**
	 * An offset to be applied along the y-axis for all vertices in this draw
	 * call.
	 */
	public double yOffset;

	/**
	 * An offset to be applied along the z-axis for all vertices in this draw
	 * call.
	 */
	public double zOffset;

	/** The static instance of the Tessellator. */
	public static Tessellator instance = new Tessellator(2097152);

	/** Whether this tessellator is currently in draw mode. */
	public boolean isDrawing;

	/** Whether we are currently using VBO or not. */
	private static boolean useVBO = false;

	/** An IntBuffer used to store the indices of vertex buffer objects. */
	private static IntBuffer vertexBuffers;

	/**
	 * The index of the last VBO used. This is used in round-robin fashion,
	 * sequentially, through the vboCount vertex buffers.
	 */
	private int vboIndex;

	/** Number of vertex buffer objects allocated for use. */
	private static int vboCount = 10;

	private Tessellator(int par1) {
	}

	public Tessellator() {
	}

	static {
		instance.defaultTexture = true;
		useVBO = tryVBO && GLContext.getCapabilities().GL_ARB_vertex_buffer_object;

		if (useVBO) {
			vertexBuffers = BufferUtils.createIntBuffer(vboCount);
			ARBBufferObject.glGenBuffersARB(vertexBuffers);
		}
	}

	/**
	 * Draws the data set up in this tessellator and resets the state to prepare
	 * for new drawing.
	 */
	public int draw() {
		if (!this.isDrawing) {
			throw new IllegalStateException("Not tesselating!");
		} else {
			this.isDrawing = false;

			int offs = 0;
			while (offs < this.vertexCount) {
				int vtc = 0;
				if ((this.drawMode == 7) && convertQuadsToTriangles) {
					vtc = Math.min(this.vertexCount - offs, trivertsInBuffer);
				} else {
					vtc = Math.min(this.vertexCount - offs, nativeBufferSize >> 5);
				}
				Tessellator.intBuffer.clear();
				Tessellator.intBuffer.put(this.rawBuffer, offs * 8, vtc * 8);
				Tessellator.byteBuffer.position(0);
				Tessellator.byteBuffer.limit(vtc * 32);
				offs += vtc;

				if (Tessellator.useVBO) {
					this.vboIndex = (this.vboIndex + 1) % Tessellator.vboCount;
					ARBBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, Tessellator.vertexBuffers.get(this.vboIndex));
					ARBBufferObject.glBufferDataARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, Tessellator.byteBuffer, ARBBufferObject.GL_STREAM_DRAW_ARB);
				}

				if (this.hasTexture) {
					if (Tessellator.useVBO) {
						GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 32, 12L);
					} else {
						Tessellator.floatBuffer.position(3);
						GL11.glTexCoordPointer(2, 32, Tessellator.floatBuffer);
					}

					GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
				}

				if (this.hasColor) {
					if (Tessellator.useVBO) {
						GL11.glColorPointer(4, GL11.GL_UNSIGNED_BYTE, 32, 20L);
					} else {
						Tessellator.byteBuffer.position(20);
						GL11.glColorPointer(4, true, 32, Tessellator.byteBuffer);
					}

					GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
				}

				if (Tessellator.useVBO) {
					GL11.glVertexPointer(3, GL11.GL_FLOAT, 32, 0L);
				} else {
					Tessellator.floatBuffer.position(0);
					GL11.glVertexPointer(3, 32, Tessellator.floatBuffer);
				}

				GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);

				if ((this.drawMode == 7) && convertQuadsToTriangles) {
					GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vtc);
				} else {
					GL11.glDrawArrays(this.drawMode, 0, vtc);
				}

				GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);

				if (this.hasTexture) {
					GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
				}

				if (this.hasColor) {
					GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
				}
			}

			if ((this.rawBufferSize > 0x20000) && (this.rawBufferIndex < (this.rawBufferSize << 3))) {
				this.rawBufferSize = 0;
				this.rawBuffer = null;
			}

			int i = this.rawBufferIndex * 4;
			this.reset();
			return i;
		}
	}

	/**
	 * Clears the tessellator state in preparation for new drawing.
	 */
	private void reset() {
		this.vertexCount = 0;
		Tessellator.byteBuffer.clear();
		this.rawBufferIndex = 0;
		this.addedVertices = 0;
	}

	/**
	 * Sets draw mode in the tessellator to draw quads.
	 */
	public void startDrawingQuads() {
		this.startDrawing(7);
	}

	/**
	 * Resets tessellator state and prepares for drawing (with the specified
	 * draw mode).
	 */
	public void startDrawing(int par1) {
		if (this.isDrawing) {
			throw new IllegalStateException("Already tesselating!");
		} else {
			this.isDrawing = true;
			this.reset();
			this.drawMode = par1;
			this.hasColor = false;
			this.hasTexture = false;
			this.isColorDisabled = false;
		}
	}

	/**
	 * Sets the texture coordinates.
	 */
	public void setTextureUV(double par1, double par3) {
		this.hasTexture = true;
		this.textureU = par1;
		this.textureV = par3;
	}

	/**
	 * Sets the RGBA values for the color, converting from floats between 0 and
	 * 1 to integers from 0-255.
	 */
	public void setColorRGBA_F(float par1, float par2, float par3, float par4) {
		this.setColorRGBA((int) (par1 * 255.0F), (int) (par2 * 255.0F), (int) (par3 * 255.0F), (int) (par4 * 255.0F));
	}

	/**
	 * Sets the RGB values as specified, and sets alpha to opaque.
	 */
	public void setColorOpaque(int par1, int par2, int par3) {
		this.setColorRGBA(par1, par2, par3, 255);
	}

	/**
	 * Sets the RGBA values for the color. Also clamps them to 0-255.
	 */
	public void setColorRGBA(int par1, int par2, int par3, int par4) {
		if (!this.isColorDisabled) {
			if (par1 > 255) {
				par1 = 255;
			}

			if (par2 > 255) {
				par2 = 255;
			}

			if (par3 > 255) {
				par3 = 255;
			}

			if (par4 > 255) {
				par4 = 255;
			}

			if (par1 < 0) {
				par1 = 0;
			}

			if (par2 < 0) {
				par2 = 0;
			}

			if (par3 < 0) {
				par3 = 0;
			}

			if (par4 < 0) {
				par4 = 0;
			}

			this.hasColor = true;

			if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
				this.color = (par4 << 24) | (par3 << 16) | (par2 << 8) | par1;
			} else {
				this.color = (par1 << 24) | (par2 << 16) | (par3 << 8) | par4;
			}
		}
	}

	/**
	 * Adds a vertex specifying both x,y,z and the texture u,v for it.
	 */
	public void addVertexWithUV(double par1, double par3, double par5, double par7, double par9) {
		this.setTextureUV(par7, par9);
		this.addVertex(par1, par3, par5);
	}

	/**
	 * Adds a vertex with the specified x,y,z to the current draw call. It will
	 * trigger a draw() if the buffer gets full.
	 */
	public void addVertex(double par1, double par3, double par5) {
		if (this.rawBufferIndex >= (this.rawBufferSize - 32)) {
			if (this.rawBufferSize == 0) {
				this.rawBufferSize = 0x10000;
				this.rawBuffer = new int[this.rawBufferSize];
			} else {
				this.rawBufferSize *= 2;
				this.rawBuffer = Arrays.copyOf(this.rawBuffer, this.rawBufferSize);
			}
		}
		++this.addedVertices;

		if ((this.drawMode == 7) && convertQuadsToTriangles && ((this.addedVertices % 4) == 0)) {
			for (int i = 0; i < 2; ++i) {
				int j = 8 * (3 - i);

				if (this.hasTexture) {
					this.rawBuffer[this.rawBufferIndex + 3] = this.rawBuffer[(this.rawBufferIndex - j) + 3];
					this.rawBuffer[this.rawBufferIndex + 4] = this.rawBuffer[(this.rawBufferIndex - j) + 4];
				}

				if (this.hasColor) {
					this.rawBuffer[this.rawBufferIndex + 5] = this.rawBuffer[(this.rawBufferIndex - j) + 5];
				}

				this.rawBuffer[this.rawBufferIndex + 0] = this.rawBuffer[(this.rawBufferIndex - j) + 0];
				this.rawBuffer[this.rawBufferIndex + 1] = this.rawBuffer[(this.rawBufferIndex - j) + 1];
				this.rawBuffer[this.rawBufferIndex + 2] = this.rawBuffer[(this.rawBufferIndex - j) + 2];
				++this.vertexCount;
				this.rawBufferIndex += 8;
			}
		}

		if (this.hasTexture) {
			this.rawBuffer[this.rawBufferIndex + 3] = Float.floatToRawIntBits((float) this.textureU);
			this.rawBuffer[this.rawBufferIndex + 4] = Float.floatToRawIntBits((float) this.textureV);
		}

		if (this.hasColor) {
			this.rawBuffer[this.rawBufferIndex + 5] = this.color;
		}

		this.rawBuffer[this.rawBufferIndex + 0] = Float.floatToRawIntBits((float) (par1 + this.xOffset));
		this.rawBuffer[this.rawBufferIndex + 1] = Float.floatToRawIntBits((float) (par3 + this.yOffset));
		this.rawBuffer[this.rawBufferIndex + 2] = Float.floatToRawIntBits((float) (par5 + this.zOffset));
		this.rawBufferIndex += 8;
		++this.vertexCount;
	}

	/**
	 * Sets the color to the given color (packed as bytes in integer) and alpha
	 * values.
	 */
	public void setColorRGBA_I(int par1, int par2) {
		int k = (par1 >> 16) & 255;
		int l = (par1 >> 8) & 255;
		int i1 = par1 & 255;
		this.setColorRGBA(k, l, i1, par2);
	}

	/**
	 * Sets the translation for all vertices in the current draw call.
	 */
	public void setTranslation(double par1, double par3, double par5) {
		this.xOffset = par1;
		this.yOffset = par3;
		this.zOffset = par5;
	}

	/**
	 * Offsets the translation for all vertices in the current draw call.
	 */
	public void addTranslation(float par1, float par2, float par3) {
		this.xOffset += par1;
		this.yOffset += par2;
		this.zOffset += par3;
	}
}
