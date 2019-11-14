
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
public class InputMessageDialog extends JDialog{
	private String[][] a;
	private String[] rea;
	public InputMessageDialog(JFrame frame,String title,boolean see,String[][] a){
		super(frame,title,see);
		this.a=a;
		init();
	}
	public void init(){
		int count=a.length;
		Container con=this.getContentPane();
		con.setLayout(new GridBagLayout());
		GridBagConstraints constraints=new GridBagConstraints();
		constraints.gridx=0;
		constraints.gridwidth=1;
		constraints.gridheight=1;
		constraints.weightx=1;
		constraints.weighty=1;
		constraints.insets = new Insets(3, 3, 3, 3);
		for(int i=0;i<count;i++){
			constraints.gridy=i;
			con.add(new JLabel(a[i][0]),constraints);
		}
		constraints.gridx=1;
		constraints.fill=GridBagConstraints.HORIZONTAL;
		JTextField[] textfield=new JTextField[count];
		for(int i=0;i<count;i++){
			constraints.gridy=i;
			textfield[i]=new JTextField(a[i][1]);
			con.add(textfield[i],constraints);
		}
		constraints.gridx=0;
		constraints.gridy=count;
		constraints.gridwidth = 2;
		constraints.gridheight = 1;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.SOUTH;
		con.add(new JSeparator(),constraints);
		constraints.gridy=count+1;
		constraints.weightx=1;
		constraints.weighty=0;
		constraints.fill=GridBagConstraints.NONE;
		constraints.anchor=GridBagConstraints.SOUTHEAST;
		constraints.insets=new Insets(7,7,7,7);
		JButton button=new JButton("È·¶¨");
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				rea=new String[count];
				for(int i=0;i<count;i++){
					rea[i]=textfield[i].getText();
				}
				InputMessageDialog.this.dispose();
			}
		});
		con.add(button, constraints);
		this.setSize(500,count*30+150);
	}
	public String[] getText(){
		return rea;
	}
}
