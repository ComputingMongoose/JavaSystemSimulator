package jss.bridge;

public class BridgeCommand {

	BridgeDevType devType;
	int devId;
	int command;
	byte []requestData;
	
	BridgeCommandStatus status;
	byte[] responseData;
	
	public BridgeCommand(BridgeDevType devType, int devId, int command, byte[] requestData) {
		this.devType=devType;
		this.devId=devId;
		this.command=command;
		if(requestData==null)this.requestData=null;
		else this.requestData=requestData.clone();
		this.status=BridgeCommandStatus.NOT_SENT;
		this.responseData=null;
	}
	
	public byte[] getCommandPacket() {
		int sz=3+2;
		if(requestData!=null)sz+=requestData.length;
		byte[] ret=new byte[sz];
		ret[0]=(byte) devId;
		ret[1]=(byte) devType.getBridgeId();
		ret[2]=(byte) command;
		if(requestData==null) {
			ret[3]=0;
			ret[4]=0;
		}else {
			ret[3]=(byte)(requestData.length&0xFF);
			ret[4]=(byte)((requestData.length>>8)&0xFF);
			for(int i=0;i<requestData.length;i++)
				ret[5+i]=requestData[i];
		}
		return ret;
	}
	
	public void setStatus(int status) {
		this.status=BridgeCommandStatus.getByBridgeStatus(status);
	}
	
	public void setStatus(BridgeCommandStatus status) {
		this.status=status;
	}
	
	public BridgeCommandStatus getStatus() {
		return this.status;
	}
	
	public void setResponseData(byte[] data) {
		if(data==null)this.responseData=null;
		else this.responseData=data.clone();
	}

	public BridgeDevType getDevType() {
		return devType;
	}

	public int getDevId() {
		return devId;
	}

	public int getCommand() {
		return command;
	}

	public byte[] getRequestData() {
		return requestData;
	}

	public byte[] getResponseData() {
		return responseData;
	}
	
}
