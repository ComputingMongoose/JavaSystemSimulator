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
	
	class ClientData {
		Socket sock;
		InputStream in;
		OutputStream out;
		
		ArrayList<String> tosend;
	}
	
	ClientData[] clients;

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
							if(client.in.available()>0) {
								int c=client.in.read();
								synchronized(lock) {transmit.add(""+(char)c);}
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
						
						boolean found=false;
						synchronized(lock) {
							for(int i=0;i<clients.length;i++)
								if(clients[i]==null) {clients[i]=client; found=true; break;}
						}
						if(!found) {
							System.out.println("TelnetTerminal: Too many connections");
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
		String []adr=address.split("[.]");
		byte[] adrb=new byte[adr.length];
		for(int i=0;i<adr.length;i++)adrb[i]=Byte.parseByte(adr[i]);
		
		clients=new ClientData[100];
		for(int i=0;i<clients.length;i++)clients[i]=null;
		
		serverSocket=new ServerSocket(port,20, InetAddress.getByAddress(adrb));
		
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
