
import java.net.SocketAddress;
public interface MessageHandler {
	public void handleMessage(byte[] data,SocketAddress addr);
}
