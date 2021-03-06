
import structure5.*; 
import java.util.Scanner; 
import java.util.Iterator; 

//included Extras 1 and 2 
//PLEASE NOTE PERMUTE.JAVA HAS EXTRA 3, also, the code used is the original permute.java, I tried accessing the working version, but it didn't work 

//A class that creates a graph out of students' schedules and formulates an exam schedule that avoids conflicts 
public class Scheduler{
    //the graph to store classes and their edges 
    private GraphListUndirected<String,Integer> graph = new GraphListUndirected<String,Integer>(); 
    private Vector<Vector<String>> slotList;

    //Constructor: read in classes and names and create a graph 
    public Scheduler(String file){
	//Student scheduler
	OrderedVector<ComparableAssociation<String,Vector<String>>> sSchedule = new OrderedVector<ComparableAssociation<String,Vector<String>>>();

	String name = "AAA Schedules for Each Student"; 
	Vector<String> classes = new Vector<String>(4); 
	

	Scanner input = new Scanner(new FileStream(file)); 
	int i = 0; 
	//Vector<String> classes = new Vector<String>(4); 
	while(input.hasNext() || i==0){
	    //Reached a name 
	    if(i==0) {
		//update name , create new student assocation 
		Vector<String> classesII = (Vector<String>)classes.clone(); 
		sSchedule.add(new ComparableAssociation<String,Vector<String>>(name,classesII)); 
		if(input.hasNext()){
		    name = input.nextLine();
		}

		//create edges between all vertices 
		for(int k = 0; k<classes.size(); k++){
		    for(int j = k+1; j<classes.size(); j++){
			if(!graph.containsEdge(classes.get(k),classes.get(j)) && 
			   !classes.get(k).equals(classes.get(j))){
			    graph.addEdge(classes.get(k),classes.get(j),1);
			}
			
			else if(!classes.get(k).equals(classes.get(j))){ 
			    Edge<String,Integer> edge = graph.getEdge(classes.get(k),classes.get(j)); 
			    edge.setLabel(edge.label()+1); 
			}
		    }
		}
		classes.clear(); 
		//remove? => if(input.hasNext()) input.nextLine();
	    }
	    //Reached a class 
	    else{
		String course = input.nextLine(); 
		if(!graph.contains(course))graph.add(course); 
		classes.add(course); 
		
	    }
	    
	    i = (i+1)%5; 
	}
	this.schedule(); 

	//Print out the students and their classes (Extension 2) 
	Iterator<ComparableAssociation<String,Vector<String>>> resultIterator = sSchedule.iterator(); 
	while(resultIterator.hasNext()){
	    ComparableAssociation<String,Vector<String>> student = resultIterator.next(); 
	    Vector<String> examSchedule = student.getValue(); 
	    System.out.println( student.getKey() + "'s Exam Schedule"); 
	    for(int b = 0; b<examSchedule.size(); b++){
		for(int c = 1; c<slotList.size(); c++){
		    if(slotList.get(c).contains(examSchedule.get(b))){
			System.out.println( examSchedule.get(b) + " " + c); 
			break; 
		    }
		}

	    }
	    //System.out.println(resultIterator.next()); 
	}
    }
    //pre: graph of classes
    //post: slotList is updated with index to class 
    public void schedule(){
	//Extensio 1, Class orderer 
	OrderedVector<String> alphabeticalList =new OrderedVector<String>(); 
	//Extension 2 Data Structure, index in vector (shift + 1) is slot
	slotList = new Vector<Vector<String>>(); 
	slotList.setSize(graph.size()); //so you can't just slotList.set(1,null) until you set its size...

	//Slot #
	int slot = 1; 
	//A Vector to hold the vertices we will remove 
	Vector<String> toRemove = new Vector<String>(); 
	//Create the initial iterator of the graph 
	Iterator<String> vertexIterator = graph.iterator(); 
	String vertexName = ""; 
	//Iterate through all vertices of the graph 
	while(vertexIterator.hasNext()){
	    //get the next vertice we will schedule into slot i
	    vertexName = vertexIterator.next(); 
	    //another iterator to iterate over the vertices of the vertexName(including it) 
	    Iterator<String> iterator = graph.iterator();
	    //Stores the schedule 
	    String result = ""; 
	    //Alternative Vector representation for schedule for Extension 2
	    Vector<String> resultList = new Vector<String>(); 
	    //Look at all vertices and see if there are connections between the vertice and the vertexName
	    while(iterator.hasNext()){
		String vertex = iterator.next(); 
		if( !graph.containsEdge(vertexName,vertex) ){
		    boolean connection = false; 
		    //need to check if there are not mutual connections between vertice and another vertice not connected to vertexName
		    for(int a = 0; a<toRemove.size(); a++){
			if( graph.containsEdge(vertex,toRemove.get(a))){
			    connection = true; 
			    break; 
			}
		    }
		    if(!connection){
		    result = result + " " + vertex; 
		    resultList.add(vertex); 
		    alphabeticalList.add(vertex + " Slot:" + slot); 
		    toRemove.add(vertex); 
		    }
		}
	    }
	    //remove from graph 
	    for(int i = 0; i<toRemove.size(); i++){
		graph.remove(toRemove.get(i));
	    }
	    toRemove.clear(); 
	    vertexIterator = graph.iterator(); 
	    //update slotList (ext 2)
	    slotList.set(slot, resultList); 
	    //print out the schedule 
	    System.out.println(slot + result); 
	    ++slot; 
	}
	//Print out the alphabetical list (Extension 1) 
	Iterator<String> resultIterator = alphabeticalList.iterator(); 
	while(resultIterator.hasNext()){
	    System.out.println(resultIterator.next()); 
	}
    }

    public static void main(String args[]){
	Scheduler schedule = new Scheduler("medium.txt"); 
    }
    
}

