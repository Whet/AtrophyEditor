package mapEditor;

import java.awt.Color;

import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

public class EditorTextPane extends JTextPane {
	
	private static final Color BACKGROUND_COLOUR = new Color(30, 30, 30);
	private static final Color STANDARD_TEXT_COLOUR = new Color(190, 215, 255);
	private static final Color COMMAND_TEXT_COLOUR = new Color(210, 82, 82);
	private static final Color TRIGGER_TEXT_COLOUR = new Color(85, 75, 161);
	private static final Color PROPERTY_TEXT_COLOUR = new Color(208, 143, 72);
	
	protected static final String COMMAND_WORDS = "(MAPSIZE|MAPSPAWNS|BLOCK|PORTAL|TRIGGER|COMMAND|TALK|TEXTUREBLOCK|INIT)";
	protected static final String PROPERTY_WORDS = "(cover|stash|triggerCond|triggerEffect|x|y|territory|zone|saferoom|stages|parent|stage|aiInit|openingLine|option|topic|lines|req|onTime|expireTime|expireRepeats|isName|isFaction|hasItem|hasWeapon|isAlive|isDead|isInvestigated|isNotInvestigated|isDaemon|destination|maxTeamSize|minTeamSize|isPlayer|aiNode|subscribe|behaviours|priority|containsFaction)";
	protected static final String TRIGGER_COMMAND_WORDS = "(updateTalk|spawnTeam|spawnCharacter|removeUnit|killUnit|teleport|converse|saferoom|removeSaferoom|loadMap|lockDoor|unlockDoor|addTag|removeTag|directorBias|changeAiNode|showMessage|setTriggerRunning|spawnItem|spawnTalkNode|callCommand)";
	
	public EditorTextPane() {
		
		this.setBackground(BACKGROUND_COLOUR);
		
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
        
        this.setDocument(doc);
        
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
	
}
