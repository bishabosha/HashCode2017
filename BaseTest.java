import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class BaseTest {
	
	static int[] videos;
	static BaseEndpoint[] endpoints;
	static int X;
	static int V;
	
	public static void main(String[] args) throws IOException {
		Scanner in = new Scanner(new FileInputStream(new File(args[0])));
		
		V = in.nextInt();
		int E = in.nextInt();
		int R = in.nextInt();
		in.nextInt();
		X = in.nextInt();
		
		in.nextLine();
		
		videos = new int[V];
		endpoints = new BaseEndpoint[E];
		
		for (int i = 0; i < V; i++) {
			videos[i] = in.nextInt();
		}
		
		in.nextLine();
		
		for (int i = 0; i < E; i++) {
			endpoints[i] = new BaseEndpoint(in);
		}
		
		long total = 0;
		
		for (int i = 0; i < R; i++) {
			in.nextInt();
			int e = in.nextInt();
			int r = in.nextInt();
			
			total += r * endpoints[e].latencyDataCentre;
			
			in.nextLine();
		}
		
		System.out.println(total);
		
		in.close();
	}
}

class BaseEndpoint {
	int latencyDataCentre;
	
	public BaseEndpoint(Scanner in) {
		latencyDataCentre = in.nextInt();
		int K = in.nextInt();
		
		for (int i = 0; i < K; i++) {
			in.nextInt();
			in.nextInt();
			in.nextLine();
		}
	}
}