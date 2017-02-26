/**
 * Copyright (2017) James Thompson, Brandon Hewer
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CacheAllocator {
	
	static int[] videos;
	static Endpoint[] endpoints;
	static Cache[] caches;
	static int cacheLimit;
	static int numVideos;
	
	public static void main(String[] args) throws IOException {
		Scanner in = new Scanner(new FileInputStream(new File(args[0])));
		
		numVideos        = in.nextInt();
		int numEndpoints = in.nextInt();
		int numRequests  = in.nextInt();
		int numCaches    = in.nextInt();
		cacheLimit       = in.nextInt();
		
		in.nextLine();
		
		videos    = new int[numVideos];
		endpoints = new Endpoint[numEndpoints];
		caches    = new Cache[numCaches];
		
		for (int i = 0; i < numVideos; i++) {
			videos[i] = in.nextInt();
		}
		
		in.nextLine();
		
		for (int i = 0; i < numCaches; i++) {
			caches[i] = new Cache(i);
		}
		
		for (int i = 0; i < numEndpoints; i++) {
			endpoints[i] = new Endpoint(in);
		}
		
		for (int i = 0; i < numRequests; i++) {
			int videoId       = in.nextInt();
			int endpointId    = in.nextInt();
			int videoRequests = in.nextInt();
			if (videos[videoId] <= cacheLimit) {
				endpoints[endpointId].addRequest(videoId, videoRequests);
			}
			in.nextLine();
		}
		
		List<Cache> cacheList = new ArrayList<>();
		
		for (int i = 0; i < numCaches; i++) {
			if (caches[i].vidAdd()) {
				cacheList.add(caches[i]);
			}
		}
		
		System.out.println(cacheList.size());
		for (Cache c: cacheList) {
			System.out.print(c.id + " ");
			for (int i = 0; i < c.videos.length; i++) {
				if (c.videos[i] > 0) {
					System.out.print(i + " ");
				}
			}
			System.out.println();
			caches[c.id] = null;
		}
		
		in.close();
	}
}

class Cache {
	int id;
	int freeCapacity;
	public double[] staging;
	public int[] videos;
	
	public Cache(int id) {
		this.id = id;
		resetVideos();
		staging = new double[CacheAllocator.numVideos];
	}
	
	public boolean vidAdd() {
		List<TupleD> priorities = getSortedPriorities();
		boolean[] bestSet       = new boolean[CacheAllocator.numVideos];
		double bestPriority     = 0.0;
		boolean[] set           = new boolean[CacheAllocator.numVideos];
		double priorityTotal    = 0.0;
		
		for(int i = 0; i < priorities.size(); i++) {
			resetVideos();
			set = new boolean[CacheAllocator.numVideos];
			priorityTotal = 0.0;
			
			for(int j = i; j < priorities.size(); j++) {
				TupleD priority = priorities.get(j);
				if (freeCapacity == 0) {
					break;
				} else if (add(priority.a)) {
					priorityTotal += priority.b;
					set[priority.a] = true;
				}
			}
			
			if(priorityTotal > bestPriority) {
				bestPriority = priorityTotal;
				bestSet = set;
			}
		}
		
		resetVideos();
		boolean result = false;
		for (int i = 0; i < bestSet.length; i++) {
			if (bestSet[i]) {
				if (add(i)) {					
					result = true;
				} else {
					break;
				}
			}
		}
		
		return result;
	}
	
	public List<TupleD> getSortedPriorities() {
		List<TupleD> priorities = new ArrayList<>();
		for (int i = 0; i < CacheAllocator.numVideos; i++) {
			final double priority = staging[i];
			if (priority > 0) {
				priorities.add(new TupleD(i, priority));
			}
		}
		priorities.sort((x, y) -> Double.compare(y.b, x.b));
		return priorities;
	}
	
	public void stage(int id, int requests, int latency, double factor) {
		staging[id] += ((double)requests/(double)latency) * factor;
	}
	
	public boolean add(int id) {
		final int size = CacheAllocator.videos[id];
		if (freeCapacity - size < 0) {
			return false;
		} else {
			freeCapacity -= size;
			videos[id] = size;
			return true;
		}
	}
	
	private void resetVideos() {
		freeCapacity = CacheAllocator.cacheLimit;
		videos = new int[CacheAllocator.numVideos];
	}
}

class Endpoint {
	int latencyDataCentre;
	
	List<Tuple> caches = new ArrayList<>();
	
	public Endpoint(Scanner in) {
		latencyDataCentre = in.nextInt();
		int numCacheConnections = in.nextInt();
		
		in.nextLine();
		
		for (int i = 0; i < numCacheConnections; i++) {
			caches.add(new Tuple(in.nextInt(), in.nextInt()));
			in.nextLine();
		}
		
		caches.sort((x, y) -> Integer.compare(x.b, y.b));
	}
	
	public void addRequest(int v, int r) {
		double factor = 1.0;
		for (Tuple pair: caches) {
			CacheAllocator.caches[pair.a].stage(v, r, pair.b, factor);
			factor *= getProbablilty(v, pair.b);
		}
	}
	
	public double getProbablilty(int v, double latency) {
		return 1.0 - CacheAllocator.videos[v] / (double)(CacheAllocator.cacheLimit + latency);
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