package jss.bridge;

import java.io.IOException;

/**
 * A bridge will connect to an external device to perform actions.
 *
 */
public interface GenericBridge {

	public void executeCommand(BridgeCommand cmd) throws IOException;
	
}
