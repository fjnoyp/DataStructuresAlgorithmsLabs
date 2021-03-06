import java.util.Iterator;
import structure5.*;
import java.util.Scanner; 

//Contains permute.java and another class, bestSchedule that creates the best possible schedule by minimizing the number of exam slots used

//for number 4, assumed that the time slots that were being ordered are the optimal time slot organization from extension 3

//Contains Extension 3 and 4 

//A class that randomly generates vectors of all possible combinations 
public class Permute<E> extends AbstractIterator<Vector<E>> {

    protected Vector<E> elems = new Vector<E>();
    protected long count = 0;
    protected long max = 1;
	
    public Permute(Iterator<E> iter) {
	super();
	while (iter.hasNext()) {
	    elems.add(iter.next());
	}
	max = fact(elems.size());
    }

    private long fact(long n) {
	if (n == 0) return 1L;
	return n * fact(n-1);
    }

    public void reset() {
	count = 0;
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext() {
	return count < max;
    }

    protected Vector<E> generate(long perm) {
	Vector<E> v = new Vector<E>();
	Vector<E> left = new Vector<E>();
	for (E e : elems) left.add(e);
	long n = elems.size();
	for (long i = 0; i < elems.size(); i++) {
	    v.add(left.remove((int)(perm % n)));
	    perm /= n;
	    n--;
	}
	return v;
    }
	
    /* (non-Javadoc)
     * @see structure.AbstractIterator#get()
     */
    public Vector<E> get() {
	return generate(count);
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#next()
     */
    public Vector<E> next() {
	return generate(count++);
    }

    public static void main(String args[]) {
	Vector<String> v = new Vector<String>();
	v.add("A");
	v.add("B");
	v.add("C");
	v.add("D");
	Iterator<Vector<String>> iter = new Permute<String>(v.iterator());
	System.out.println(v);
	while (iter.hasNext()) {
	    System.out.println(iter.next());
	}

	BestSchedule.bestSchedule("small.txt"); 
	
    }
	
}

//A class that creates the shortest schedule possible by considering all possible combinations 
//Another method is available, that minimizes the amount of concurrent exams students need to take based on the shortest schedule
//possible 
class BestSchedule{

    public static int numStudents = 0; 
    
    public BestSchedule(){
	
    }
    //change the graph to keep track of the students that are taking the given exam 
    public static GraphListUndirected<Association<String,Vector<String>>,Integer> readFile(String file){
	
	GraphListUndirected<Association<String,Vector<String>>,Integer> graph = new GraphListUndirected<Association<String,Vector<String>>,Integer>(); 
	Vector<String> classes = new Vector<String>(4); 
	
	//Objects for proper file reading 
	Scanner input = new Scanner(new FileStream(file)); 
	int i = 0; 

	String name = ""; 

	//Read through file 
	while(input.hasNext() || i==0){

	    //Reached a name 
	    if(i==0) {
		if(input.hasNext()){
		    numStudents ++; 
		    name = input.nextLine(); 
		}
		//create edges between all vertices 
		for(int k = 0; k<classes.size(); k++){
		    for(int j = k+1; j<classes.size(); j++){
			//if graph doesn't contain edge between classes, add it 
			if(!graph.containsEdge(new Association<String,Vector<String>>(classes.get(k),null), 
					       new Association<String,Vector<String>>(classes.get(j),null)) && !classes.get(k).equals(classes.get(j))){
			    graph.addEdge(new Association<String,Vector<String>>(classes.get(k),null),
					  new Association<String,Vector<String>>(classes.get(j),null),1);
			}
			
			else if(!classes.get(k).equals(classes.get(j))){ 
			    Edge<Association<String,Vector<String>>,Integer> edge = graph.getEdge(new Association<String,Vector<String>>(classes.get(k),null),
								      new Association<String,Vector<String>>(classes.get(j),null)); 
			    edge.setLabel(edge.label()+1); 
			}
		    }
		}
		classes.clear(); 
	    }
	    //Reached a class 
	    else{
		String course = input.nextLine(); 
		if(!graph.contains(new Association<String,Vector<String>>(course,null))){
		    Vector<String> names = new Vector<String>(); 
		    names.add(name); 
		    graph.add(new Association<String,Vector<String>>(course,names));
		}
		else{ //add new name to class 
		    graph.get(new Association<String,Vector<String>>(course,null)).getValue().add(name); 
		}
	    
		classes.add(course); 
		
	    }
	    
	    i = (i+1)%5; 
	}
	return graph; 
    }

    //prints out the best schedule of the given file 
    public static void bestSchedule(String file){

	//create schedules based on permuted classes and return the shortest one 
	GraphListUndirected<Association<String,Vector<String>>,Integer> graph = readFile(file); 

	//create a regular vector of string classe to permute 
	Vector<String> classVector = new Vector<String>(); 
	Iterator<Association<String,Vector<String>>> iterator = graph.iterator(); 
	while(iterator.hasNext()){
	    classVector.add(iterator.next().getKey()); 
	}

	Iterator<Vector<String>> allCombos = new Permute<String>(classVector.iterator()); 

	//Create schedules for every single combination and return the shortest 
	Vector<String> shortestSchedule = schedule( allCombos.next() , graph ); 
	while(allCombos.hasNext()){
	    Vector<String> otherSchedule = schedule( allCombos.next() , graph ); 
	    if(otherSchedule.size()<shortestSchedule.size()){
		shortestSchedule = otherSchedule; 
	    }
	}
	//print shortest schedule
	//System.out.println(graph); 
	for(int a = 0; a<classVector.size(); a++){
	    System.out.println(graph.get(new Association<String,Vector<String>>(classVector.get(a),null)).getValue().get(0)  ); 
	}
	System.out.println("shortest schedule: "); 
	for(int i = 0; i<shortestSchedule.size(); i++){
	    System.out.println(shortestSchedule.get(i)); 
	}

	//Print out a schedule that has a minimum number of concurrencies or a student taking an exam two times in a row 
        Iterator<Vector<String>> scheduleIterator = new Permute<String>(shortestSchedule.iterator());
	int minConcur = numStudents * classVector.size(); //max possible concurrencies is that each student has to take an exam twice for every single exam offered 
	Vector<String> minSchedule = new Vector<String>();	
	//Run every single combination of the best schedule we created and determine the number of concurrencies that we find 
	while(scheduleIterator.hasNext()){
	    Vector<String> otherSchedule = scheduleIterator.next(); 
	    int otherConcur = numConcurrencies( otherSchedule , graph ); 
	    if(minConcur>otherConcur){
		minConcur = otherConcur; 
		minSchedule = otherSchedule; 
	    }
	}
	//print out minimum concurrencies 
	System.out.println("schedule with minimum number of exam concurrencies: " + minConcur); 
	//print out the schedule 
	for(int b = 0; b<minSchedule.size(); b++){
	    System.out.println(minSchedule.get(b)); 
	}
    }
    //Determine the number of concurrencies given the current schedule 
    public static int numConcurrencies( Vector<String> schedule, GraphListUndirected<Association<String,Vector<String>>,Integer> graph ){
	int concur = 0; 
	Vector<String> prevStudents = new Vector<String>(); 

	for(int i = 0; i<schedule.size(); i++){

	    //read in classes in the schedule index block in blocks of length 9 
	    //Have to deal with issue whereby the schedules vector returned, the strings in each is just a long string of all classes at the given time slot, need to parse this data to get readable classes to give to the graph method
	    int exams = schedule.get(i).length() / 9; 
	    String exam = ""; 
	    //Create a vector of the students who are taking the exam right now 
	    Vector<String> curStudents = new Vector<String>(); 
	    for(int a = 0; a<exams; a+=9){
		exam = schedule.get(i).substring(a+1,a+9); 
		curStudents = addVectors(graph.get(new Association<String,Vector<String>>(exam,null)).getValue(),curStudents);  
	    }
	    //check if previous students are taking exam again as current students 
	    for(int j = 0; j<curStudents.size(); j++){
		if(prevStudents.contains(curStudents.get(j))) concur ++; 
	    }
	    prevStudents = curStudents; 
	}
	
	return concur; 
    }
    
    public static Vector<String> addVectors(Vector<String> A, Vector<String> B){
	for(int i = 0; i<B.size(); i++){
	    A.add(B.get(i)); 
	}
	return A; 
    }

    //Creates a schedule based on the ordering of Vector<String> combination 
    public static Vector<String> schedule( Vector<String> combination , GraphListUndirected<Association<String,Vector<String>>,Integer> graph ){
	//Vector to return 
	Vector<String> schedule = new Vector<String>(); 

	//A Vector to hold the vertices we will remove 
	Vector<String> toRemove = new Vector<String>(); 

	//An iterator for vector combination 
	Iterator<String> iterator = combination.iterator(); 

	while(iterator.hasNext()){
	    String result = ""; 
	    String vertexName = iterator.next(); 
	    Iterator<String> otherIterator = combination.iterator(); 
	    while(otherIterator.hasNext()){
		String vertex = otherIterator.next(); 
		if( !graph.containsEdge( new Association<String,Vector<String>>(vertexName,null),
					 new Association<String,Vector<String>>(vertex,null)) ){
		    boolean connection = false; 
		    for(int a = 0; a<toRemove.size(); a++){
			if(graph.containsEdge( new Association<String,Vector<String>>(vertex,null),
					       new Association<String,Vector<String>>(toRemove.get(a),null))){
			    connection = true; 
			    break; 
			}
		    }
		    if(!connection){
		    result = result + " " + vertex; 
		    toRemove.add(vertex); 
		    }
		}
	    }
	    //remove from graph 
	    for(int i = 0; i<toRemove.size(); i++){
		combination.remove(toRemove.get(i));
	    }
	    toRemove.clear(); 
	    iterator = combination.iterator(); 
	    schedule.add(result); 
	}
	return schedule; 
    }
}
