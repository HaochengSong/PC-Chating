
import java.net.SocketAddress;
import java.util.*;
import java.io.IOException;
import java.net.*;
public class UDPMessager implements Messenger{
	ArrayList<DatagramPacket> messageList=new ArrayList<DatagramPacket>();
	DatagramSocket UDPWorker;
	MessageHandler handler;
	public UDPMessager(int port)throws SocketException{
		UDPWorker=new DatagramSocket(port);
	}
	public void setMessageHandler(MessageHandler handler){
		this.handler=handler;
	}
	public void sendData(byte[] data,SocketAddress addr){
		synchronized(messageList){
			DatagramPacket one=new DatagramPacket(data,data.length);
			one.setSocketAddress(addr);
			messageList.add(one);
			messageList.notify();
		} 
	}
	public void startMessenger(){
		Thread recvThread=new Thread(new MessageReceiver());
		recvThread.start();
		Thread sendThread=new Thread(new MessageSender());
		sendThread.start();
	}
	class MessageReceiver implements Runnable{
		public void run(){
			byte[] data=new byte[1024];
			while(!Thread.currentThread().isInterrupted()){
				DatagramPacket one=new DatagramPacket(data,data.length);
				try{
					UDPWorker.receive(one);
				}
				catch(IOException e){
					e.printStackTrace();
				}
				byte[] recvData=one.getData();
				byte[] realData=new byte[one.getLength()];
				System.arraycopy(recvData,0,realData,0,one.getLength());
				handler.handleMessage(realData,one.getSocketAddress());
			}
		}
	}
	class MessageSender implements Runnable{
		public void run(){
			while(!Thread.currentThread().isInterrupted()){
				DatagramPacket one=getData();
				try{
					UDPWorker.send(one);
				}
				catch(IOException e){
					e.printStackTrace();
				}
			}
		}
	}
	private DatagramPacket getData(){
		synchronized(messageList){
			while(messageList.size()==0){
				try{
					messageList.wait();
				}
				catch(InterruptedException e){
					e.printStackTrace();
				}
			}
			DatagramPacket data=messageList.get(messageList.size()-1);
			messageList.remove(messageList.size()-1);
			return data;
		}
	}
}

