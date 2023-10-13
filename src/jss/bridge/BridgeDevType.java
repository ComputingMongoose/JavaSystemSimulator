package jss.bridge;

public enum BridgeDevType {

	FLOPPY_DISK(1),
	BRIDGE(255);
	
	private int bridgeId;
	
	BridgeDevType(int bridgeId) {
		this.bridgeId=bridgeId;
	}

	public int getBridgeId() {
		return bridgeId;
	}
	
}
