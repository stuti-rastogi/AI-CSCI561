import java.io.*;
import java.util.*;


/**
 * Implement all zoo related condition checking and states
 * @author stuti
 *
 */
class Zoo implements Cloneable{
	private int zoo[][];
	private int n = 0;
	private int p = 0;
	private int t = 0;		// # of trees
	
	// constructor
	public Zoo(int n, int p, int t, int zoo[][]) {
		this.n = n;
		this.p = p;
		this.zoo = new int[n][n];
		this.zoo = zoo;
		this.t = t;
	}
	
	/**
	 * @return the zoo
	 */
	public int[][] getZoo() {
		return zoo;
	}

	/**
	 * @param zoo the zoo to set
	 */
	public void setZoo(int[][] zoo) {
		this.zoo = zoo;
	}

	/**
	 * @return the n
	 */
	public int getN() {
		return n;
	}

	/**
	 * @return the p
	 */
	public int getP() {
		return p;
	}

	/**
	 * @return the t
	 */
	public int getT() {
		return t;
	}
	
	// method to print the zoo as a board
	public void printZoo(BufferedWriter writer) throws IOException {
		for (int i = 0; i < n; i++)
	    {
	    		for (int j = 0; j < n; j++)
	    		{
	    			writer.write(Integer.toString(zoo[i][j]));
	    		}
	    		writer.write("\n");
	    }
	}
	
	public Search search(String searchMethod, long startTime)
	{
		Search searcher = new Search();
		Search result = new Search();
		
		if (this.t == 0)
		{
			if (p > n)
			{
				result.searchResult = 0;
				return result;
			}
			if (searchMethod.compareTo("BFS") == 0)
			{
				result = searcher.optimisedBfs(this);
			}
			
			if (searchMethod.compareTo("DFS") == 0)
			{
				result = searcher.optimisedDfs(this);
			}
			if (searchMethod.compareTo("SA") == 0)
			{
				result = searcher.sa(this, startTime);
			}
		}
		else
		{
			if (p > (this.n + this.t))
			{
				result.searchResult = 0;
				return result;
			}
			if (searchMethod.compareTo("BFS") == 0)
			{
				result = searcher.bfs(this, startTime);
			}
			
			if (searchMethod.compareTo("DFS") == 0)
			{
				result = searcher.dfs(this, startTime);
			}
			
			if (searchMethod.compareTo("SA") == 0)
			{
				result = searcher.sa(this, startTime);
			}
		}
		return result;
	}
	
	// if current configuration of zoo is safe
	public boolean isSafe(ArrayList<int[]> list, int x, int y)
	{
		int [][] matrixToCheck = new int[n][n];
		for (int i = 0; i < n; i++)
			{
				for (int j = 0; j < n; j++)
				{
					matrixToCheck[i][j] = this.getZoo()[i][j];
				}
			}
		
		for (int[] el: list)
		{
			matrixToCheck[el[0]][el[1]] = 1;
		}
		
		if (matrixToCheck[x][y] != 0)
			return false;
		
		// ROW LEFT
		int i = x-1;
		while (i >= 0)
		{
			// we reached a tree safely without encountering a lizard in between => safe
			if (matrixToCheck[i][y] == 2)
				break;
			//lizard before tree => unsafe
			if (matrixToCheck[i][y] == 1)
				return false;
			i--;
		}
		
		// ROW RIGHT
		i = x+1;
		while (i < n)
		{
			// we reached a tree safely without encountering a lizard in between => safe
			if (matrixToCheck[i][y] == 2)
				break;
			//lizard before tree => unsafe
			if (matrixToCheck[i][y] == 1)
				return false;
			i++;
		}
		
		// COLUMN UP
		int j = y-1;
		while (j >= 0)
		{
			// we reached a tree safely without encountering a lizard in between => safe
			if (matrixToCheck[x][j] == 2)
				break;
			//lizard before tree => unsafe
			if (matrixToCheck[x][j] == 1)
				return false;
			j--;
		}
		
		// COLUMN DOWN
		j = y+1;
		while (j < n)
		{
			// we reached a tree safely without encountering a lizard in between => safe
			if (matrixToCheck[x][j] == 2)
				break;
			//lizard before tree => unsafe
			if (matrixToCheck[x][j] == 1)
				return false;
			j++;
		}
		
		// DIAGONAL LEFT UP
		i = x-1;
		j = y-1;
		while (i >= 0 && j >= 0)
		{
			// we reached a tree safely without encountering a lizard in between => safe
			if (matrixToCheck[i][j] == 2)
				break;
			//lizard before tree => unsafe
			if (matrixToCheck[i][j] == 1)
				return false;
			i--;
			j--;
		}
		
		// DIAGONAL LEFT DOWN
		i = x-1;
		j = y+1;
		while (i >= 0 && j < n)
		{
			// we reached a tree safely without encountering a lizard in between => safe
			if (matrixToCheck[i][j] == 2)
				break;
			//lizard before tree => unsafe
			if (matrixToCheck[i][j] == 1)
				return false;
			i--;
			j++;
		}
		
		// DIAGONAL RIGHT DOWN
		i = x+1;
		j = y+1;
		while (i < n && j < n)
		{
			// we reached a tree safely without encountering a lizard in between => safe
			if (matrixToCheck[i][j] == 2)
				break;
			//lizard before tree => unsafe
			if (matrixToCheck[i][j] == 1)
				return false;
			i++;
			j++;
		}

		// DIAGONAL RIGHT UP
		i = x+1;
		j = y-1;
		while (i < n && j >= 0)
		{
			// we reached a tree safely without encountering a lizard in between => safe
			if (matrixToCheck[i][j] == 2)
				break;
			//lizard before tree => unsafe
			if (matrixToCheck[i][j] == 1)
				return false;
			i++;
			j--;
		}
		
		return true;
	}

	public ArrayList<HashSet<int[]>> expandArray(HashSet<int[]> current) {
		ArrayList<HashSet<int[]>> children =  new ArrayList<HashSet<int[]>>();
		for (int i = 0; i < n; i++)
		{
			for (int j = 0; j < n; j++)
			{
				if (this.isArraySafe(current, i, j))		//TODO
				{
					HashSet<int []> child = new HashSet<int []>();
					try
					{
						for (int [] location: current)
						{
							child.add(location);
						}
						int[] toAdd = {i, j}; 
						child.add(toAdd);
						children.add(child);
					}
					catch (Exception e)
					{
						System.err.println(e);
					}
				}
			}
		}
		return children;
	}

	private boolean isArraySafe(HashSet<int[]> current, int x, int y) {
		int [][] matrixToCheck = new int[n][n];
		for (int i = 0; i < n; i++)
			{
				for (int j = 0; j < n; j++)
				{
					matrixToCheck[i][j] = this.getZoo()[i][j];
				}
			}
		
		for (int [] position: current)
		{
			matrixToCheck[position[0]][position[1]] = 1;
		}
		
		if (matrixToCheck[x][y] != 0)
			return false;
		
		// ROW LEFT
		int i = x-1;
		while (i >= 0)
		{
			// we reached a tree safely without encountering a lizard in between => safe
			if (matrixToCheck[i][y] == 2)
				break;
			//lizard before tree => unsafe
			if (matrixToCheck[i][y] == 1)
				return false;
			i--;
		}
		
		// ROW RIGHT
		i = x+1;
		while (i < n)
		{
			// we reached a tree safely without encountering a lizard in between => safe
			if (matrixToCheck[i][y] == 2)
				break;
			//lizard before tree => unsafe
			if (matrixToCheck[i][y] == 1)
				return false;
			i++;
		}
		
		// COLUMN UP
		int j = y-1;
		while (j >= 0)
		{
			// we reached a tree safely without encountering a lizard in between => safe
			if (matrixToCheck[x][j] == 2)
				break;
			//lizard before tree => unsafe
			if (matrixToCheck[x][j] == 1)
				return false;
			j--;
		}
		
		// COLUMN DOWN
		j = y+1;
		while (j < n)
		{
			// we reached a tree safely without encountering a lizard in between => safe
			if (matrixToCheck[x][j] == 2)
				break;
			//lizard before tree => unsafe
			if (matrixToCheck[x][j] == 1)
				return false;
			j++;
		}
		
		// DIAGONAL LEFT UP
		i = x-1;
		j = y-1;
		while (i >= 0 && j >= 0)
		{
			// we reached a tree safely without encountering a lizard in between => safe
			if (matrixToCheck[i][j] == 2)
				break;
			//lizard before tree => unsafe
			if (matrixToCheck[i][j] == 1)
				return false;
			i--;
			j--;
		}
		
		// DIAGONAL LEFT DOWN
		i = x-1;
		j = y+1;
		while (i >= 0 && j < n)
		{
			// we reached a tree safely without encountering a lizard in between => safe
			if (matrixToCheck[i][j] == 2)
				break;
			//lizard before tree => unsafe
			if (matrixToCheck[i][j] == 1)
				return false;
			i--;
			j++;
		}
		
		// DIAGONAL RIGHT DOWN
		i = x+1;
		j = y+1;
		while (i < n && j < n)
		{
			// we reached a tree safely without encountering a lizard in between => safe
			if (matrixToCheck[i][j] == 2)
				break;
			//lizard before tree => unsafe
			if (matrixToCheck[i][j] == 1)
				return false;
			i++;
			j++;
		}

		// DIAGONAL RIGHT UP
		i = x+1;
		j = y-1;
		while (i < n && j >= 0)
		{
			// we reached a tree safely without encountering a lizard in between => safe
			if (matrixToCheck[i][j] == 2)
				break;
			//lizard before tree => unsafe
			if (matrixToCheck[i][j] == 1)
				return false;
			i++;
			j--;
		}
		
		return true;
	}

	public ArrayList<ArrayList<int[]>> expandDFS(ArrayList<int[]> current) {
		ArrayList<ArrayList<int[]>> children =  new ArrayList<ArrayList<int[]>>();
		for (int i = 0; i < n; i++)
		{
			for (int j = 0; j < n; j++)
			{
				if (this.isSafe(current, i, j))		//TODO
				{
					ArrayList<int []> child = new ArrayList<int []>();
					try
					{
						for (int [] location: current)
						{
							child.add(location);
						}
						int[] toAdd = {i, j}; 
						child.add(toAdd);
						children.add(child);
					}
					catch (Exception e)
					{
						System.err.println(e);
					}
				}
			}
		}
		return children;
	}	
}

/**
 * Implement the 3 search algorithms here
 * @author stuti
 *
 */
class Search {
	public int searchResult;
	public HashSet<Location> resultList;		//optimised
	public ArrayList<int[]> resultArray;		//dfs
	public HashSet<int[]> resultSA;			//bfs, sa
	
	// variables for n queens optimised code
	public int[] config;
	
	public Search()
	{
		this.searchResult = 0;
		this.resultList = new HashSet<Location>();
		this.resultArray = new ArrayList<int[]>();
		this.resultSA = new HashSet<int[]>();
		this.config = null;
	}
	
	public Search bfs (Zoo zoo, long startTime)
	{
		Queue<HashSet<int[]>> open = new LinkedList<HashSet<int[]>>(); 
		Set<Set<int[]>> closed = new HashSet<Set<int[]>>();
		
		HashSet<int[]> currentList = new HashSet<int[]>();
		
		open.add(currentList);
		
		while(true)
		{
//			if ((System.currentTimeMillis() - startTime) > 280000)
//			{
//				System.out.println("Time cutoff");
//				return this;
//			}
			
			if (open.isEmpty())
			{
				this.searchResult = 0;
				return this;
			}
			
			HashSet<int[]> toCheck = open.remove();
			System.out.println(toCheck.size());
			if (toCheck.size() == zoo.getP())
			{
				this.searchResult = 1;
				this.resultSA = toCheck;
				return this;
			}
			
			ArrayList<HashSet<int[]>> children = new ArrayList<HashSet<int[]>>();
			children = zoo.expandArray(toCheck);
			while (!children.isEmpty())
			{
				HashSet<int[]> child = children.remove(0);
				if (!closed.contains(child) && !checkQueueContainsChild(open, child))
				{
					open.add(child);
				}
			}
			closed.add(new HashSet<int[]>(toCheck));
		}
	}
	
	private boolean checkQueueContainsChild(Queue<HashSet<int[]>> open, HashSet<int[]> child) {
		// TODO Auto-generated method stub
		for (HashSet<int[]> set: open)
		{
			boolean result = true;
			for (int [] coordinates: child)
			{
				result = result && checkCoordinatesRepeated(set, coordinates);
			}
			if (result == true)
				return true;
		}
		return false;
	}

	public Search dfs (Zoo zoo, long startTime)
	{
		Stack<ArrayList<int[]>> open = new Stack<ArrayList<int[]>>(); 
		ArrayList<ArrayList<int[]>> closed = new ArrayList<ArrayList<int[]>>();
		
		ArrayList<int[]> currentList = new ArrayList<int[]>();
		
		open.add(currentList);
		
		while(true)
		{
//			if ((System.currentTimeMillis() - startTime) > 280000)
//				return this;
			if (open.isEmpty())
			{
				this.searchResult = 0;
				return this;
			}
			
			ArrayList<int[]> toCheck = open.pop();
			System.out.println(toCheck.size());
         
			if (toCheck.size() == zoo.getP())
			{
				this.searchResult = 1;
				this.resultArray = toCheck;
				return this;
			}
			
			ArrayList<ArrayList<int[]>> children = new ArrayList<ArrayList<int[]>>();
			children = zoo.expandDFS(toCheck);
			while (!children.isEmpty())
			{
				ArrayList<int[]> child = children.remove(0);
				if (!closed.contains(child) && !open.contains(child))
				{
					open.push(child);
				}
			}
			closed.add(toCheck);
		}
	}
	
	public Search sa (Zoo zoo, long startTime)
	{
		HashSet<int[]> initialConfig = generateRandomBoard(zoo);

		double temp = 100;
		int iter = 1;
		HashSet<int[]> currentState = initialConfig;
		while (true)
		{
			int currentE = calculateE(zoo, currentState);
			if (currentE == 0)
			{
				this.searchResult = 1;
				this.resultSA = currentState;
				break;
			}
         	long timePassed = (System.currentTimeMillis() - startTime);
         	//if (timePassed > 180
         	//System.out.println("Time passed: " + timePassed/1000 + "s");
			if (timePassed > 280000)
            {
				break;
            }
			HashSet<int[]> nextState = generateMove(zoo, currentState);
			int deltaE = calculateE(zoo, nextState) - calculateE(zoo, currentState);
			if (deltaE <= 0)
			{	
				currentState.removeAll(currentState);
				currentState.addAll(nextState);
			}
			else
			{
				double probability = Math.exp(-deltaE/temp);
				boolean accepting = shouldAccept(probability);
				if (accepting)
				{	
					currentState.removeAll(currentState);
					currentState.addAll(nextState);
				}
			}
			
			temp = 1 /(Math.log(1 + 5*iter));		// if iter = 1, avoid divide by zero

			iter = iter + 1;
		}
		return this;
	}
	
	private boolean shouldAccept(double probability) {
		// TODO Auto-generated method stub
		Random rand = new Random();
		if (rand.nextFloat() <= probability)
			return true;
		return false;
	}

	private HashSet<int[]> generateMove(Zoo zoo, HashSet<int[]> currentState) {
		// TODO Auto-generated method stub
		Random rand = new Random();
		int whichLizard = rand.nextInt(zoo.getP());
		
		//pick a random lizard to move
		ArrayList<int[]> stateArray = new ArrayList<int[]>();
		stateArray.addAll(currentState);
		int []replace = stateArray.get(whichLizard);
		HashSet<int[]> nextState = new HashSet<int []>();
		nextState.addAll(currentState);
		nextState.remove(stateArray.get(whichLizard));
		
		//keep trying random positions for that lizard till get an empty square
		while(true)
		{
			int x = rand.nextInt(zoo.getN());
			int y = rand.nextInt(zoo.getN());
			int []coordinates = {x, y};
			
			if ((zoo.getZoo()[x][y] != 2) && !checkCoordinatesRepeated(currentState, coordinates))
			{
				nextState.add(coordinates);
				break;
			}
		}
		return nextState;
	}

	private int calculateE(Zoo zoo, HashSet<int[]> currentState) {
		// TODO Auto-generated method stub
		int conflicts = 0;
		int n = zoo.getN();
		int [][] matrixToCheck = new int[n][n];
		for (int i = 0; i < n; i++)
			{
				for (int j = 0; j < n; j++)
				{
					matrixToCheck[i][j] = zoo.getZoo()[i][j];
				}
			}
		
		for (int []el: currentState)
		{
			matrixToCheck[el[0]][el[1]] = 1;
		}
		
		for (int []l: currentState)
		{
			int x = l[0];
			int y = l[1];
			
			// ROW LEFT
			int i = x-1;
			while (i >= 0)
			{
				// we reached a tree safely without encountering a lizard in between => safe
				if (matrixToCheck[i][y] == 2)
					break;
				//lizard before tree => unsafe
				if (matrixToCheck[i][y] == 1)
				{
					conflicts = conflicts + 1;
					break;
				}
				i--;
			}
				
			// ROW RIGHT
			i = x+1;
			while (i < n)
			{
				// we reached a tree safely without encountering a lizard in between => safe
				if (matrixToCheck[i][y] == 2)
					break;
				//lizard before tree => unsafe
				if (matrixToCheck[i][y] == 1)
				{
					conflicts = conflicts + 1;
					break;
				}
				i++;
			}
			
			// COLUMN UP
			int j = y-1;
			while (j >= 0)
			{
				// we reached a tree safely without encountering a lizard in between => safe
				if (matrixToCheck[x][j] == 2)
					break;
				//lizard before tree => unsafe
				if (matrixToCheck[x][j] == 1)
				{
					conflicts = conflicts + 1;
					break;
				}
				j--;
			}
			
			// COLUMN DOWN
			j = y+1;
			while (j < n)
			{
				// we reached a tree safely without encountering a lizard in between => safe
				if (matrixToCheck[x][j] == 2)
					break;
				//lizard before tree => unsafe
				if (matrixToCheck[x][j] == 1)
				{
					conflicts = conflicts + 1;
					break;
				}
				j++;
			}
				
			// DIAGONAL LEFT UP
			i = x-1;
			j = y-1;
			while (i >= 0 && j >= 0)
			{
				// we reached a tree safely without encountering a lizard in between => safe
				if (matrixToCheck[i][j] == 2)
					break;
				//lizard before tree => unsafe
				if (matrixToCheck[i][j] == 1)
				{
					conflicts = conflicts + 1;
					break;
				}
				i--;
				j--;
			}
			
			// DIAGONAL LEFT DOWN
			i = x-1;
			j = y+1;
			while (i >= 0 && j < n)
			{
				// we reached a tree safely without encountering a lizard in between => safe
				if (matrixToCheck[i][j] == 2)
					break;
				//lizard before tree => unsafe
				if (matrixToCheck[i][j] == 1)
				{
					conflicts = conflicts + 1;
					break;
				}
				i--;
				j++;
			}
				
			// DIAGONAL RIGHT DOWN
			i = x+1;
			j = y+1;
			while (i < n && j < n)
			{
				// we reached a tree safely without encountering a lizard in between => safe
				if (matrixToCheck[i][j] == 2)
					break;
				//lizard before tree => unsafe
				if (matrixToCheck[i][j] == 1)
				{
					conflicts = conflicts + 1;
					break;
				}
				i++;
				j++;
			}

			// DIAGONAL RIGHT UP
			i = x+1;
			j = y-1;
			while (i < n && j >= 0)
			{
				// we reached a tree safely without encountering a lizard in between => safe
				if (matrixToCheck[i][j] == 2)
					break;
				//lizard before tree => unsafe
				if (matrixToCheck[i][j] == 1)
				{
					conflicts = conflicts + 1;
					break;
				}
				i++;
				j--;
			}
		}
		return conflicts/2;
	}

	private HashSet<int[]> generateRandomBoard(Zoo zoo) {
		// TODO Auto-generated method stub
		HashSet<int[]> randomConfig = new HashSet<int[]>();
		
		Random rand = new Random();
		for (int i = 0; i < zoo.getP(); i++)
		{
			int x = rand.nextInt(zoo.getN());
			int y = rand.nextInt(zoo.getN());
			int []coordinates = {x, y};
			
			if ((zoo.getZoo()[x][y] == 2) || checkCoordinatesRepeated(randomConfig, coordinates))
			{
				i = i - 1;
			}
			else
			{
				randomConfig.add(coordinates);
				
			}
		}
		return randomConfig;
	}

	private boolean checkCoordinatesRepeated(HashSet<int[]> randomConfig, int[] coordinates) {
		// TODO Auto-generated method stub
		for (int []entry: randomConfig)
		{
			if (Arrays.equals(entry, coordinates))
				return true;
		}
		return false;
	}

	private boolean containsCheck (Queue<HashSet<Location>> list, HashSet<Location> child)
	{
		for (HashSet<Location> l: list)
		{
			if (l.containsAll(child))
				return true;
		}
		return false;
	}

	public Search optimisedDfs(Zoo zoo) 
	{
		// TODO Auto-generated method stub
		// keep array of size n, for each col which row queen is in
		// i think all are initialised to 0 - check - yes!
		this.config = new int[zoo.getN()];
		for (int i = 0; i < zoo.getN(); i++)
					this.config[i] = -1;
						
				Stack<Queen> open = new Stack<Queen>();
				Set<Queen> closed = new HashSet<Queen>();
				
				open.push(new Queen(config));
				
				while(true)
				{
					// won't ever happen, n queens always has a solution for p <= n
					if (open.isEmpty())
					{
						this.searchResult = 0;
						return this;
					}
					
					Queen current = open.pop();
					if (queensPlaced(zoo, current))
					{
						this.searchResult = 1;
						this.resultList = generateLocations(zoo, current.config);
						return this;
					}
					
					ArrayList<Queen> children = new ArrayList<Queen>();
					children = generateConfigs(zoo, current);
					while (!children.isEmpty())
					{
						Queen child = children.remove(0);
						if (!closed.contains(child) && !open.contains(child))
						{
							//System.out.println(child.config.length);
							open.add(child);
						}
					}
					closed.add(current);
				}
	}

	public Search optimisedBfs(Zoo zoo) {
		// TODO Auto-generated method stub
		
		// keep array of size n, for each col which row queen is in
		// i think all are initialised to 0 - check - yes!
		this.config = new int[zoo.getN()];
		for (int i = 0; i < zoo.getN(); i++)
			this.config[i] = -1;
				
		Queue<Queen> open = new LinkedList<Queen>();
		Set<Queen> closed = new HashSet<Queen>();
		
		open.add(new Queen(config));
		
		while(true)
		{
			// won't ever happen, n queens always has a solution for p <= n
			if (open.isEmpty())
			{
				this.searchResult = 0;
				return this;
			}
			
			Queen current = open.remove();
			if (queensPlaced(zoo, current))
			{
				this.searchResult = 1;
				this.resultList = generateLocations(zoo, current.config);
				return this;
			}
			
			ArrayList<Queen> children = new ArrayList<Queen>();
			children = generateConfigs(zoo, current);
			while (!children.isEmpty())
			{
				Queen child = children.remove(0);
				if (!closed.contains(child) && !open.contains(child))
				{
					open.add(child);
				}
			}
			closed.add(current);
		}
	}

	private ArrayList<Queen> generateConfigs(Zoo zoo, Queen current) {
		// TODO Auto-generated method stub
		ArrayList<Queen> children = new ArrayList<Queen>();
		
		for (int i = 0; i < zoo.getN(); i++)
		{
			Queen child = new Queen(new int[zoo.getN()]);
			for (int j = 0; j < current.lizardsPlaced; j++)
				child.config[j] = current.config[j];
			child.config[current.lizardsPlaced] = i;
			child.lizardsPlaced = current.lizardsPlaced;
			if (nonConflict(zoo, child))
			{
				child.lizardsPlaced = current.lizardsPlaced + 1;
				children.add(child);
			}
		}
		return children;
	}

	private boolean nonConflict(Zoo zoo, Queen child) {
		// TODO Auto-generated method stub
		// check for all previous cols
		for (int i = 0; i < child.lizardsPlaced; i++)
		{
			// same row
			if (child.config[i] == child.config[child.lizardsPlaced])
				return false;
			// lower diagonal
			if (child.config[child.lizardsPlaced] - child.config[i] == (child.lizardsPlaced - i))
				return false;
			// upper diagonal
			if (child.config[i] - child.config[child.lizardsPlaced] == (child.lizardsPlaced - i))
				return false;
		}
		return true;
	}

	private HashSet<Location> generateLocations(Zoo zoo, int[] config) {
		// TODO Auto-generated method stub
		// from array, generate set of locations to maintain consistency
		HashSet<Location> finalConfig = new HashSet<Location>();
		for (int i = 0; i < zoo.getN(); i++)
		{
			finalConfig.add(new Location(i, config[i]));
		}
		return finalConfig;
	}

	// goal test for n queens
	private boolean queensPlaced(Zoo zoo, Queen current) {
		// TODO Auto-generated method stub
		if (current.lizardsPlaced == zoo.getP())
			return true;
		return false;
	}
}


/******************LOCATION CLASS*********************************/

class Location implements Cloneable{
	public int x;
	public int y;
	
	public Location(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}
	
	public boolean equals (Object obj)
	{
		Location other = (Location) obj;
		if (this.x == other.x && this.y == other.y)
			return true;
		return false;
	}
}


class Queen {
	public int[] config;
	public int lizardsPlaced;
	
	public Queen(int[] config)
	{
		this.config = config;
		this.lizardsPlaced = 0;
	}
}



/**
 * class holding the run and print methods
 * @author stuti
 *
 */
class homework {

	public static void main(String[] args) {
		try {
			long startTime = System.currentTimeMillis();
			BufferedReader reader = new BufferedReader(new FileReader("DFS15.txt"));
		    
			String searchMethod = null;
		    searchMethod = reader.readLine();
		    
		    String nReader = reader.readLine();
		    int n = Integer.parseInt(nReader.trim());
		    //int n = 12;
		    String pReader = reader.readLine();
		    int p = Integer.parseInt(pReader.trim());
		    
		    int trees = 0;
		    
		    int zooRead[][] = new int[n][n];
		    
		    for (int i = 0; i < n; i++)
		    {
		    		String row = reader.readLine();
		    		for (int j = 0; j < n; j++)
		    		{
		    			zooRead[i][j] = Integer.parseInt(row.substring(j, j+1));
		    			if (zooRead[i][j] == 2)
		    				trees = trees + 1;
		    		}
		    }
		    
		    for (int i = 0; i < n; i++)
		    {
		    		for (int j = 0; j < n; j++)
		    			System.out.print(zooRead[i][j] + " ");
		    		System.out.println();
		    }
		    
		    Zoo zoo = new Zoo(n, p, trees, zooRead);
		    
		    Search searchResult = zoo.search(searchMethod, startTime);
    			BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"));
		    if (searchResult.searchResult == 0)
		    {
		    		writer.write("FAIL\n");
		    		writer.close();
		    }
		    else
		    {
		    		writer.write("OK\n");
				for (Location l: searchResult.resultList)
		    		{
		    			zoo.getZoo()[l.x][l.y] = 1;
		    		}
				for (int[] el: searchResult.resultArray)
		    		{
		    			//System.out.println("x: " + l.x + ", y: " + l.y);
		    			zoo.getZoo()[el[0]][el[1]] = 1;
		    		}
				for (int []coord: searchResult.resultSA)
		    		{
		    			//System.out.println("x: " + coord[0] + ", y: " + coord[1]);
		    			zoo.getZoo()[coord[0]][coord[1]] = 1;
		    		}
		    		zoo.printZoo(writer);
		    		writer.close();
		    }

		    reader.close();
		    
		    
		    
		    long endTime = System.currentTimeMillis();
         	 //System.out.println("Took " + (endTime - startTime) * 0.001 + " s");
		}
		catch (IOException x) {
		    System.err.println(x);
		}
	}
}


