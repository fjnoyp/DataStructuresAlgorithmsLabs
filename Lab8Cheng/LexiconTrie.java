import structure5.*;
import java.util.Iterator;
import java.util.Scanner; 
/*

Thought Questions 2.11

If we use an ordered vector, the vector would contain complete words instead of word paths.  Based on the examples in the lab handout, I assumed that the suggested corrections must be the same length as the input word.  Thus, in order to suggest correction, we would have a recursive call with an index parameter.  This index parameter would allow us to extract the given character at the given index.  We would use that index to search through every single word in the order vector to see if the character at that same index is the same or different.  If it is different, our recursive call would decrease the value of maxDistance and if that value becomes less than 0, we would stop the given recursive call.  In order to avoid useless suggestion, i.e. suggesting a 4 letter word for a 3 letter input word, we could check the given word's length in the ordered vector every time to avoid these inefficiencies.  Even if we assume that past a certain point, all the words starting with letter a for example exceed the three length input, we'd still have to check every single element in the vector to see when we reach the b starting words, etc.  This approach would therefore be extremely inefficient as every single iteration would have to check all the elements of the vector thus for a word of length k and a ordered vector of length n, the total time would roughly be k* n.  Our current approach using a LexiconTrie is significantly more efficient, as we would only care about the children of a given node.  The total time would then be k * avg number of children / node.  We know that avg children per node is significantly less than n or total number of words in the trie/ordered Vector.  

 */

//Manages the binary tree of Lexicon Nodes, implements methods of a Lexicon 
public class LexiconTrie implements Lexicon {
    private LexiconNode root;
    private int numWords = 0; 
    public LexiconTrie(){
	root = new LexiconNode(' '); 
    }
    //Add words to the tree.  If the word is already contained do nothing, else step through the tree, adding each word in the word parameter to the tree.  Note that addChild will only add another LexiconNode child if and only if that child did not exist vefore.  
    public boolean addWord(String word) {
	//if word already exists, return true as no word is added 
	if(this.containsWord(word)) return false; 
	//step through each letter of the word, adding to binary tree 
	LexiconNode finger = this.root; 
	for(int i = 0; i<word.length(); i++){
	    finger = finger.addChild(word.charAt(i)); 
	}
	finger.setIsWord(true); 
	numWords ++; 
	return true;
    }
    //read in from a file.  Treat every new line as a new word 
    public int addWordsFromFile(String filename) {
	Assert.condition( filename.contains(".txt") , "must read valid file"); 
	int count = 0; 
	Scanner input = new Scanner(new FileStream(filename)); 
	while(input.hasNext()){
	    
	    String in = input.next(); 
	    in = in.toLowerCase(); 
	    this.addWord(in); 
	    count ++; 
	}
	return count; 
	

    }

    //set the isWord boolean to false and remove any unneccessary nodes following and preceding the given node that is removed 
    public boolean removeWord(String word) {
	//if the word is not in the tree do nothing 
	if(!this.containsWord(word)){return false;}  

	//Find the node that contains the last letter of the word 
	LexiconNode theWord = this.find(word); 
	this.find(word).setIsWord(false); 

	//We use the search() helper method from match regex that 
	Vector<LexiconNode> nodes = new Vector<LexiconNode>(); 

	LexiconNode parent = theWord.getParent(); 	
	search(theWord,'1',nodes); 
	if(nodes.size()<=0) theWord.getParent().removeChild( theWord.getLetter() ); 

	while(parent != null && parent.getChildrenPointers().size()<1 && !parent.isWord()) {
	    char remove = parent.getLetter(); 
	    parent = parent.getParent(); 
	    parent.removeChild( remove );
	}
	
	numWords --; 
	return true; 

    }

    public int numWords() {return numWords;}

    //note the additional check isWord() 
    public boolean containsWord(String word){
	return (this.find(word)!=null && this.find(word).isWord()); 
    }
    public boolean containsPrefix(String prefix){
        return (this.find(prefix)!=null); 
    }
    //helper method to find if a string of letters exists in the tree 
    protected LexiconNode find(String letters){
	LexiconNode finger = this.root; 
	for(int i = 0; i<letters.length(); i++){
	    finger = finger.getChild(letters.charAt(i)); 
	    if(finger == null) return null;	 
	}
	return finger; 
    }



    public trieIterator iterator() {return new trieIterator(this.root); } 


    //suggest corrections for a given word. Calls a helper method suggest, which structures the setString to have the proper suggestions 
    public Set<String> suggestCorrections(String target, int maxDistance){
	Set<String> setString = new SetVector<String>(); 
	target = target.toLowerCase(); 
	this.suggest(target,this.root,-1,maxDistance,setString); 	

	return setString; 
    }
    //the recursive helper method for suggestCorrections.  Takes in a Set<String> which it populates with suggestions 
    public void suggest(String target, LexiconNode node, int index, int maxDistance,Set<String> setString){

	Vector<LexiconNode> childrenPointers = node.getChildrenPointers(); 
	//If we reach the end of the target string's length and we have a word, add that word to the setString 
	//Note, adopted the rule that suggestions must be exact same length as the target string (as suggested in the lab handout) 
	if(index+1==target.length()&&node.isWord()){ 
	    setString.add( this.iterator().tracePath(node) ); 
	    return; 
	}      
	//Continue to iterate through all children of a node and whenever a given child node does not match the next character in the target string, decrease maxDistance.  When maxDistance is less than 0, we can discard the given path to the leaves of the tree and not return that word.  
	for(int i = 0; i<childrenPointers.size(); i++){
	    if(childrenPointers.get(i).getLetter() == target.charAt(index+1)){
		suggest(target,childrenPointers.get(i),index+1,maxDistance,setString); }
	    else if(maxDistance-1>=0){
		suggest(target,childrenPointers.get(i),index+1,maxDistance-1,setString); }
	}
    }
    //adds the ability to use * and ? to have the program suggest new words. This method calls the helper method match to populate a setString that will return a vector of suggestions. 
    public Set<String> matchRegex(String pattern){
	Set<String> setString = new SetVector<String>(); 
	pattern = pattern.toLowerCase(); 
	this.match(pattern,root,-1,setString); 

	return setString; 
    }

    //Helper method for the matchRegex method.  Takes in a Set as an input and populates it with suggestions 
    public void match(String pattern, LexiconNode node, int index, Set<String> setString){

	Vector<LexiconNode> childrenPointers = node.getChildrenPointers(); 

	//base case, stop recursing once the entire length of the pattern has been explored 
	//note that if the childrenPointers vector is empty, no additional recursive calls will be made due to for loop dependency 
	if(index+1 == pattern.length()){
	    if(node.isWord()) setString.add(this.iterator().tracePath(node)); 
	    return; 
	}

	// regular, the pattern is a regular char, just make sure that this word path exists in the tree
	if(pattern.charAt(index+1) != '?' && pattern.charAt(index+1) != '*'){
	    if(node.getChild(pattern.charAt(index+1))!=null) 
		this.match(pattern,node.getChild(pattern.charAt(index+1)),index+1,setString); 
	}
	// ?, suggest a blank or another character, does so by calling match on every character child at the given node 
	else if(pattern.charAt(index+1) == '?'){
	    for(int i = 0; i<childrenPointers.size(); i++){
		match(pattern,childrenPointers.get(i),index+1,setString);
	    }
	    //account for zero character case 
	    match(pattern,node,index+1,setString); 

	}
	// *, suggest a blank or one or more characters, does so by calling an additional recursive helper called search 
	if(pattern.charAt(index+1) == '*'){
	    Vector<LexiconNode> nodes = new Vector<LexiconNode>(); 
	    //if the * is at the end, return all the words that have the word path traced so far as their beginning index 
	    if(index+2 >= pattern.length()){ search(node,'1',nodes); } 
	    //else do a regular search 
	    else{ search(node,pattern.charAt(index+2),nodes); }	
	
	    for(int i = 0; i<nodes.size(); i++){
		match(pattern,nodes.get(i),index+1,setString); 
	    }
	    //account for zero character case 
	    match(pattern,node,index+1,setString); 
	}
	
	
    }
    //Recursive helper method that modifies a parameter vector "nodes" and populates it with node suggestions.  The program steps through all the children of a given node.  The program stops adding new nodes to the vector when the target char character is reached 
    public void search(LexiconNode node, char character, Vector<LexiconNode> nodes){
	Vector<LexiconNode> childrenPointers = node.getChildrenPointers(); 	
	if(childrenPointers.size()==0){
	    if(character=='1' && node.isWord()) //check if we are just tracing path to end when * is last char of pattern
		nodes.add(node); 
	}
	for(int i = 0; i<childrenPointers.size(); i++){
	    if(childrenPointers.get(i).getLetter()==character)
		nodes.add(childrenPointers.get(i).getParent()); 		
	    search(childrenPointers.get(i),character,nodes); 
	}
    }
    


}

//An iterator for the trie.  Constructs a list of words and returns them in alphabetical order 
class trieIterator extends AbstractIterator<String>{
    private LexiconNode root; 
    private QueueList<String> wordList;
    public trieIterator(LexiconNode root){

	this.root = root; 
	wordList = new QueueList<String>(); 

	this.search(root,wordList); 
	
    }
    public String next(){
	return wordList.dequeue(); 
    }
    //a recursive helper method that traces all paths of letters. Once leaves are reached and if the isWord() boolean is true, add the given word path to the vector.   
    protected void search(LexiconNode element,QueueList<String> queueList){
	if(element.isWord()) queueList.add( this.tracePath(element) ); 

	Vector<LexiconNode> childrenPointers = element.getChildrenPointers(); 
	if( childrenPointers.size() > 0 ){
	    for(int i = 0; i<childrenPointers.size(); i++){
		this.search( childrenPointers.get(i) , queueList); 
	    }
	}
    }
    //trace a path from the given node back to the root 
    public String tracePath(LexiconNode element){
	String word = ""; 
	while(element.getLetter()!=' '){
	    word = element.getLetter() + word;
	    element = element.getParent(); 
	}
	return word; 
    }
    public String get(){
	return wordList.get(); 
    }
    public boolean hasNext(){
	return wordList.size()>0; 
    }
    //we reset the iterator by clearing the list and then repopulating it with all the words by using the search method 
    public void reset(){
	wordList.clear(); 
	this.search(root,wordList); 
    }
}