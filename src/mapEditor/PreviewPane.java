package mapEditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.Timer;

import mapEditor.editorUnits.MapEntity;
import mapEditor.editorUnits.MapEntityType;

public class PreviewPane extends JPanel {

	private static final int BRIGHTEST_LINE_GAP = 10;
	private static final Color BRIGHTEST_LINE_COLOUR = new Color(0, 60, 60);

	private static final int BRIGHT_LINE_GAP = 5;
	private static final Color BRIGHT_LINE_COLOUR = new Color(0, 120, 120);
	
	private static final Color NORMAL_LINE_COLOUR = new Color(0, 30, 30);;
	
	private static final Color CROSSHAIR_COLOUR = Color.yellow;
	private static final int CROSSHAIR_LENGTH = 10;
	
	private int gridSize;
	private int panX, panY;
	private boolean mousePanning;
	private Point oldPoint;
	private Point newPoint;
	private int[] panAtClick;
	private int[] size;
	
	private MapData mapData;
	private MapEntityType selectedType;
	private MapEntity selectedEntity;
	
	public PreviewPane() {
		
		this.gridSize = 40;
		this.panX = 0;
		this.panY = 0;
		this.size = new int[]{0,0,0,0};
		
		this.setLayout(new BorderLayout());
		
		this.selectedType = MapEntityType.BLOCK;
		this.selectedEntity = null;
		
	}
	
	public void init(MapData mapData) {
		this.addPanControl();
		this.addMenuBar(mapData);
		this.addPaintedMenu();
		
		this.mapData = mapData;
	}

	private void addPaintedMenu() {
		
		JPanel painted = new JPanel() {
			@Override
			public void paint(Graphics g) {
				g.setColor(Color.black);
				g.fillRect(0, 0, this.getWidth(), this.getHeight());
				
				drawGrid(g);
				
				g.setColor(Color.yellow);
				// [maxLeft, maxRight, maxUp, maxDown]
				g.drawRect(-size[0] + panX,
						   -size[2] + panY,
						   size[1] + size[0] + this.getWidth(),
						   size[3] + size[2] + this.getHeight());
				
				drawEntities(g);
			}

			private void drawGrid(Graphics g) {
				int brightLineCounter = -BRIGHTEST_LINE_GAP / 2;
				
				int paintBrush = panX;
				
				// draw grid
				
				while(paintBrush < this.getWidth()){
					
					if(brightLineCounter % BRIGHTEST_LINE_GAP == 0){
						g.setColor(BRIGHTEST_LINE_COLOUR);
					}
					else if(brightLineCounter % BRIGHT_LINE_GAP == 0){
						g.setColor(BRIGHT_LINE_COLOUR);
					}
					else{
						g.setColor(NORMAL_LINE_COLOUR);
					}
					
					g.drawLine(paintBrush, 0, paintBrush, this.getHeight());
					paintBrush += gridSize;
					brightLineCounter++;
				}
				
				brightLineCounter = -BRIGHTEST_LINE_GAP / 2;
				
				paintBrush = panY;
				while(paintBrush < this.getHeight()){
					
					if(brightLineCounter % BRIGHTEST_LINE_GAP == 0){
						g.setColor(BRIGHTEST_LINE_COLOUR);
					}
					else if(brightLineCounter % BRIGHT_LINE_GAP == 0){
						g.setColor(BRIGHT_LINE_COLOUR);
					}
					else{
						g.setColor(NORMAL_LINE_COLOUR);
					}
					
					g.drawLine(0, paintBrush, this.getWidth(), paintBrush);
					paintBrush += gridSize;
					brightLineCounter++;
				}
				
				// Draw crosshair
				if(newPoint == null)
					return;
				
				g.setColor(CROSSHAIR_COLOUR);
				Point mouse = gridPoint(newPoint);
				g.drawLine(mouse.x + panX,
								   mouse.y - CROSSHAIR_LENGTH + panY,
								   mouse.x + panX, 
								   mouse.y + CROSSHAIR_LENGTH + panY);
				g.drawLine(mouse.x - CROSSHAIR_LENGTH + panX,
								   mouse.y + panY,
								   mouse.x + CROSSHAIR_LENGTH + panX, 
								   mouse.y + panY);
			}
			
			private void drawEntities(Graphics drawShape) {
				
				Graphics2D g =  (Graphics2D) drawShape;
				
				for (MapEntity entity : mapData.getEntities()) {
					if(entity.getX() + panX > 0 && entity.getX() + panX < this.getWidth() && entity.getY() + panY > 0 && entity.getY() + panY < this.getHeight())
						entity.draw(g, panX, panY);
				}
			}
			
		};
		
		this.add(painted, BorderLayout.CENTER);
		
	}

	private Point gridPoint(Point point){
		int[] moddedMouseLocation = new int[2];
		moddedMouseLocation[0] = (int) ( Math.round( ((point.x) - (panX) ) / (float)(gridSize) ) * (gridSize) );
		moddedMouseLocation[1] = (int) ( Math.floor( ((point.y) - (panY) ) / (float)(gridSize) ) * (gridSize) );
		
		return new Point(moddedMouseLocation[0],moddedMouseLocation[1]);
	}
	
	private void addMenuBar(final MapData mapData) {
		
		JMenuBar menuBar = new JMenuBar();
		
		this.add(menuBar, BorderLayout.NORTH);
		
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		
		JMenuItem generateScript = new JMenuItem("Generate Script");
		
		generateScript.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				mapData.generateScript();
			}
		});
		
		fileMenu.add(generateScript);
		
		JMenu objectsMenu = new JMenu("Map");
		menuBar.add(objectsMenu);
		
		JMenuItem blockBtn = new JMenuItem("Block");
		objectsMenu.add(blockBtn);
		
	}

	private void addPanControl() {
		this.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mousePressed(MouseEvent e) {

				if(e.getButton() == 2) {
					if(!mousePanning) {
						mousePanning = true;
						oldPoint = e.getPoint();
						panAtClick = new int[]{panX, panY};
					}
				}
				else if(!entityAction(gridPoint(newPoint), e.getButton()) && e.getButton() == 3)
					mapData.createEntity(selectedType, gridPoint(newPoint));
			}
			
			private boolean entityAction(Point mousePoint, int mouseButton) {
				
				for(MapEntity entity: mapData.getEntities()) {
					if(entity.getType().equals(selectedType)) {
						if(entity == selectedEntity && entity.interact(mousePoint, mouseButton)) {
							return true;
						}
						if(selectedEntity == null && mouseButton == 1 && entity.contains(mousePoint)) {
							setSelectedEntity(entity);
							return true;
						}
					}
				}
				
				setSelectedEntity(null);
				return false;
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				
				if(e.getButton() == 2) {
					mousePanning = false;
				}
				
			}
			
		});
		
		Timer updTimer = new Timer(10, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				newPoint = PreviewPane.this.getMousePosition();
				PreviewPane.this.repaint();
				
				if(!mousePanning || panAtClick == null || PreviewPane.this.getMousePosition() == null)
					return;
				
				panX = panAtClick[0];
				panY = panAtClick[1];
				
				panX += newPoint.x - oldPoint.x;
				panY += newPoint.y - oldPoint.y;
				
			}
		});
		
		updTimer.start();		
	}

	public void setMapSize(int[] size) {
		this.size = size;
	}
	
	private void setSelectedEntity(MapEntity entity) {
		if(selectedEntity != null)
			selectedEntity.setSelected(false);
		
		this.selectedEntity = entity;
		
		if(selectedEntity != null)
			this.selectedEntity.setSelected(true);
	}

}
