package de.jumpingpxl.jumpingaddon.util.transformer;

/**
 * @author Nico (JumpingPxl) Middendorf
 * @date 11.03.2019
 */

public interface Transformer {
	boolean matches(String transformedName);

	byte[] transform(String name, String transformedName, byte[] basicClass);
}
