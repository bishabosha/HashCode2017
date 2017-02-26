import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

public class BaseRequestTime {
	
	static int[] latencies;
	
	public static void main(String[] args) throws IOException {
		Scanner in = new Scanner(new FileInputStream(new File(args[0])));
		
		int V = in.nextInt();
		int E = in.nextInt();
		int R = in.nextInt();
		in.nextInt();
		in.nextInt();
		in.nextLine();
		
		for (int i = 0; i < V; i++) {
			in.nextInt();
		}
		
		in.nextLine();
		
		latencies = new int[E];
		for (int i = 0; i < E; i++) {
			latencies[i] = in.nextInt();
			int K = in.nextInt();
			for (int j = 0; j < K; j++) {
				in.nextInt();
				in.nextInt();
				in.nextLine();
			}
		}
		
		long total = 0;
		
		for (int i = 0; i < R; i++) {
			in.nextInt();
			int e = in.nextInt();
			int r = in.nextInt();
			
			total += r * latencies[e];
			
			in.nextLine();
		}
		
		System.out.println(total);
		
		in.close();
	}
}