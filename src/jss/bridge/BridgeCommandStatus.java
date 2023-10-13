package jss.bridge;

public enum BridgeCommandStatus {

	DONE(1),
	ERROR(2),
	WAITING_FOR_BRIDGE(1000),
	NOT_SENT(1001),
	UNKNOWN(9999);
	
	private int bridgeStatus;
	
	BridgeCommandStatus(int bridgeStatus){
		this.bridgeStatus=bridgeStatus;
	}
	
	public int getBridgeStatus() {
		return this.bridgeStatus;
	}
	
	public static BridgeCommandStatus getByBridgeStatus(int status) {
		for(BridgeCommandStatus bcs:BridgeCommandStatus.values())
			if(bcs.bridgeStatus==status)return bcs;
		return UNKNOWN;
	}
	
}
