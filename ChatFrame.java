
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.awt.event.*;
public class ChatFrame implements MessageHandler{
	private JFrame frame; 
	private JTextArea textareashow;
	private JTextArea textareasend;
	private JList list;
	private JButton sendbutton;
	private JButton addbutton;
	private JButton savebutton;
	private JScrollPane jscshow;
	private JScrollPane jscsend;
	private JScrollPane jscchoose;
	private DefaultListModel userslist;
	
	private String username;
	private int userport;
	private Messenger messenger;
	private static final String ECHO_STRING="echo";
	public ChatFrame(){
		this.buildGUI();
		this.init();
	}
/* buildGUI 对聊天主界面进行布局 */
	public void buildGUI(){
		frame=new JFrame("聊天器初号机SA");
		frame.setSize(1000,600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ChatUtils.locateFrameCenter(frame);
		textareashow=new JTextArea();
		textareashow.setRows(1);
		textareashow.setColumns(10);
		textareashow.setEditable(false);
		jscshow=new JScrollPane(textareashow);
		textareasend=new JTextArea();
		jscsend=new JScrollPane(textareasend);
		jscsend.setMinimumSize(new Dimension(100,80));
		jscsend.setPreferredSize(new Dimension(100,80));
		textareasend.addKeyListener(new KeyListener(){
			public void keyPressed(KeyEvent e){
				if(e.getKeyChar()==KeyEvent.VK_ENTER){
					if(e.isControlDown()){
						sendChatMessage();
					}
				}
			}
			public void keyReleased(KeyEvent e){
				
			}
			public void keyTyped(KeyEvent e){
				
			}
		});
		textareasend.addKeyListener(new KeyListener(){
			public void keyPressed(KeyEvent e){
				int now=list.getSelectedIndex();
				if(e.isControlDown()){
					if(e.getKeyCode()==KeyEvent.VK_UP){
						now--;
					}
					else if(e.getKeyCode()==KeyEvent.VK_DOWN){
						now++;
					}
				}
				else{
					return;
				}
				if((now!=list.getSelectedIndex())&&(now>=0)&&(now<list.getModel().getSize())){
					list.setSelectedIndex(now);
				}
			}
			public void keyReleased(KeyEvent e){
				
			}
			public void keyTyped(KeyEvent e){
				
			}
		});
		userslist=new DefaultListModel();
		list=new JList(userslist);
		jscchoose=new JScrollPane(list);
		jscchoose.setMinimumSize(new Dimension(80,80));
		jscchoose.setPreferredSize(new Dimension(80,80));
		sendbutton=new JButton("确定发送");
		savebutton=new JButton("保存聊天记录");
		addbutton=new JButton("添加好友");
/* 构建添加好友模块 */
		JPanel userspanel=new JPanel();
		userspanel.setLayout(new BorderLayout(0,3));
		userspanel.add(jscchoose,BorderLayout.CENTER);
		userspanel.add(addbutton,BorderLayout.SOUTH);
/* 构建发送消息模块 */	
		JPanel sendpanel=new JPanel();
		sendpanel.setLayout(new BorderLayout(7,0));
		sendpanel.add(jscsend,BorderLayout.CENTER);
		sendpanel.add(sendbutton,BorderLayout.EAST);
		sendpanel.add(savebutton,BorderLayout.SOUTH);
/* 添加显示消息模块 */	
		Container con=frame.getContentPane();
		con.setLayout(new GridBagLayout());
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.gridx=0;
		gbc.gridy=0;
		gbc.gridwidth=1;
		gbc.gridheight=1;
		gbc.fill=GridBagConstraints.BOTH;
		gbc.insets=new Insets(5,5,5,5);
		gbc.weightx=1.0;
		gbc.weighty=1.0;
		con.add(jscshow,gbc);
/* 添加好友模块 */		
		gbc.gridx=1;
		gbc.gridwidth=1;
		gbc.weightx=0;
		gbc.weighty=0;
		gbc.insets=new Insets(5,0,5,5);
		con.add(userspanel,gbc);
/* 添加发送消息模块 */		
		gbc.gridx=0;
		gbc.gridy=1;
		gbc.gridwidth=2;
		gbc.gridheight=1;
		gbc.weightx=1;
		gbc.insets=new Insets(0,5,5,5);
		con.add(sendpanel,gbc);
	}
/* init 将初始化聊天程序 */
	public void init(){
		while(true){
/* 首先构建一个输入用户名及端口号的窗口 */
			String[][] data=new String[][]{{"用户名:",null},{"端口:",null}};
			InputMessageDialog imdialog=new InputMessageDialog(frame,"请输入用户名和端口",true,data);
			imdialog.setVisible(true);
/* dispose()方法确保除非按下指定按钮，否则一下程序代码不会执行 */
/*— — — — — — — — — — — — — — — — — — — — — — — — — — — — — — — — — — — — — — — — — — —*/
			String[] userdata=imdialog.getText();
			if(userdata==null)
				continue;
			this.username=userdata[0];
			this.userport=Integer.valueOf(userdata[1]);
			try{
/* 检查用户输入的用户名是否符合规则，不符合则弹出窗口告知用户并重新输入 */
				if(username.indexOf(ChatUtils.separator)!=-1){
					String error="用户名中不能包含"+ChatUtils.separator;
					JOptionPane.showMessageDialog(frame,error,"错误的用户名",JOptionPane.ERROR_MESSAGE);
					continue;
				}
/* 检查用户输入的用户名是否符合规则，不符合则弹出窗口告知用户并重新输入 */
				if(userport<0||userport>65536){
					String error2="端口号不能小于0或大于65536";
					JOptionPane.showMessageDialog(frame,error2,"错误的端口号",JOptionPane.ERROR_MESSAGE);	
					continue;
				}
			}
			catch(NumberFormatException e){
				JOptionPane.showMessageDialog(frame,"输入的端口号不是数字","错误的端口号",JOptionPane.ERROR_MESSAGE);
				continue;
			}
			break;
		}
		frame.setVisible(true);
		try{
/* 将根据用户输入的端口号启动UDPMessaer模块,同时启动的两个新线程，负责接收数据和发送数据，在调用UDPMessager中的sendData方法前，发送数据的线程将处于沉睡状态 */
			messenger=new UDPMessager(userport);
			messenger.setMessageHandler(this);
			messenger.startMessenger();
		}
		catch(SocketException e){
			JOptionPane.showMessageDialog(frame,"端口已被占用，程序将退出","被占用的端口",JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
/* 给主界面的添加好友按钮添加事件监听器 */
		addbutton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
/* 调用添加好友的方法 */
				addUser();
			}
		});
/* 给主界面的发送消息按钮添加事件监听器 */
		sendbutton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
/* 调用发送信息的方法 */
				sendChatMessage();
			}
		});
/* 给主界面的保存聊天记录按钮添加事件监听器 */
		savebutton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
/* 调用保存聊天记录的方法 */
				saveChatRecord();
			}
		});
/* 给主界面的好友列表设置选择模式为单选模式 */
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
/* 给主界面的好友列表添加事件监听器 */
		list.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e){
				Object selection=list.getSelectedValue();
				if(selection==null||(selection instanceof UserModel)){
					return;
				}
				UserModel user=(UserModel)selection;
				textareashow.setText(user.getMessageHistory().toString());
				updateTitle();
			}
		});
		updateTitle();
	}
/* 用于更新主界面标题的方法 */
	public void updateTitle(){
		Object selection=list.getSelectedValue();
		if(selection==null||!(selection instanceof UserModel)){
			return ;
		}
		String title=this.username+"与"+((UserModel)selection).getName()+"的聊天窗口";
		this.frame.setTitle(title);
	}
/* 用于添加好友的方法（按钮） */
	public void addUser(){
		String[][] a=new String[][]{{"好友ip地址:",null},{"好友端口号:",null}};
		InputMessageDialog add=new InputMessageDialog(this.frame,"请输入你要添加的好友的用户名与端口号",true,a);
		add.setVisible(true);
		String[] ipandport=add.getText();
		if(ipandport==null){
			return;
		}
		InetSocketAddress addr=ChatUtils.createAddr(ipandport[0],Integer.valueOf(ipandport[1]));
		if(addr==null){
			JOptionPane.showMessageDialog(frame,"输入了错误的端口号或ip地址","错误的好友信息",JOptionPane.ERROR_MESSAGE);
			return;
		}
		sendMessage(ECHO_STRING,addr);
	}
/* 用于发送的信息的方法（按钮） */
	public void sendChatMessage(){
		Object selection=list.getSelectedValue();
		if(selection==null||!(selection instanceof UserModel)){
			JOptionPane.showMessageDialog(frame,"请先选择一个列表中的好友再发送信息","请选择好友",JOptionPane.ERROR_MESSAGE);
			return;
		}
		UserModel user=(UserModel)selection;
		String message=textareasend.getText();
		if(message==null){
			return ;
		}
		sendMessage(message,user.getAddr());
/* 清空输入栏的消息 */
		textareasend.setText(null);
/* 添加要发送的消息到对该好友的聊天记录里 */
		user.getMessageHistory().append(username+":\r\n"+message+"\r\n");
/* 通过展示与该好友的全部聊天记录，来达到聊天的效果 */
		textareashow.setText(user.getMessageHistory().toString());
	}
/* 用于保存与好友列表总选定好友聊天记录的方法（按钮） */
	public void saveChatRecord(){
		if(list.getSelectedValue()==null){
			JOptionPane.showMessageDialog(frame,"请先选择一个好友进行聊天记录的保存","未选择好友",JOptionPane.ERROR_MESSAGE);
			return;
		}
		JFileChooser chooser=new JFileChooser();
/* 弹出Save File的选择对话框 */
		chooser.showSaveDialog(frame);
/* 设置为只显示文件的模式 */
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		File file=chooser.getSelectedFile();
/* 检查选中的文件是否存在，若不存在则创建此文件 */
		if(!file.exists()){
			try{
				file.createNewFile();
			}
			catch(IOException e){
				JOptionPane.showMessageDialog(frame,"创建文件失败"+e.getMessage(),"文件创建失败",JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
/* 将与该好友的聊天记录全部保存到选定的文件中 */
		try{
			FileOutputStream fileout=new FileOutputStream(file);
			byte[] record=textareashow.getText().getBytes();
			fileout.write(record,0,record.length);
			fileout.close();
		}
		catch(FileNotFoundException e){
			e.printStackTrace();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
/* 用于发送信息的方法,根据一个String值和一个SocketAddress类型的实例 */
	public void sendMessage(String message,SocketAddress addr){
		byte[] data=ChatUtils.bulidMessage(this.username,message);
		messenger.sendData(data,addr);
	}
/* 用于接收消息的模块 */
	public void handleMessage(byte[] data,SocketAddress addr){
		String[] message=ChatUtils.parseMessage(data);
		String friendname=message[0];
		String content=message[1];
		UserModel one=new UserModel(friendname,addr);
		UserModel fin=findUser(one);
		if(fin==null){
			userslist.addElement(one);
			fin=one;
		}
		if(content.equals(ECHO_STRING)){
			this.sendMessage(null,addr);
			return;
		}
		if(content.length()>0){
			fin.getMessageHistory().append(fin.getName()+":\r\n"+content+"\r\n");
		}
		list.setSelectedValue(fin,true);
		updateChatHistory(fin.getMessageHistory().toString());
	}
/* 该方法用于查找此用户是否在好友列表中 */
	public UserModel findUser(UserModel one){
		int number=userslist.getSize();
		for(int i=0;i<number;i++){
			UserModel two=(UserModel)userslist.get(i);
			if(two.equals(one)){
				return two;
			}
		}
		return null;
	}
/* 将聊天历史记录显示在文本显示框中,并让滚条滚到最后一行 */
	public void updateChatHistory(String history){
		textareashow.setText(history);
		int last=history.indexOf("\n");
		if(last==-1){
			return;
		}
		textareashow.setCaretPosition(last);
	}
/* 添加键盘监听器，可使用Ctrl和Enter组合键发送消息 */
	class CtrlEnterSend implements KeyListener{
		public void keyPressed(KeyEvent e){
			if(e.getKeyChar()==KeyEvent.VK_ENTER){
				if(e.isControlDown()){
					sendChatMessage();
				}
			}
		}
		public void keyReleased(KeyEvent e){
			
		}
		public void keyTyped(KeyEvent e){
			
		}
	}
	class CtrlChoose implements KeyListener{
		public void keyPressed(KeyEvent e){
			int now=list.getSelectedIndex();
			if(e.isControlDown()){
				if(e.getKeyCode()==KeyEvent.VK_UP){
					now--;
				}
				else if(e.getKeyCode()==KeyEvent.VK_DOWN){
					now++;
				}
			}
			else{
				return;
			}
			if((now!=list.getSelectedIndex())&&(now>=0)&&(now<list.getModel().getSize())){
				list.setSelectedIndex(now);
			}
		}
		public void keyReleased(KeyEvent e){
			
		}
		public void keyTyped(KeyEvent e){
			
		}
	}
/* 显示聊天程序主界面 */
	public void showFrame(){
		this.frame.setVisible(true);
	}	
}
/* 描述好友的类 UserModel */
class  UserModel{
	private String name;
	private SocketAddress addr;
	private StringBuffer chatmessagehistory;
	public UserModel(String name,SocketAddress addr){
		this.name=name;
		this.addr=addr;
		this.chatmessagehistory=new StringBuffer();
	}
	public StringBuffer getMessageHistory(){
		return this.chatmessagehistory;
	}
	public String getName(){
		return this.name;
	}
	public SocketAddress getAddr(){
		return this.addr;
	}
	public void setName(String name){
		this.name=name;
	}
	public String toString(){
		return this.name;
	}
	public int hashCode(){
		final int prime = 31;
		int result = 1;
		result = prime * result + ((addr == null) ? 0 : addr.hashCode());
		return result;
	}
	public boolean equals(Object obj){
		if(this==obj){
			return true;
		}
		if(obj==null){
			return false;
		}
		if(!(obj instanceof UserModel)){
			return false;
		}
		UserModel u0=(UserModel)obj;
		if(this.addr==null){
			if(u0.addr!=null){
				return false;
			}
		}
		else if(!this.addr.equals(u0.addr)){
			return false;
		}
		return true;
	}
}
