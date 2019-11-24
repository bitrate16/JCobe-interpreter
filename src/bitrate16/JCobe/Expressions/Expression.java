package bitrate16.JCobe.Expressions;

import bitrate16.JCobe.Enviroment.Block;
import bitrate16.JCobe.Values.Value;

/**
 * Used for executing statements & getting return values
 * 
 * @author bitrate16
 *
 */
public interface Expression {
	/* Set virtual storage for operating with variables & functions */
	public Value evaluate(Block b);
}
