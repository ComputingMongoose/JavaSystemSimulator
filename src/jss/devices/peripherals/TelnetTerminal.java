package jss.devices.peripherals;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import jss.configuration.ConfigurationValueTypeException;
import jss.configuration.DeviceConfiguration;
import jss.configuration.DeviceConfigurationException;
import jss.devices.memory.MemoryAccessException;
import jss.simulation.Simulation;

public class TelnetTerminal extends AbstractSerialDevice {
	
	int port;
	ServerSocket serverSocket;
	boolean raw_socket; // set to true to use raw_socket protocol
	
	public static final int IAC = 0xff;
    public static final int IAC_WILL = 0xfb;
    public static final int IAC_DO = 0xfd;
    public static final int IAC_DONT = 0xfe;
    public static final int IAC_ECHO = 0x01;
    public static final int IAC_BINARY = 0x00;
    public static final int IAC_SGA = 0x03;
    public static final int IAC_NAWS = 0x1f;
    public static final int IAC_SB = 0xfa;
    public static final int IAC_SE = 0xf0;	
	
	class ClientData {
		Socket sock;
		InputStream in;
		OutputStream out;
		
		ArrayList<String> tosend;
		
		boolean doInit;
		boolean raw;
		
		int state; // 0 = regular, 1=IAC
		int []iac_bytes;
		int iac_current;
	}
	
	ClientData[] clients;
	
	public static void writeBytes(OutputStream out, int[] bytes) throws IOException {
		for(int b:bytes)out.write(b);
	}

	class ThreadClient extends Thread {
		@Override
		public void run() {
			while(true) {
				boolean work=false;
				for(int i=0;i<clients.length;i++) {
					ClientData client=null;
					synchronized(lock) {
						client=clients[i];
					}
					if(client!=null) {
						try {
							if(client.doInit) {
								writeBytes(client.out, new int[] {
										IAC, IAC_WILL, IAC_ECHO,
										IAC, IAC_DO, IAC_SGA,
										IAC, IAC_WILL, IAC_SGA,
										IAC, IAC_DO, IAC_BINARY,
										IAC, IAC_WILL, IAC_BINARY,
										IAC, IAC_DONT, IAC_NAWS
								});
								client.doInit=false;
							}
							if(client.in.available()>0) {
								int c=client.in.read();
								boolean send=true;
								if(!client.raw) {
									switch(client.state) {
									case 0:
										if(c==IAC) {
											client.state=1;
											client.iac_current=0;
											if(client.iac_bytes==null)client.iac_bytes=new int[10];
											send=false;
										}
										break;
									case 1: // IAC received
										client.iac_bytes[client.iac_current]=c;
										client.iac_current++;
										if(client.iac_current==2) {
											client.state=0;
											sim.writeToCurrentLog(String.format("Received Telnet IAC command %X %X", client.iac_bytes[0],client.iac_bytes[1]));
										}
										send=false;
										break;
									}
								}
								if(send) {
									synchronized(lock) {transmit.add(""+(char)c);}
								}
								work=true;
							}
							
							String send=null;
							synchronized(client) {
								if(client.tosend.size()>0) {
									send=client.tosend.get(0);
									client.tosend.remove(0);
								}
							}
							if(send!=null) {
								client.out.write((byte)send.charAt(0));
								work=true;
							}
						}catch(IOException ex) {
							ex.printStackTrace();
							try {
								client.sock.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							synchronized(lock) {clients[i]=null;}
						}
					}
				}
				if(!work) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else {
					Thread.yield();
				}
			}
		}
	}
	
	class ThreadServer extends Thread {
		@Override
		public void run() {
			while(true) {
				
				Thread.yield();
				
				try {
					Socket sock=serverSocket.accept();
					
					if(sock!=null) {
						ClientData client=new ClientData();
						client.sock=sock;
						client.in=sock.getInputStream();
						client.out=sock.getOutputStream();
						client.tosend=new ArrayList<String>(10);
						client.raw=true;
						
						boolean found=false;
						synchronized(lock) {
							if(!raw_socket) {client.doInit=true;client.raw=false;}
							for(int i=0;i<clients.length;i++)
								if(clients[i]==null) {clients[i]=client; found=true; break;}
						}
						if(!found) {
							sim.writeToCurrentLog("TelnetTerminal: Too many connections");
							sock.close();
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public void configure(DeviceConfiguration config, Simulation sim)
			throws DeviceConfigurationException, ConfigurationValueTypeException, IOException {
		
		super.configure(config, sim);
		
		port=(int) config.getLong("port");
		String address=config.getOptString("address", "127.0.0.1");
		
		sim.writeToCurrentLog(String.format("%s will listen on port %d for telnet connections",config.getName(),port));
		
		String []adr=address.split("[.]");
		byte[] adrb=new byte[adr.length];
		for(int i=0;i<adr.length;i++)adrb[i]=Byte.parseByte(adr[i]);
		
		clients=new ClientData[(int)config.getOptLong("max_clients", 100)];
		for(int i=0;i<clients.length;i++)clients[i]=null;
		
		serverSocket=new ServerSocket(port,20, InetAddress.getByAddress(adrb));
		
		raw_socket=(config.getOptLong("raw_socket", 0)==1);
		
		new ThreadServer().start();
		new ThreadClient().start();
	}
	
	@Override
	public void writeControl(long address, long value) throws MemoryAccessException {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeData(int value) throws MemoryAccessException {
		for(int i=0;i<clients.length;i++) {
			ClientData client=null;
			synchronized(lock) { client=clients[i];}
			if(client!=null) {
				synchronized(client) {
					client.tosend.add(""+(char)value);
				}
			}
		}
	}

	@Override
	public void getTransmitData() throws MemoryAccessException {
		// TODO Auto-generated method stub

	}

}
