
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
public class test{
	public static void main(String[] args){
		JDialog dialog=new JDialog(new JFrame(),"������",true);
		dialog.setSize(400,400);
		ChatUtils.locateDialogCenter(dialog);
		Container con=dialog.getContentPane();
		con.setLayout(new FlowLayout());
		JButton button=new JButton("ȷ��");
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dialog.dispose();
			}
		});
		con.add(button);
		System.out.println("������");
		dialog.setVisible(true);
	}
}
