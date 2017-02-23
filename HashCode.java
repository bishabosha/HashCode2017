import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class HashCode {
	
	static int[] videos;
	static Endpoint[] endpoints;
	static Request[] requests;
	static Cache[] caches;
	
	public static void main(String[] args) throws IOException {
		Scanner in = new Scanner(new FileInputStream(new File(args[0])));
		
		int V = in.nextInt();
		int E = in.nextInt();
		int R = in.nextInt();
		int C = in.nextInt();
		int X = in.nextInt();
		
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
			caches[i] = new Cache(X);
		}
		
		in.close();
	}
}

class Cache {
	int limit;
	Set<Integer> videos = new HashSet<>();
	public Cache(int limit) {
		this.limit = limit;
	}
}

class Endpoint {
	int latencyDataCentre;
	HashMap<Integer, Integer> cacheIdLatency = new HashMap<>();
	
	public Endpoint(Scanner in) {
		latencyDataCentre = in.nextInt();
		int K = in.nextInt();
		
		in.nextLine();
		
		for (int i = 0; i < K; i++) {
			cacheIdLatency.put(in.nextInt(), in.nextInt());
			in.nextLine();
		}
	}
}

class Request {
	int vId;
	int eId;
	int requests;
	
	public Request(int v, int e, int n) {
		this.vId = v;
		this.eId = e;
		this.requests = n;
	}
	
	@Override
	public String toString() {
		return vId + "|" + eId + "|" + requests;
	}
}