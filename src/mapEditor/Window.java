package mapEditor;

import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

public class Window extends JFrame {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				@SuppressWarnings("unused")
				Window window = new Window();
			}
		});
	}
	
	private PreviewPane previewPane;
	private ScriptPane scriptPane;
	
	public Window() {
		
		this.setTitle("Atrophy Editor");
		this.setSize(1200, 800);
		
		this.scriptPane = new ScriptPane();
		this.previewPane = new PreviewPane(this);
		
		MapData mapData = new MapData(previewPane, scriptPane);
		
		this.previewPane.init(mapData);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, previewPane, scriptPane);
		this.add(splitPane);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.setVisible(true);
	}
	
}
