import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Graphs {
	@SuppressWarnings("null")
	public static Path shortestPath(Graph graph, String from, String to) {
		final Path path = new Path();

		//Copy of passed value
		//		String copyFrom = from;
		String copyTo = to;

		//DUMMY CODE (STUB) TODO
		Map<String,Double> distances = new HashMap<String,Double>(); // North York - Scarborough 16.7
		// eg:- distances.add(Scarborough,21.6) if the starting point is North York

		Map<String,String> edgeTo = new HashMap<String,String>(); // Destination - Origin ---> it must be Origin - destination
		// eg:- edgeTo.add(Scarborough,North York)

		//		Map<String,Double> cities = new TreeMap<String,Double>();

		// (1,1) , (2,3)
		// (2,4) , (1,1)
		Map<Double,String> cities = new TreeMap<Double,String>();
		cities.put(0.0,from);
		distances.put(from, 0.0);
		// when updating the distance in double check 
		//
		// 0-(1)-->1--(2)-->2
		// |-------(4)-------^


		while(!cities.isEmpty()) {            
			//				System.out.println("Smallest value := " +cities.get(((TreeMap<Double, String>) cities).firstKey()));

			double value = ((TreeMap<Double, String>) cities).firstKey();

			from = cities.get(value);




			for (Edge e : graph.adj(new Vertex(from))) {

				String tempString = e.toString();
				String destination = tempString.substring(tempString.indexOf('-')+2,tempString.indexOf('=')-1);
				String origin = tempString.substring(tempString.indexOf('[')+1,tempString.indexOf(']'));



				//------- DEBUG CODE -------//
				//				System.out.println(e.toString());
				//				System.out.println(origin);
				//				System.out.println(place);

				if(!distances.containsKey(destination)) {
					distances.put(destination, e.weight() + distances.get(from));
					edgeTo.put(destination, origin);
					//					edgeTo.put(origin,destination);
					cities.put(distances.get(destination), destination);
				} else {
					// we need to compute the distance and check if new distance is less then update or else do nothing
					double newWeight = e.weight() + distances.get(from); //16.6
					if(newWeight < distances.get(destination)) {
						// update the distance
						distances.remove(destination);
						distances.put(destination, newWeight);
						edgeTo.put(destination, origin);
						//						edgeTo.put(origin,destination);
						cities.put(distances.get(destination), destination);
					} 
				}


				//-------------------------- Debug code 3 lines ---------------//
				//				System.out.println("distances := " + distances);
				//				System.out.println("edgeTo := " + edgeTo);
				//				System.out.println("cities := " + cities);



			}
			// removing the value of from from the list as we have already visited it
			cities.remove(value);
		}

		//----------------- DEBUG CODE --------------------//
		//		System.out.println("distances := " + distances);
		//		System.out.println("edgeTo := " + edgeTo);
		//		System.out.println("cities := " + cities);
		//		
		//		System.out.println();
		//		System.out.println("The shortest path from " + copyFrom + " to " + copyTo + " is " + distances.get(copyTo) + " Km");

		List<Edge> temppathList = new ArrayList<Edge>();


		while(edgeTo.containsKey(copyTo)) {
			// edge has Etibicoke in it so we need an edge from etobickoe to toronto
			String nextStop = edgeTo.get(copyTo);

			for(Edge e: graph.adj(new Vertex(nextStop))) {
				String tempString = e.toString();
				String destination = tempString.substring(tempString.indexOf('-')+2,tempString.indexOf('=')-1);

				if(destination.compareTo(copyTo) == 0) {
					temppathList.add(e);
				}
			}

			copyTo = nextStop;
		}



		Collections.reverse(temppathList);

		for(Edge e : temppathList) {
			path.getList().add(e);
		}

		//		System.out.println(temppathList);
		return path;
	}

	public static Graph MST(Graph graph) {
		Map<String,Edge> MSTEdge = new HashMap<String,Edge>();

		Map<Double,Edge> edgeTo = new TreeMap<Double,Edge>(); // to grab minimum weight from current list

		List<String> cities = new ArrayList<>();

		class MSTgraph extends Graph{

			public MSTgraph() {
				this.adj = new HashMap<String, List<Edge>>();
			}
		}

		Graph finalGraph = new MSTgraph();


		cities.add("Toronto");
		MSTEdge.put("Toronto", null);



		while(MSTEdge.size() != 40 ) { // cities.size() < 40



			// the error is occurring because we have [Etobicoke]-[Woodbridge]=24.5 km && 24.5=[Elmira]-[Fergus]=24.5 km with same key 

			for(String from : cities) {
				for(Edge e: graph.adj(new Vertex(from))) {
					String tempString = e.toString();
					String destination = tempString.substring(tempString.indexOf('-')+2,tempString.indexOf('=')-1);
					if(!MSTEdge.containsKey(destination)) { // changed added one condition
						edgeTo.put(e.weight(),e);
					}
				}
			}



			Edge smallest = edgeTo.get(((TreeMap<Double, Edge>) edgeTo).firstKey());
			Double smallestKey = ((TreeMap<Double, Edge>) edgeTo).firstKey(); // first smallest key
			String origin = smallest.toString().substring(smallest.toString().indexOf('[')+1,smallest.toString().indexOf(']'));
			String destination = smallest.toString().substring(smallest.toString().indexOf('-')+2,smallest.toString().indexOf('=')-1);


			//	                       ** DEBUG CODE **                              //		
			//				System.out.println("in if");
			//				System.out.println(edgeTo);
			//				System.out.println(cities);
			//				System.out.println(destination);
			//			}
			//			System.out.println(cities);

			while(cities.contains(destination)) { 
				edgeTo.remove(smallestKey);
				smallest = edgeTo.get(((TreeMap<Double, Edge>) edgeTo).firstKey());
				smallestKey = ((TreeMap<Double, Edge>) edgeTo).firstKey();
				origin = smallest.toString().substring(smallest.toString().indexOf('[')+1,smallest.toString().indexOf(']'));
				destination = smallest.toString().substring(smallest.toString().indexOf('-')+2,smallest.toString().indexOf('=')-1);
			}

			for(Edge reverse : graph.adj(new Vertex(destination))) {
				String dest = reverse.toString().substring(reverse.toString().indexOf('-')+2,reverse.toString().indexOf('=')-1);
				if(dest.compareTo(origin) == 0) {
					if( reverse != null) {

						if(!finalGraph.adj.containsKey(destination)) {
							ArrayList<Edge> edgesList = new ArrayList<Edge>();
							edgesList.add(reverse);
							finalGraph.adj.put(destination, edgesList);
						} else {
							finalGraph.adj.get(destination).add(reverse);
						}
					}
				}
			}


			cities.add(destination);
			//			System.out.println(smallest);
			MSTEdge.put(destination, smallest); // changed origin to destination

			if( smallest != null) {

				if(!finalGraph.adj.containsKey(origin)) {
					ArrayList<Edge> edgesList = new ArrayList<Edge>();
					edgesList.add(smallest);
					finalGraph.adj.put(origin, edgesList);
				} else {
					finalGraph.adj.get(origin).add(smallest);
				}
			}

			edgeTo.clear();
			//    DEBUG CODE    //
			//			System.out.println("MSTEdge := " + MSTEdge);
			//			System.out.println("MSTEdge count := " + MSTEdge.size());
			//			System.out.println("cities count := " + cities.size());

		}

		finalGraph.E = MSTEdge.size() - 1;
		finalGraph.V = MSTEdge.size();



		return finalGraph;
	}
}
