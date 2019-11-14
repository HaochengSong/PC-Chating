
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
/* buildGUI ��������������в��� */
	public void buildGUI(){
		frame=new JFrame("���������Ż�SA");
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
		sendbutton=new JButton("ȷ������");
		savebutton=new JButton("���������¼");
		addbutton=new JButton("��Ӻ���");
/* ������Ӻ���ģ�� */
		JPanel userspanel=new JPanel();
		userspanel.setLayout(new BorderLayout(0,3));
		userspanel.add(jscchoose,BorderLayout.CENTER);
		userspanel.add(addbutton,BorderLayout.SOUTH);
/* ����������Ϣģ�� */	
		JPanel sendpanel=new JPanel();
		sendpanel.setLayout(new BorderLayout(7,0));
		sendpanel.add(jscsend,BorderLayout.CENTER);
		sendpanel.add(sendbutton,BorderLayout.EAST);
		sendpanel.add(savebutton,BorderLayout.SOUTH);
/* �����ʾ��Ϣģ�� */	
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
/* ��Ӻ���ģ�� */		
		gbc.gridx=1;
		gbc.gridwidth=1;
		gbc.weightx=0;
		gbc.weighty=0;
		gbc.insets=new Insets(5,0,5,5);
		con.add(userspanel,gbc);
/* ��ӷ�����Ϣģ�� */		
		gbc.gridx=0;
		gbc.gridy=1;
		gbc.gridwidth=2;
		gbc.gridheight=1;
		gbc.weightx=1;
		gbc.insets=new Insets(0,5,5,5);
		con.add(sendpanel,gbc);
	}
/* init ����ʼ��������� */
	public void init(){
		while(true){
/* ���ȹ���һ�������û������˿ںŵĴ��� */
			String[][] data=new String[][]{{"�û���:",null},{"�˿�:",null}};
			InputMessageDialog imdialog=new InputMessageDialog(frame,"�������û����Ͷ˿�",true,data);
			imdialog.setVisible(true);
/* dispose()����ȷ�����ǰ���ָ����ť������һ�³�����벻��ִ�� */
/*�� �� �� �� �� �� �� �� �� �� �� �� �� �� �� �� �� �� �� �� �� �� �� �� �� �� �� �� �� �� �� �� �� �� �� �� �� �� �� �� �� �� ��*/
			String[] userdata=imdialog.getText();
			if(userdata==null)
				continue;
			this.username=userdata[0];
			this.userport=Integer.valueOf(userdata[1]);
			try{
/* ����û�������û����Ƿ���Ϲ��򣬲������򵯳����ڸ�֪�û����������� */
				if(username.indexOf(ChatUtils.separator)!=-1){
					String error="�û����в��ܰ���"+ChatUtils.separator;
					JOptionPane.showMessageDialog(frame,error,"������û���",JOptionPane.ERROR_MESSAGE);
					continue;
				}
/* ����û�������û����Ƿ���Ϲ��򣬲������򵯳����ڸ�֪�û����������� */
				if(userport<0||userport>65536){
					String error2="�˿ںŲ���С��0�����65536";
					JOptionPane.showMessageDialog(frame,error2,"����Ķ˿ں�",JOptionPane.ERROR_MESSAGE);	
					continue;
				}
			}
			catch(NumberFormatException e){
				JOptionPane.showMessageDialog(frame,"����Ķ˿ںŲ�������","����Ķ˿ں�",JOptionPane.ERROR_MESSAGE);
				continue;
			}
			break;
		}
		frame.setVisible(true);
		try{
/* �������û�����Ķ˿ں�����UDPMessaerģ��,ͬʱ�������������̣߳�����������ݺͷ������ݣ��ڵ���UDPMessager�е�sendData����ǰ���������ݵ��߳̽����ڳ�˯״̬ */
			messenger=new UDPMessager(userport);
			messenger.setMessageHandler(this);
			messenger.startMessenger();
		}
		catch(SocketException e){
			JOptionPane.showMessageDialog(frame,"�˿��ѱ�ռ�ã������˳�","��ռ�õĶ˿�",JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
/* �����������Ӻ��Ѱ�ť����¼������� */
		addbutton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
/* ������Ӻ��ѵķ��� */
				addUser();
			}
		});
/* ��������ķ�����Ϣ��ť����¼������� */
		sendbutton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
/* ���÷�����Ϣ�ķ��� */
				sendChatMessage();
			}
		});
/* ��������ı��������¼��ť����¼������� */
		savebutton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
/* ���ñ��������¼�ķ��� */
				saveChatRecord();
			}
		});
/* ��������ĺ����б�����ѡ��ģʽΪ��ѡģʽ */
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
/* ��������ĺ����б�����¼������� */
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
/* ���ڸ������������ķ��� */
	public void updateTitle(){
		Object selection=list.getSelectedValue();
		if(selection==null||!(selection instanceof UserModel)){
			return ;
		}
		String title=this.username+"��"+((UserModel)selection).getName()+"�����촰��";
		this.frame.setTitle(title);
	}
/* ������Ӻ��ѵķ�������ť�� */
	public void addUser(){
		String[][] a=new String[][]{{"����ip��ַ:",null},{"���Ѷ˿ں�:",null}};
		InputMessageDialog add=new InputMessageDialog(this.frame,"��������Ҫ��ӵĺ��ѵ��û�����˿ں�",true,a);
		add.setVisible(true);
		String[] ipandport=add.getText();
		if(ipandport==null){
			return;
		}
		InetSocketAddress addr=ChatUtils.createAddr(ipandport[0],Integer.valueOf(ipandport[1]));
		if(addr==null){
			JOptionPane.showMessageDialog(frame,"�����˴���Ķ˿ںŻ�ip��ַ","����ĺ�����Ϣ",JOptionPane.ERROR_MESSAGE);
			return;
		}
		sendMessage(ECHO_STRING,addr);
	}
/* ���ڷ��͵���Ϣ�ķ�������ť�� */
	public void sendChatMessage(){
		Object selection=list.getSelectedValue();
		if(selection==null||!(selection instanceof UserModel)){
			JOptionPane.showMessageDialog(frame,"����ѡ��һ���б��еĺ����ٷ�����Ϣ","��ѡ�����",JOptionPane.ERROR_MESSAGE);
			return;
		}
		UserModel user=(UserModel)selection;
		String message=textareasend.getText();
		if(message==null){
			return ;
		}
		sendMessage(message,user.getAddr());
/* �������������Ϣ */
		textareasend.setText(null);
/* ���Ҫ���͵���Ϣ���Ըú��ѵ������¼�� */
		user.getMessageHistory().append(username+":\r\n"+message+"\r\n");
/* ͨ��չʾ��ú��ѵ�ȫ�������¼�����ﵽ�����Ч�� */
		textareashow.setText(user.getMessageHistory().toString());
	}
/* ���ڱ���������б���ѡ�����������¼�ķ�������ť�� */
	public void saveChatRecord(){
		if(list.getSelectedValue()==null){
			JOptionPane.showMessageDialog(frame,"����ѡ��һ�����ѽ��������¼�ı���","δѡ�����",JOptionPane.ERROR_MESSAGE);
			return;
		}
		JFileChooser chooser=new JFileChooser();
/* ����Save File��ѡ��Ի��� */
		chooser.showSaveDialog(frame);
/* ����Ϊֻ��ʾ�ļ���ģʽ */
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		File file=chooser.getSelectedFile();
/* ���ѡ�е��ļ��Ƿ���ڣ����������򴴽����ļ� */
		if(!file.exists()){
			try{
				file.createNewFile();
			}
			catch(IOException e){
				JOptionPane.showMessageDialog(frame,"�����ļ�ʧ��"+e.getMessage(),"�ļ�����ʧ��",JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
/* ����ú��ѵ������¼ȫ�����浽ѡ�����ļ��� */
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
/* ���ڷ�����Ϣ�ķ���,����һ��Stringֵ��һ��SocketAddress���͵�ʵ�� */
	public void sendMessage(String message,SocketAddress addr){
		byte[] data=ChatUtils.bulidMessage(this.username,message);
		messenger.sendData(data,addr);
	}
/* ���ڽ�����Ϣ��ģ�� */
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
/* �÷������ڲ��Ҵ��û��Ƿ��ں����б��� */
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
/* ��������ʷ��¼��ʾ���ı���ʾ����,���ù����������һ�� */
	public void updateChatHistory(String history){
		textareashow.setText(history);
		int last=history.indexOf("\n");
		if(last==-1){
			return;
		}
		textareashow.setCaretPosition(last);
	}
/* ��Ӽ��̼���������ʹ��Ctrl��Enter��ϼ�������Ϣ */
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
/* ��ʾ������������� */
	public void showFrame(){
		this.frame.setVisible(true);
	}	
}
/* �������ѵ��� UserModel */
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
