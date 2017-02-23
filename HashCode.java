import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class HashCode {
	
	static int[] videos;
	static Endpoint[] endpoints;
	static Request[] requests;
	static Cache[] caches;
	static int X;
	
	public static void main(String[] args) throws IOException {
		Scanner in = new Scanner(new FileInputStream(new File(args[0])));
		
		int V = in.nextInt();
		int E = in.nextInt();
		int R = in.nextInt();
		int C = in.nextInt();
		X = in.nextInt();
		
		in.nextLine();
		
		videos = new int[V];
		endpoints = new Endpoint[E];
		requests = new Request[R];
		caches = new Cache[C];
		
		for (int i = 0; i < V; i++) {
			videos[i] = in.nextInt();
		}
		
		in.nextLine();
		
		for (int i = 0; i < E; i++) {
			endpoints[i] = new Endpoint(in);
		}
		
		for (int i = 0; i < R; i++) {
			requests[i] = new Request(in.nextInt(), in.nextInt(), in.nextInt());
			in.nextLine();
		}
		
		for (int i = 0; i < C; i++) {
			caches[i] = new Cache(X, i);
		}
		
		for (int i = 0; i < E; i++) {
			endpoints[i].stageVideos();
		}
		
		List<Cache> cacheList = new ArrayList<>();
		
		for (int i = 0; i < C; i++) {
			caches[i].naiveVidAdd();
			if (caches[i].vidIdSize.size() > 0) {
				cacheList.add(caches[i]);
			}
		}
		
		
		System.out.println(cacheList.size());
		for (Cache c: cacheList) {
			System.out.print(c.id + " ");
			for (int id: c.vidIdSize.keySet()) {
				System.out.print(id + " ");
			}
			System.out.println();
		}
		
		
		in.close();
	}
}

class Cache {
	int limit;
	int left;
	int id;
	public HashMap<Integer, Integer> vidIdSize = new HashMap<>();
	public HashMap<Integer, Double> staging = new HashMap<>();
	
	public void naiveVidAdd() {
		List<TupleD> priorities = getSortedPriorities();
		for (TupleD tup: priorities) {
			if (!put(tup.a, HashCode.videos[tup.a])) {
				return;
			}
		}
	}
	
	public List<TupleD> getSortedPriorities() {
		List<TupleD> priorities = new ArrayList<>();
		for (Map.Entry<Integer, Double> entry: staging.entrySet()) {
			priorities.add(new TupleD(entry.getKey(), entry.getValue()));
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
		Double factor = staging.get(id);
		if (factor == null) {
			factor = 0.0;
		}
		staging.put(id, factor + ((double)requests/(double)latency) * factor2);
	}
	
	public boolean put(int id, int size) {
		if (left - size < 0) {
			return false;
		}
		if (vidIdSize.containsKey(id)) {
			return false;
		} else {
			left -= size;
			vidIdSize.put(id, size);
			return true;
		}
	}
	
	public void remove(int id) {
		Integer mb = vidIdSize.remove(id);
		if (mb != null) {
			limit += mb;
		}
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
	}
	
	public void sortCaches() {
		caches.sort(new Comparator<Tuple>() {

			@Override
			public int compare(Tuple arg0, Tuple arg1) {
				return Integer.compare(arg0.b, arg1.b);
			}
		});
	}
	
	public void stageVideos() {
		sortCaches();
		for (Request r: requests) {
			double factor = 1.0;
			for (Tuple pair: caches) {
				HashCode.caches[pair.a].stage(r.vId, r.requests, pair.b, factor);
				factor *= (1 - (double)HashCode.videos[r.vId] / (double)HashCode.X);
			}
		}
	}
}

class Tuple{
	int a;
	int b;
	
	public Tuple(int a, int b) {
		this.a = a;
		this.b = b;
	}
}

class TupleD{
	int a;
	double b;
	
	public TupleD(int a, double b) {
		this.a = a;
		this.b = b;
	}
}

class Request {
	int vId;
	int requests;
	
	public Request(int v, int e, int n) {
		this.vId = v;
		this.requests = n;
		
		if (HashCode.videos[v] <= HashCode.X) {
			HashCode.endpoints[e].requests.add(this);
		}
	}
}