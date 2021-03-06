
import structure5.*;
import java.util.Random;
import java.util.Scanner;

/**
 * A Random Sentence Generator
 * 
 * This class currently parses a grammar file from standard input.
 * Your job is to extend it to print random sentences from the
 * grammar.
 */
public class RandomSentenceGenerator {

    /** The grammar for the generator */
    protected DefinitionTable grammar;

    /**
     * Create a new grammar from the given file.
     */
    public RandomSentenceGenerator(Scanner in) {
	grammar = new DefinitionTable();
	this.readGrammar(in);
    }


    /**
     * post: reads one production and returns it <p> 
     * 
     * Student: Change this to parse and create a production properly.
     */
    protected Vector<String> readOneProduction(Scanner in) {
	Vector<String> strings = new Vector<String>(); 
	while (in.hasNextLine()) {
	    String token = in.next();
	    if (token.equals(";")) break;
	    else strings.add(token); 
	    Assert.condition(in.hasNext(), 
			     "End of File occured while parsing production");
	}
	return strings;
    }

    /**
     * post: reads the name and productions for one non-terminal <p>
     * Student: Change this to parse the productions, create a
     *          definition and add it to grammar
     */
    protected void readOneNonTerminalDef(Scanner in) {

	String name = in.next();  // the non-terminal name
	
	//Verify that name is correct 
	Assert.condition(in.hasNext(), 
			 "End of File occured while parsing non-terminal " 
			 + name);
	Assert.condition(in.next().equals("{"), 
			 "Expected { for " + name);

	//Create a new definition 
	Definition definition = new Definition(); 
	
	while (true) {
	    Assert.condition(in.hasNext(), 
			     "Are you missing a } at end of " + name + "}");

	    // if we see a } as the next token, break out of the loop,
	    // because we will be done reading productions.
	    if (in.hasNext("}")) break;

	    definition.add(readOneProduction(in));
	}

	grammar.add(name,definition); 

	// read the "}" at the end of the definition.
	Assert.condition(in.next().equals("}"), "Error reading } for " + name);
    }


    /**
     * Read a grammar from a file. <p>
     */
    protected void readGrammar(Scanner in) {
	while (in.hasNext()) {
	    //System.out.println(in.nextLine()); 
	    readOneNonTerminalDef(in);
	}
    }

    

    /**
     * Print out the grammar.  Useful for debugging.
     */
    public String toString() {
	return grammar.toString();
    }


    /*
     * Add new methods here to generate random sentences from the
     * grammar.
     */ 
    

    public static void main(String args[]) {
	RandomSentenceGenerator rsg = 
	    new RandomSentenceGenerator(new Scanner(System.in));

	// print out random sentences based on the grammar rules provided 
	for(int i = 0; i<3; i++){
	    //create spacing between each random output 
	    System.out.println(); 
	    rsg.buildSentence("<start>"); 
	    System.out.println(); 
	    System.out.println(); 
	}
    }

    //A recursive method that uses the grammar.  Starts with <start> to build up a random paragraph.  
    public void buildSentence(String nonTerminal){
	Definition definition = grammar.get(nonTerminal); 
	Assert.condition(definition!=null,"must have a valid definition for each word"); 
	Assert.condition(definition.size()!=0,"must have words for your definition"); 
	Vector<String> word = definition.getRandom(); 
	for(int i = 0; i<word.size(); i++){
	    if( word.get(i).indexOf('<') != -1 ){
		this.buildSentence(word.get(i)); 
	    }
	    else{
		//I tried to make the spacing work out when there were . , or - but I found that not
		//adding a space for words that had . , or - actually made the formatting more off (at least
		// for the college rejection letter) 
		String output = word.get(i); 
		if(output.indexOf('-') != -1 || output.indexOf(',') != -1 || output.indexOf('.') != -1){
		    System.out.print(" " + word.get(i)); 
		}
		else{
		    System.out.print(" " + word.get(i)); 
		}
	    }
	}
    }
}
