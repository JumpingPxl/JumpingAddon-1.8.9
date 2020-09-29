package de.jumpingpxl.jumpingaddon.util.transformer;

import de.jumpingpxl.jumpingaddon.JumpingAddon;
import net.labymod.core.asm.LabyModCoreMod;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 11.03.2019
 */

public class TransformHandler implements IClassTransformer {

	private static JumpingAddon jumpingAddon;
	private final String renderItemClass = (LabyModCoreMod.isObfuscated() ? "bjh"
			: "net.minecraft.client.renderer.entity.RenderItem");
	private final String renderGlintEffect = (LabyModCoreMod.isObfuscated() ? "(Lboq;)V"
			: "(Lnet/minecraft/client/resources/model/IBakedModel;)V");

	public TransformHandler() {

	}

	public static void setJumpingAddon(JumpingAddon jumpingAddon) {
		TransformHandler.jumpingAddon = jumpingAddon;
	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if (jumpingAddon == null) {
			return basicClass;
		}
		//	System.out.println("TRANSFORM " + renderItemClass + ";" + transformedName);
		if (transformedName.equals(renderItemClass)) {
			System.out.println("NOT NULL1");
			final ClassReader reader = new ClassReader(basicClass);
			final ClassWriter writer = new ClassWriter(reader, 2);
			final ClassVisitor visitor = new ClassVisitor(262144, writer) {
				@Override
				public MethodVisitor visitMethod(int access, String name, String desc, String signature,
				                                 String[] exceptions) {
					final MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature,
							exceptions);
					if (desc.equals(renderGlintEffect)) {
						System.out.println("NOT NULL2");
						return new MethodVisitor(262144) {
							@Override
							public void visitLdcInsn(Object cst) {
								if (cst.equals(-8372020) && jumpingAddon.getSettings()
										.getColoredGlint()
										.getAsBoolean()) {
									super.visitFieldInsn(1604, "de.jumpingpxl.jumpingaddon.util.Settings",
											"glintColor", "I");
								} else {
									super.visitLdcInsn(cst);
								}
							}
						};
					}
					return methodVisitor;
				}
			};
			reader.accept(visitor, 0);
			return writer.toByteArray();
		}
		return basicClass;
	}
}
