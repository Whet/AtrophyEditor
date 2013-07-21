package mapEditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Stack;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

public class ScriptPane extends JPanel {

	private static final Color BACKGROUND_COLOUR = new Color(30, 30, 30);
	
	private static final Color STANDARD_TEXT_COLOUR = new Color(190, 215, 255);
	private static final Color COMMAND_TEXT_COLOUR = new Color(210, 82, 82);
	private static final Color TRIGGER_TEXT_COLOUR = new Color(85, 75, 161);
	private static final Color PROPERTY_TEXT_COLOUR = new Color(208, 143, 72);
	
	protected static final String COMMAND_WORDS = "(MAPSIZE|MAPSPAWNS|BLOCK|PORTAL|TRIGGER|COMMAND|TALK|TEXTUREBLOCK|INIT)";
	protected static final String PROPERTY_WORDS = "(cover|stash|triggerCond|triggerEffect|x|y|territory|zone|saferoom|stages|parent|stage|aiInit|openingLine|option|topic|lines|req|onTime|expireTime|expireRepeats|isName|isFaction|hasItem|hasWeapon|isAlive|isDead|isInvestigated|isNotInvestigated|isDaemon|destination|maxTeamSize|minTeamSize|isPlayer|aiNode|subscribe|behaviours|priority|containsFaction)";
	protected static final String TRIGGER_COMMAND_WORDS = "(updateTalk|spawnTeam|spawnCharacter|removeUnit|killUnit|teleport|converse|saferoom|removeSaferoom|loadMap|lockDoor|unlockDoor|addTag|removeTag|directorBias|changeAiNode|showMessage|setTriggerRunning|spawnItem|spawnTalkNode|callCommand)";
	
	protected static final Map<String, List<String>> ENCAPSULATED_PROPERTIES = new HashMap<>();
	protected static final Set<String> ENCAPSULATING_WORDS = new HashSet<>();
	
	{
		ENCAPSULATING_WORDS.add("MAPSIZE");
		ENCAPSULATING_WORDS.add("MAPSPAWNS");
		ENCAPSULATING_WORDS.add("BLOCK");
		ENCAPSULATING_WORDS.add("cover");
		ENCAPSULATING_WORDS.add("stash");
		ENCAPSULATING_WORDS.add("PORTAL");
		ENCAPSULATING_WORDS.add("TRIGGER");
		ENCAPSULATING_WORDS.add("triggerCond");
		ENCAPSULATING_WORDS.add("triggerEffect");
		ENCAPSULATING_WORDS.add("isAlive");
		ENCAPSULATING_WORDS.add("spawnTeam");
		ENCAPSULATING_WORDS.add("spawnCharacter");
		ENCAPSULATING_WORDS.add("killUnit");
		ENCAPSULATING_WORDS.add("teleport");
		ENCAPSULATING_WORDS.add("destination");
		ENCAPSULATING_WORDS.add("converse");
		ENCAPSULATING_WORDS.add("saferoom");
		ENCAPSULATING_WORDS.add("removeSaferoom");
		ENCAPSULATING_WORDS.add("loadMap");
		ENCAPSULATING_WORDS.add("lockDoor");
		ENCAPSULATING_WORDS.add("unlockDoor");
		ENCAPSULATING_WORDS.add("changeAiNode");
		ENCAPSULATING_WORDS.add("spawnItem");
		ENCAPSULATING_WORDS.add("spawnTalkNode");
		ENCAPSULATING_WORDS.add("COMMAND");
		ENCAPSULATING_WORDS.add("TALK");
		ENCAPSULATING_WORDS.add("topic");
		ENCAPSULATING_WORDS.add("INIT");
		
		List<String> emptyList = new ArrayList<>();
		List<String> factionList = new ArrayList<>();
		factionList.add("\"Bandits\"");
		factionList.add("\"Daemon\"");
		factionList.add("\"Loner\"");
		factionList.add("\"Player\"");
		factionList.add("\"White Vista\"");
		List<String> blockList = new ArrayList<>();
		blockList.add("x:");
		blockList.add("y:");
		blockList.add("territory: \"FACTION\" 10");
		blockList.add("zone: \"FACTION\"");
		blockList.add("cover {\n}");
		blockList.add("stash {\n}");
		List<String> polygonList = new ArrayList<>();
		polygonList.add("x:");
		polygonList.add("y:");
		List<String> triggerList = new ArrayList<>();
		triggerList.add("triggerCond {\n}");
		triggerList.add("triggerEffect {\n}");
		List<String> triggerEffectList = new ArrayList<>();
		triggerEffectList.add("updateTalk: \"TALKNAME\" 0");
		triggerEffectList.add("spawnTeam {\n}");
		triggerEffectList.add("spawnCharacter {\n}");
		triggerEffectList.add("killUnit {\n}");
		triggerEffectList.add("teleport {\n}");
		triggerEffectList.add("converse {\n}");
		triggerEffectList.add("saferoom {\n}");
		triggerEffectList.add("removeSaferoom {\n}");
		triggerEffectList.add("loadMap {\n}");
		triggerEffectList.add("lockDoor {\n}");
		triggerEffectList.add("unlockDoor {\n}");
		triggerEffectList.add("addTag:\"TAGV\"");
		triggerEffectList.add("removeTag:\"TAGV\"");
		triggerEffectList.add("changeAiNode {\n{\n}\n{\n}\n}");
		triggerEffectList.add("showMessage:\"MESSAGE\"");
		triggerEffectList.add("setTriggerRunning:\"TRIGGER\" false");
		triggerEffectList.add("spawnItem \"ITEMNAME\" {\n}");
		triggerEffectList.add("spawnTalkNode {\n}");
		triggerEffectList.add("commandCall:\"COMMAND\"");
		List<String> unitInfoList = new ArrayList<>();
		unitInfoList.add("isName: \"NAME\"");
		unitInfoList.add("minTeamSize: 0");
		unitInfoList.add("maxTeamSize: 5");
		unitInfoList.add("hasItem: \"ITEMNAME\"");
		unitInfoList.add("hasItem: \"WEAPONNAME\"");
		unitInfoList.add("isFaction: \"FACTION\"");
		unitInfoList.add("isAlive");
		unitInfoList.add("isPlayer");
		unitInfoList.add("destination {\n}");
		List<String> roomInfoList = new ArrayList<>();
		roomInfoList.add("containsFaction: \"FACTION\"");
		roomInfoList.add("isName: \"BLOCKNAME\"");
		List<String> doorInfoList = new ArrayList<>();
		doorInfoList.add("isName: \"PORTALNAME\"");
		List<String> loadMapList = new ArrayList<>();
		loadMapList.add("isName: \"MAPNAME\"");
		loadMapList.add("isFaction: \"FACTION\"");
		loadMapList.add("engChance: 5");
		loadMapList.add("medChance: 5");
		loadMapList.add("wepChance: 5");
		loadMapList.add("sciChance: 5");
		List<String> talkNodeList = new ArrayList<>();
		talkNodeList.add("isName: \"TALKNODENAME\"");
		talkNodeList.add("subscribe: \"TALKNAME\"");
		List<String> talkList = new ArrayList<>();
		talkList.add("parent: \"PARENTNAME\"");
		talkList.add("stage: 0");
		talkList.add("aiInit: true");
		talkList.add("openingLine: \"OPENINGLINE\"");
		talkList.add("topic::\"TOPICNAME\" {\n}");
		List<String> topicList = new ArrayList<>();
		topicList.add("lines:\"LINE\"");
		topicList.add("req:\"REQ\"");
		List<String> commandList = new ArrayList<>();
		commandList.add("commandCall: \"COMMANDNAME\"");
		
		ENCAPSULATED_PROPERTIES.put("MAPSIZE", emptyList);
		ENCAPSULATED_PROPERTIES.put("MAPSPAWNS", factionList);
		ENCAPSULATED_PROPERTIES.put("BLOCK", blockList);
		ENCAPSULATED_PROPERTIES.put("cover", polygonList);
		ENCAPSULATED_PROPERTIES.put("stash", polygonList);
		ENCAPSULATED_PROPERTIES.put("PORTAL", polygonList);
		ENCAPSULATED_PROPERTIES.put("TRIGGER", triggerList);
		ENCAPSULATED_PROPERTIES.put("triggerCond", emptyList);
		ENCAPSULATED_PROPERTIES.put("triggerEffect", triggerEffectList);
		ENCAPSULATED_PROPERTIES.put("isAlive", unitInfoList);
		ENCAPSULATED_PROPERTIES.put("spawnTeam", unitInfoList);
		ENCAPSULATED_PROPERTIES.put("spawnCharacter", unitInfoList);
		ENCAPSULATED_PROPERTIES.put("killUnit", unitInfoList);
		ENCAPSULATED_PROPERTIES.put("teleport", unitInfoList);
		ENCAPSULATED_PROPERTIES.put("destination", polygonList);
		ENCAPSULATED_PROPERTIES.put("converse", unitInfoList);
		ENCAPSULATED_PROPERTIES.put("saferoom", roomInfoList);
		ENCAPSULATED_PROPERTIES.put("removeSaferoom", roomInfoList);
		ENCAPSULATED_PROPERTIES.put("loadMap", loadMapList);
		ENCAPSULATED_PROPERTIES.put("lockDoor", doorInfoList);
		ENCAPSULATED_PROPERTIES.put("unlockDoor", doorInfoList);
		ENCAPSULATED_PROPERTIES.put("changeAiNode", emptyList);
		ENCAPSULATED_PROPERTIES.put("spawnItem", unitInfoList);
		ENCAPSULATED_PROPERTIES.put("spawnTalkNode", talkNodeList);
		ENCAPSULATED_PROPERTIES.put("COMMAND", triggerEffectList);
		ENCAPSULATED_PROPERTIES.put("TALK", talkList);
		ENCAPSULATED_PROPERTIES.put("topic", topicList);
		ENCAPSULATED_PROPERTIES.put("INIT", commandList);
	}
	
	private JTextPane txt;
	private SuggestionPanel suggestion;
	
	public ScriptPane() {
		this.setLayout(new BorderLayout());

		final StyleContext cont = StyleContext.getDefaultStyleContext();
        final AttributeSet attrCommand = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, COMMAND_TEXT_COLOUR);
        final AttributeSet attrProperty = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, PROPERTY_TEXT_COLOUR);
        final AttributeSet attrTrigger = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, TRIGGER_TEXT_COLOUR);
        final AttributeSet attrDefault = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, STANDARD_TEXT_COLOUR);
        
        DefaultStyledDocument doc = new DefaultStyledDocument() {
        	
            public void insertString (int offset, String str, AttributeSet a) throws BadLocationException {
                super.insertString(offset, str, a);

                String text = getText(0, getLength());
                int before = findLastNonWordChar(text, offset);
                if (before < 0) before = 0;
                int after = findFirstNonWordChar(text, offset + str.length());
                int wordL = before;
                int wordR = before;

                while (wordR <= after) {
                    if (wordR == after || String.valueOf(text.charAt(wordR)).matches("\\W")) {
                        if (text.substring(wordL, wordR).matches("(\\W)*" + COMMAND_WORDS))
                            setCharacterAttributes(wordL, wordR - wordL, attrCommand, false);
                        else if (text.substring(wordL, wordR).matches("(\\W)*" + PROPERTY_WORDS))
                        	setCharacterAttributes(wordL, wordR - wordL, attrProperty, false);
                        else if (text.substring(wordL, wordR).matches("(\\W)*" + TRIGGER_COMMAND_WORDS))
                        	setCharacterAttributes(wordL, wordR - wordL, attrTrigger, false);
                        else
                            setCharacterAttributes(wordL, wordR - wordL, attrDefault, false);
                        wordL = wordR;
                    }
                    wordR++;
                }
                
            }

			public void remove (int offs, int len) throws BadLocationException {
                super.remove(offs, len);

                String text = getText(0, getLength());
                int before = findLastNonWordChar(text, offs);
                if (before < 0) before = 0;
                int after = findFirstNonWordChar(text, offs);

                if (text.substring(before, after).matches("(\\W)*" + COMMAND_WORDS))
                    setCharacterAttributes(before, after - before, attrCommand, false);
                else if (text.substring(before, after).matches("(\\W)*" + PROPERTY_WORDS))
                	setCharacterAttributes(before, after - before, attrProperty, false);
                else if (text.substring(before, after).matches("(\\W)*" + TRIGGER_COMMAND_WORDS))
                	setCharacterAttributes(before, after - before, attrTrigger, false);
                else
                    setCharacterAttributes(before, after - before, attrDefault, false);
            }
			
			
        };
        
        txt = new JTextPane(doc);
        txt.setText("MAPSIZE{0,0,0,0}\n");
        add(new JScrollPane(txt), BorderLayout.CENTER);
        
        txt.setBackground(BACKGROUND_COLOUR);
        
        txt.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    if (suggestion != null) {
                        if (suggestion.insertSelection()) {
                            e.consume();
                            final int position = txt.getCaretPosition();
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        txt.getDocument().remove(position - 1, 1);
                                    } catch (BadLocationException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            	if (e.getKeyCode() == KeyEvent.VK_DOWN && suggestion != null)
                    suggestion.moveDown();
                else if (e.getKeyCode() == KeyEvent.VK_UP && suggestion != null)
                    suggestion.moveUp();
                else if (e.getKeyCode() == 50 || e.getKeyCode() == 91 || e.getKeyChar() == KeyEvent.VK_BACK_SPACE || Character.isLetterOrDigit(e.getKeyChar())) {

                	if(e.isShiftDown() && e.getKeyCode() == 50)
						try {
							txt.getDocument().insertString(txt.getCaretPosition(), "\"", null);
							txt.setCaretPosition(txt.getText().length() - 1);
						} catch (BadLocationException e1) {
							e1.printStackTrace();
						}
                	else if(e.isShiftDown() && e.getKeyCode() == 91)
						try {
							txt.getDocument().insertString(txt.getCaretPosition(), "\n\t\n}", null);
							txt.setCaretPosition(txt.getText().length() - 2);
						} catch (BadLocationException e1) {
							e1.printStackTrace();
						}
					else
                		showSuggestionLater();
                	
                } 
                else if (Character.isWhitespace(e.getKeyChar()))
                    hideSuggestion();
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }
        });
        
	}
	
	private int findLastNonWordChar (String text, int index) {
        while (--index >= 0) {
            if (String.valueOf(text.charAt(index)).matches("\\W")) {
                break;
            }
        }
        return index;
    }

    private int findFirstNonWordChar (String text, int index) {
        while (index < text.length()) {
            if (String.valueOf(text.charAt(index)).matches("\\W")) {
                break;
            }
            index++;
        }
        return index;
    }

	public void setText(String script) {
		this.txt.setText(script);
	}

	public String getScript() {
		return this.txt.getText();
	}
	
	public class SuggestionPanel {
		
        private JList<String> list;
        private JPopupMenu popupMenu;
        private String subWord;
        private final int insertionPosition;
        private final String context;
        
        public SuggestionPanel(JTextPane txt, int position, String subWord, Point location) {
        	
        	this.context = getDataContext();
        	
        	if(!context.isEmpty())
        		System.out.println("CONTEXT: " + context);
        	
        	this.insertionPosition = position;
            this.subWord = subWord;
            list = createSuggestionList(position, subWord);
            popupMenu = new JPopupMenu();
            popupMenu.removeAll();
            popupMenu.setOpaque(false);
            popupMenu.setBorder(null);
            popupMenu.add(list, BorderLayout.CENTER);
            popupMenu.show(txt, location.x, txt.getBaseline(0, 0) + location.y);
            
            if(list.getSelectedValuesList().size() == 0)
            	this.popupMenu.setVisible(false);
        }

        public void hide() {
            popupMenu.setVisible(false);
            if (suggestion == this) {
                suggestion = null;
            }
        }

        private JList<String> createSuggestionList(final int position, final String subWord) {

        	String[] data = createData(subWord);
        	
            JList<String>  list = new JList<String>(data);
            list.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            list.setSelectedIndex(0);
            list.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        insertSelection();
                    }
                }
            });
            return list;
        }

        private class SuggestionMatch {
        	private int matchValue;
        	private String text;
        	
        	public SuggestionMatch(int matchValue, String text) {
        		this.matchValue = matchValue;
        		this.text = text;
        	}
        }
        
        private String getDataContext() {
        	
        	try {
        	
	        	int caretPos = txt.getCaretPosition();
	        	
	        	while(caretPos >= 0) {
	        		
	        		if(txt.getText().substring(caretPos, caretPos + 1).equals("{")) {
	        			
	        			Stack<Character> charStack = new Stack<>();
	        			boolean readingVar = false;
	        			
	        			while(caretPos >= 0) {
	
	        				if(readingVar && charStack.peek().equals(':') && charStack.get(charStack.size() - 1).equals(':')) {
	        					readingVar = false;
	        					charStack.clear();
	        				}
	        				else if(txt.getText().substring(caretPos, caretPos + 1).equals("\"")) {
	        					readingVar = true;
	        				}
	        				else if(txt.getText().substring(caretPos, caretPos + 1).equals("}")) {
	        					return "";
	        				}
	        				else {
	        					
	        					if(!txt.getText().substring(caretPos, caretPos + 1).equals("{") &&
	        	        		   !txt.getText().substring(caretPos, caretPos + 1).equals(" "))
	        						charStack.push(txt.getText().substring(caretPos, caretPos + 1).toCharArray()[0]);
	        					else {
	        						caretPos--;
	        						continue;
	        					}
	        					
	        					char[] chars = new char[charStack.size()];
	        					for(int i = 0; i < charStack.size(); i++) {
	        						chars[chars.length - 1 - i] = charStack.get(i);
	        					}
	        					
	        					String charString = new String(chars);

	        					if(ENCAPSULATING_WORDS.contains(charString)) {
	        						return charString;
	        					}
	        				}
	
	        				caretPos--;
	        			}
	        			
	        		}
	        		caretPos--;
	        	}
        	
        	}
        	catch(Exception e) {
        		
        	}
        	
        	return "";
        	
        }
        
        private String[] createData(String subWord) {
        	
        	Comparator<SuggestionMatch> comparator = new Comparator<ScriptPane.SuggestionPanel.SuggestionMatch>() {
				
				@Override
				public int compare(SuggestionMatch o1, SuggestionMatch o2) {
					if(o1.matchValue < o2.matchValue)
						return -1;
					else if(o1.matchValue > o2.matchValue)
						return 1;
					return 0;
				}
			};
			
        	PriorityQueue<SuggestionMatch> suggestions = new PriorityQueue<SuggestionMatch>(10, comparator );
        	
        	String commands = COMMAND_WORDS.replaceAll("\\(|\\)", "");
        	String[] words = commands.split("\\|");
        	
        	for(int i = 0; i < words.length; i++) {
        		if(words[i].toLowerCase().startsWith(subWord.toLowerCase()) && words[i].length() - subWord.length() > 0)
        			suggestions.add(new SuggestionMatch(words[i].length() - subWord.length(), words[i]));
        	}
        	
        	commands = PROPERTY_WORDS.replaceAll("\\(|\\)", "");
        	words = commands.split("\\|");
        	for(int i = 0; i < words.length; i++) {
        		if(words[i].toLowerCase().startsWith(subWord.toLowerCase()) && words[i].length() - subWord.length() > 0)
        			suggestions.add(new SuggestionMatch(words[i].length() - subWord.length(), words[i]));
        	}
        	
        	commands = TRIGGER_COMMAND_WORDS.replaceAll("\\(|\\)", "");
        	words = commands.split("\\|");
        	for(int i = 0; i < words.length; i++) {
        		if(words[i].toLowerCase().startsWith(subWord.toLowerCase()) && words[i].length() - subWord.length() > 0)
        			suggestions.add(new SuggestionMatch(words[i].length() - subWord.length(), words[i]));
        	}
        	
        	
        	if(ENCAPSULATED_PROPERTIES.containsKey(context)) {
        		List<String> suggestionsList = ENCAPSULATED_PROPERTIES.get(context);
        		
        		for(String suggestion: suggestionsList) {
        			suggestions.add(new SuggestionMatch(100, suggestion));
        		}
        	}

        	String[] returnValue;
        	
        	if(suggestions.size() < 10)
        		returnValue = new String[suggestions.size()];
        	else 
        		returnValue = new String[10];
        	
        	for(int i = 0; i < returnValue.length; i++) {
        		returnValue[i] = suggestions.poll().text;
        	}
        	
			return returnValue;
		}

		public boolean insertSelection() {
            if (list.getSelectedValue() != null) {
                try {
                    final String selectedSuggestion = ((String) list.getSelectedValue());
                    txt.getDocument().remove(txt.getCaretPosition() - subWord.length(), subWord.length());
                    txt.getDocument().insertString(insertionPosition - subWord.length(), selectedSuggestion, null);
                    hideSuggestion();
                    return true;
                } catch (BadLocationException e1) {
                    e1.printStackTrace();
                }
                hideSuggestion();
            }
            return false;
        }

        public void moveUp() {
            int index = Math.min(list.getSelectedIndex() - 1, 0);
            selectIndex(index);
        }

        public void moveDown() {
            int index = Math.min(list.getSelectedIndex() + 1, list.getModel().getSize() - 1);
            selectIndex(index);
        }

        private void selectIndex(int index) {
            final int position = txt.getCaretPosition();
            list.setSelectedIndex(index);
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    txt.setCaretPosition(position);
                };
            });
        }
    }

    protected void showSuggestionLater() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                showSuggestion();
            }

        });
    }

    protected void showSuggestion() {
        hideSuggestion();
        final int position = txt.getCaretPosition();
        Point location;
        try {
            location = txt.modelToView(position).getLocation();
        } catch (BadLocationException e2) {
            e2.printStackTrace();
            return;
        }
        String text = txt.getText();
        int start = Math.max(0, position - 1);
        while (start > 0) {
            if (!Character.isWhitespace(text.charAt(start))) {
                start--;
            } else {
                start++;
                break;
            }
        }
        if (start > position) {
            return;
        }
        final String subWord = text.substring(start, position);
        if (subWord.length() < 2) {
            return;
        }
        suggestion = new SuggestionPanel(txt, position, subWord, location);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                txt.requestFocusInWindow();
            }
        });
    }

    private void hideSuggestion() {
        if (suggestion != null) {
            suggestion.hide();
        }
    }

}