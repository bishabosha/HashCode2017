import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class HashCode {
	
	static int[] videos;
	static Endpoint[] endpoints;
	static Cache[] caches;
	static int X;
	static int V;
	
	public static void main(String[] args) throws IOException {
		Scanner in = new Scanner(new FileInputStream(new File(args[0])));
		
		V = in.nextInt();
		int E = in.nextInt();
		int R = in.nextInt();
		int C = in.nextInt();
		X = in.nextInt();
		
		in.nextLine();
		
		videos = new int[V];
		endpoints = new Endpoint[E];
		caches = new Cache[C];
		
		for (int i = 0; i < V; i++) {
			videos[i] = in.nextInt();
		}
		
		in.nextLine();
		
		for (int i = 0; i < C; i++) {
			caches[i] = new Cache(X, i);
		}
		
		for (int i = 0; i < E; i++) {
			endpoints[i] = new Endpoint(in);
		}
		
		for (int i = 0; i < R; i++) {
			int v = in.nextInt();
			int e = in.nextInt();
			int r = in.nextInt();
			if (videos[v] <= X) {
				endpoints[e].addRequest(v, r);
			}
			in.nextLine();
		}
		
		List<Cache> cacheList = new ArrayList<>();
		
		for (int i = 0; i < C; i++) {
			caches[i].vidAdd();
			if (caches[i].hasVideos()) {
				cacheList.add(caches[i]);
			}
		}
		
		System.out.println(cacheList.size());
		for (Cache c: cacheList) {
			System.out.print(c.id + " ");
			int count = 0;
			for (int i = 0; i < c.videos.length; i++) {
				if (c.videos[i] > 0) {
					System.out.print(i + " ");
					count += videos[i];
				}
			}
			System.out.print(count <= X);
			System.out.println();
			caches[c.id] = null;
		}
		
		in.close();
	}
}

class Cache {
	int limit;
	int left;
	int id;
	public double[] staging = new double[HashCode.V];
	public int[] videos = new int[HashCode.V];
	
	public void naiveVidAdd() {
		List<TupleD> priorities = getSortedPriorities();
		for (TupleD tup: priorities) {
			if (!put(tup.a, HashCode.videos[tup.a])) {
				return;
			}
		}
	}
	
	public void vidAdd() {
		List<TupleD> priorities = getSortedPriorities();
		boolean[] bestSet = new boolean[HashCode.V];
		double bestPriority = 0.0;
		
		for(int i=0; i<priorities.size(); i++) {
			boolean[] set = new boolean[HashCode.V];
			int memory = 0;
			double priority = 0.0;
			
			for(int j=i+1; j<priorities.size(); j++) {
				int videoId = priorities.get(j).a;
				
				if(memory == limit) {
					break;
				}
				else if(memory + HashCode.videos[videoId] <= limit) {
					memory = memory + HashCode.videos[videoId];
					priority = priority + priorities.get(j).b;
					set[videoId] = true;
				}
			}
			
			if(priority > bestPriority) {
				bestPriority = priority;
				bestSet = set;
			}
		}
		
		for (int i = 0; i < HashCode.V; i++) {
			if (bestSet[i]) {
				put(i, HashCode.videos[i]);
			}
		}
	}
	
	public List<TupleD> getSortedPriorities() {
		List<TupleD> priorities = new ArrayList<>();
		for (int i = 0; i < HashCode.V; i++) {
			if (staging[i] > 0) {
				priorities.add(new TupleD(i, staging[i]));
			}
		}
		priorities.sort(new Comparator<TupleD>() {
			@Override
			public int compare(TupleD arg0, TupleD arg1) {
				return Double.compare(arg1.b, arg0.b);
			}
		});
		return priorities;
	}
	
	public void stage(int id, int requests, int latency, double factor2) {
		double factor = staging[id];
		staging[id] = factor + ((double)requests/(double)latency) * factor2;
	}
	
	public boolean hasVideos() {
		for (int i = 0; i < videos.length; i++) {
			if (videos[i] > 0) {
				return true;
			}
		}
		return false;
	}
	
	public boolean put(int id, int size) {
		if (left - size < 0 || videos[id] > 0) {
			return false;
		} else {
			left -= size;
			videos[id] = size;
			return true;
		}
	}
	
	public void remove(int id) {
		int mb = videos[id];
		if (mb > 0) {
			left += mb;
		}
		videos[id] = 0;
	}
	
	public Cache(int limit, int id) {
		this.limit = limit;
		this.left = limit;
		this.id = id;
	}
}

class Endpoint {
	int latencyDataCentre;
	
	List<Tuple> caches = new ArrayList<>();	
	List<Request> requests = new ArrayList<>();
	
	public Endpoint(Scanner in) {
		latencyDataCentre = in.nextInt();
		int K = in.nextInt();
		
		in.nextLine();
		
		for (int i = 0; i < K; i++) {
			caches.add(new Tuple(in.nextInt(), in.nextInt()));
			in.nextLine();
		}
		
		sortCaches();
	}
	
	public void sortCaches() {
		caches.sort(new Comparator<Tuple>() {

			@Override
			public int compare(Tuple arg0, Tuple arg1) {
				return Integer.compare(arg0.b, arg1.b);
			}
		});
	}
	
	public void addRequest(int v, int r) {
		double factor = 1.0;
		for (Tuple pair: caches) {
			HashCode.caches[pair.a].stage(v, r, pair.b, factor);
			factor *= (1 - (double)HashCode.videos[v] / (double)HashCode.X);
		}
	}
}

class Tuple {
	int a;
	int b;
	
	public Tuple(int a, int b) {
		this.a = a;
		this.b = b;
	}
}

class TupleD {
	int a;
	double b;
	
	public TupleD(int a, double b) {
		this.a = a;
		this.b = b;
	}
}