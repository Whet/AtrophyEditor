package mapEditor;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import mapEditor.editorUnits.MapEntity;

public class FormFrame extends JFrame {
	
	private JTextField varName;
	private EditorTextPane additionalCode;

	private FormFrame(final MapEntity selectedEntity) {
		
		this.setTitle("Code Form");
		
		this.setLayout(new BorderLayout());
		
		JPanel varPanel = new JPanel();
		
		final JTextField varNameLabel = new JTextField("Var Name: ");
		varNameLabel.setEditable(false);
		
		varName = new JTextField("");
		
		varPanel.setLayout(new BoxLayout(varPanel, BoxLayout.LINE_AXIS));
		
		varPanel.add(new JPanel(){{this.add(varNameLabel);}});
		varPanel.add(varName);
		
		this.add(varPanel, BorderLayout.NORTH);
		
		JPanel textFieldPane = new JPanel();
		
		textFieldPane.setLayout(new BorderLayout());
		
		JTextField textFieldLabel = new JTextField("Additional Code: ");
		textFieldLabel.setEditable(false);
		textFieldPane.add(textFieldLabel , BorderLayout.NORTH);
		
		additionalCode = new EditorTextPane();
		
		textFieldPane.add(additionalCode, BorderLayout.CENTER);
		
		this.add(textFieldPane, BorderLayout.CENTER);
		
		this.setSize(400, 500);
		
		JPanel confirmPanel = new JPanel();
		
		JButton confirmButton = new JButton("Confirm Changes");
		
		confirmPanel.add(confirmButton);
		
		confirmButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedEntity.setAdditionalCode(additionalCode.getText());
				selectedEntity.setVarName(varName.getText());
				FormFrame.this.setVisible(false);
				close();
			}
		});
		
		this.add(confirmPanel, BorderLayout.SOUTH);
		
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		this.varName.setText(selectedEntity.getVarName());
		this.additionalCode.setText(selectedEntity.getAdditionalCode());
		
	}
	
	private void close() {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				FormFrame.this.dispatchEvent(new WindowEvent(FormFrame.this, WindowEvent.WINDOW_CLOSING));
			}
		});
	}
	
	public static class FormSheet {
		
		private JTextField varField;
		private EditorTextPane additionalCode;

		public FormSheet(JTextField varField, EditorTextPane additionalCode) {
			this.varField = varField;
			this.additionalCode = additionalCode;
		}
		
		public String getVarName() {
			return this.varField.getText();
		}
		
		public String getCode() {
			return this.additionalCode.getText();
		}
	}
	
	public static void createCodeForm(Point mousePoint, MapEntity selectedEntity) {
		FormFrame form = new FormFrame(selectedEntity);
		form.setLocation(mousePoint);
		form.setVisible(true);
	}

}
