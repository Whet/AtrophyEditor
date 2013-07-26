package mapEditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;

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
	
	public static final int GRID_SIZE = 40;
	
	private int panX, panY;
	private boolean mousePanning;
	private Point oldPoint;
	private Point newPoint;
	private int[] panAtClick;
	private int[] size;
	
	private MapData mapData;
	private MapEntityType selectedType;
	private MapEntityType scopedType;
	private MapEntity parentEntity;
	private MapEntity selectedEntity;
	
	private Set<JMenuItem> noScopeButtons;
	private Set<JMenuItem> roomScopeButtons;
	
	public PreviewPane() {
		
		this.panX = 0;
		this.panY = 0;
		this.size = new int[]{0,0,0,0};
		
		this.setLayout(new BorderLayout());
		
		this.selectedType = MapEntityType.BLOCK;
		this.selectedEntity = null;
		
		PreviewPane.this.setFocusable(true);
		
		noScopeButtons = new HashSet<>();
		roomScopeButtons = new HashSet<>();
	}
	
	public void init(MapData mapData) {
		this.addMouseControl();
		this.addKeyboardControl();
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
					paintBrush += GRID_SIZE;
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
					paintBrush += GRID_SIZE;
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
					if(entity.getX() + panX > 0 && entity.getX() + panX < this.getWidth() && entity.getY() + panY > 0 && entity.getY() + panY < this.getHeight()) {
						entity.draw(g, panX, panY);
						
						if(!entity.getVarName().isEmpty()) {
							g.setColor(Color.white);
							g.drawString(entity.getVarName(), entity.getX() + panX, entity.getY() + panY - 5);
						}
					}
				}
			}
			
		};
		
		this.add(painted, BorderLayout.CENTER);
		
	}

	private Point gridPoint(Point point){
		int[] moddedMouseLocation = new int[2];
		moddedMouseLocation[0] = (int) ( Math.round( ((point.x) - (panX) ) / (float)(GRID_SIZE) ) * (GRID_SIZE) );
		moddedMouseLocation[1] = (int) ( Math.floor( ((point.y) - (panY) ) / (float)(GRID_SIZE) ) * (GRID_SIZE) );
		
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
		blockBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setSelectedType(MapEntityType.BLOCK);
			}

		});
		objectsMenu.add(blockBtn);
		noScopeButtons.add(blockBtn);
		
		JMenuItem portalBtn = new JMenuItem("Portal");
		portalBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setSelectedType(MapEntityType.PORTAL);
			}
			
		});
		objectsMenu.add(portalBtn);
		noScopeButtons.add(portalBtn);
		
		JMenuItem coverBtn = new JMenuItem("Cover");
		coverBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				scopedType = MapEntityType.COVER;
			}
			
		});
		objectsMenu.add(coverBtn);
		roomScopeButtons.add(coverBtn);
		
		JMenuItem stashBtn = new JMenuItem("Stash");
		stashBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				scopedType = MapEntityType.STASH;
			}
			
		});
		objectsMenu.add(stashBtn);
		roomScopeButtons.add(stashBtn);
		
		setScope(null);
		
	}
	
	private void addMouseControl() {
		this.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseEntered(MouseEvent e) {
				super.mouseEntered(e);
				PreviewPane.this.requestFocusInWindow();
			}
			
			@Override
			public void mousePressed(MouseEvent e) {

				if(e.getButton() == 2) {
					if(!mousePanning) {
						mousePanning = true;
						oldPoint = e.getPoint();
						panAtClick = new int[]{panX, panY};
					}
				}
				else if(!entityAction(gridPoint(newPoint), e.getButton()) && e.getButton() == 3) {
					if(parentEntity != null)
						parentEntity.createScopedEntity(scopedType, gridPoint(newPoint));
					else
						mapData.createEntity(selectedType, gridPoint(newPoint));
				}
			}
			
			private boolean entityAction(Point mousePoint, int mouseButton) {
				
				if(parentEntity == null)
					for(MapEntity entity: mapData.getEntities()) {
						if(entity.getType().equals(selectedType)) {
							if(entity == selectedEntity && entity.mouseInteraction(mousePoint, mouseButton)) {
								return true;
							}
							if(selectedEntity == null && mouseButton == 1 && entity.contains(mousePoint)) {
								setSelectedEntity(entity);
								return true;
							}
						}
					}
				else
					for(MapEntity entity: parentEntity.getEntities()) {
						if(entity.getType().equals(selectedType)) {
							if(entity == selectedEntity && entity.mouseInteraction(mousePoint, mouseButton)) {
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
	
	private void addKeyboardControl() {
		this.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				// F5
				if(e.getKeyCode() == 116) {
					mapData.generateScript();
				}
				// E
				else if(e.getKeyCode() == 69 && selectedEntity != null) {
					FormFrame.createCodeForm(newPoint, selectedEntity);
				}
				// Q
				else if(e.getKeyCode() == 81 && (selectedEntity != null || parentEntity != null)) {
					if(parentEntity == null) {
						switch(selectedEntity.getType()) {
							case BLOCK:
								parentEntity = selectedEntity;
								setSelectedEntity(null);
								setSelectedType(MapEntityType.BLOCK_REGION);
								setScope(MapEntityType.BLOCK_REGION);
								scopedType = MapEntityType.COVER;
								
								for(MapEntity entity: parentEntity.getEntities()) {
									entity.setTypeSelected(MapEntityType.BLOCK_REGION);
								}
							break;
							default:
							break;
						
						}
					}
					else {
						
						for(MapEntity entity: parentEntity.getEntities()) {
							entity.setSelected(false);
							entity.setTypeSelected(parentEntity.getType());
						}
						
						setSelectedType(parentEntity.getType());
						setSelectedEntity(parentEntity);
						parentEntity = null;
						setScope(null);
					}
				}
				// DEL
				else if(e.getKeyCode() == 127 && selectedEntity != null) {
					if(parentEntity != null)
						parentEntity.removeScopedEntity(selectedEntity);
					else
						mapData.removeEntity(selectedEntity);
				}
				else if(selectedEntity != null)
					selectedEntity.keyboardInteraction(e.getKeyCode());
			}
		});
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
	
	private void setSelectedType(MapEntityType type) {
		this.selectedType = type;
		this.setSelectedEntity(null);
		
		for(MapEntity entity: this.mapData.getEntities()) {
			entity.setTypeSelected(selectedType);
		}
	}
	
	private void setScope(MapEntityType type) {
		
		for(JMenuItem item:roomScopeButtons) {
			item.setEnabled(false);
		}
		for(JMenuItem item:noScopeButtons) {
			item.setEnabled(false);
		}
		
		if(type == null) {
			for(JMenuItem item:noScopeButtons) {
				item.setEnabled(true);
			}
			return;
		}
		
		switch(type) {
			case BLOCK_REGION:
				for(JMenuItem item:roomScopeButtons) {
					item.setEnabled(true);
				}
			break;
			default:
				for(JMenuItem item:noScopeButtons) {
					item.setEnabled(true);
				}
			break;
		
		}
	}

}
