
import java.net.*;
import java.awt.*;
import javax.swing.*;
public class ChatUtils {
	public static final String separator="#";
	public static byte[] bulidMessage(String name,String content){
		String message=name+separator+content;
		return message.getBytes();
	}
	public static String[] parseMessage(byte[] data){
		String message=new String(data,0,data.length);
		int pos=message.indexOf(separator);
		if(pos==-1){
			System.out.println("收到了不符合格式的信息");
			return null;
		}
		String[] combo=new String[2];
		combo[0]=message.substring(0,pos);
		combo[1]=message.substring(pos+1);
		return combo;
	}
	public static void locateFrameCenter(JFrame frame){
		int framewidth=frame.getWidth();
		int frameheight=frame.getHeight();
		Toolkit tool=Toolkit.getDefaultToolkit();
		Dimension screen=tool.getScreenSize();
		int screenwidth=screen.width;
		int screenheight=screen.height;
		frame.setLocation((screenwidth-framewidth)/2,(screenheight-frameheight)/2);
	}
	public static void locateDialogCenter(JDialog dialog){
		int dialogwidth=dialog.getWidth();
		int dialogheight=dialog.getHeight();
		Dimension screen=Toolkit.getDefaultToolkit().getScreenSize();
		int screenwidth=screen.width;
		int screenheight=screen.height;
		dialog.setLocation((screenwidth-dialogwidth)/2,(screenheight-dialogheight)/2);
	}
	public static InetSocketAddress createAddr(String str,int port){
		String[] str2=str.split("\\.");
		byte[] ip=new byte[str2.length];
		for(int i=0;i<str2.length;i++){
			ip[i]=Short.valueOf(str2[i]).byteValue();
		}
		try{
			InetSocketAddress ipfin=new InetSocketAddress(InetAddress.getByAddress(ip),port);
			return ipfin;
		}
		catch(UnknownHostException e){
			e.printStackTrace();
		}
		return null;
	}
}
