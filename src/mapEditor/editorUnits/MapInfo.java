package mapEditor.editorUnits;

public class MapInfo {
	
	private int[] size;
	
	public MapInfo() {
		size = new int[4];
	}

	public String getScript() {
		return "MAPSIZE{" + this.size[0] + "," + this.size[1] + "," + this.size[2] + "," + this.size[3] + "}";
	}

	public void readScript(String script) {
		
		String[] lines = script.split("\n");
		
		for(int i = 0; i < lines.length; i++) {
			try {
				if(lines[i].startsWith("MAPSIZE")) {
					
					String editedLine = lines[i].replaceAll(" ", "");
					editedLine = editedLine.replaceAll("MAPSIZE", "");
					editedLine = editedLine.replaceAll("\\{", "");
					editedLine = editedLine.replaceAll("\\}", "");
					String[] numbers = editedLine.split(",");
					
					if(numbers.length == 4)
						for(int j = 0; j < numbers.length; j++) {
							this.size[j] = Integer.parseInt(numbers[j]);
						}
				}
			}
			catch(Exception e) {
				
			}
		}
		
	}

	public int[] getSize() {
		return this.size;
	}

}
