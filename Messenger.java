
import java.net.SocketAddress;
public interface Messenger {
	public void setMessageHandler(MessageHandler handler);
	public void sendData(byte[] data,SocketAddress addr);
	public void startMessenger();
}
