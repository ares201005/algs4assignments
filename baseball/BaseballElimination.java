
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FordFulkerson;
import java.util.Map;
import java.util.HashMap;

public class BaseballElimination{

   private final HashMap<String, Integer> teams; // mapping team to an ID
   private final int N;       // number of teams
   private final int[] w;     // wins
   private final int[] l;     // losses
   private final int[] r;     // remaining games;
   private final int[][] g;   // games between two teams
   private boolean[] e;       // eliminated or not
   private boolean[] checked; // was already checked or not
   private Iterable<String>[] certificate;

   private FlowNetwork flownetwork; 

   // create a baseball division from given filename in format specified below
   public BaseballElimination(String filename)   
   { 
      In in = new In(filename);
      String line = in.readLine();
      N = Integer.parseInt(line);
      w = new int[N];
      l = new int[N];
      r = new int[N];
      e = new boolean[N];
      checked = new boolean[N];
      g = new int[N][N];
      certificate = new Iterable[N];

      teams = new HashMap<String, Integer>();

      int i = 0;
      while (in.hasNextLine()) { 
         line = in.readLine();
         String[] keys = line.trim().split("\\s+");
         teams.put(keys[0], i);

         w[i] = Integer.parseInt(keys[1]);
         l[i] = Integer.parseInt(keys[2]);
         r[i] = Integer.parseInt(keys[3]);
         for (int j = 0; j < N; j++){ 
            g[i][j] = Integer.parseInt(keys[4 + j]);
         }
         i++;
      }

   }

   // number of teams
   public  int numberOfTeams()
   { 
      return N;
   }

   // all teams
   public Iterable<String> teams() 
   { 
       return teams.keySet();
   } 

   // check team validity
   private void checkTeam(String team) { 
      if (!teams.containsKey(team)) throw new IllegalArgumentException();
   } 

   // number of wins for given team
   public    int wins(String team)
   { 
      checkTeam(team);
      int i = teams.get(team);
      return w[i];
   }

   // number of losses for given team
   public              int losses(String team)  
   { 
      checkTeam(team);
      int i = teams.get(team);
      return l[i];
   }

   // number of remaining games for given team
   public              int remaining(String team) 
   { 
      checkTeam(team);
      int i = teams.get(team);
      return r[i];
   } 


   // number of remaining games between team1 and team2
   public  int against(String team1, String team2)
   { 
      checkTeam(team1);
      checkTeam(team2);
      int i = teams.get(team1);
      int j = teams.get(team2);
      return g[i][j];
   } 

   // is given team eliminated?
   public          boolean isEliminated(String team)  
   { 
      checkTeam(team);
      int i = teams.get(team);

      if (!checked[i]) { 
         Elimination(team);
         checked[i] = true;
      } 
      return e[i];
   } 


   // is given team eliminated
   private void Elimination(String team) {
      // check trivial elemination first
      trivialElimination(team);

      // check nontrivial elemination
      // skiped if eliminated from trivial elmination 
      int i = teams.get(team);
      if (N > 2 && !e[i]) {
         nontrivialElimination(team);
      }
   }

   // trivial elimination: a team is out if the maximum possible wins is less than the wins of another team
   private void trivialElimination(String team) {
      int i = teams.get(team);
      Bag<String> res = new Bag<String>();

      for (String t : teams()) {
         if (!t.equals(team) && comparew(team, t) < 0) {
            res.add(t);
         }
      }

      if (res.size() > 0) {
         certificate[i] = res;
         e[i] = true;
      }
   }


   // nontrivial check, using flownetwork and FF algorithm
   private void nontrivialElimination(String team) {
      String[] nodes = buildNode(team);
      buildFlowNetwork(nodes);
      runFF(nodes);
   }

   private void runFF(String[] nodes) {
      int V = 2 + N * (N - 1) / 2;
      FordFulkerson ff = new FordFulkerson(flownetwork, 0, V - 1);
      Bag<String> res = new Bag<String>();

      for (int i = 1; i < N; i++) {
         int vi = (i - 1) * (2*N - i) / 2 + 1;  // index of the team vertice i
         boolean inMincut = ff.inCut(vi);
         if (inMincut) {
            res.add(nodes[i]);
         }
      }
      
      if (res.size() > 0){
         int i = teams.get(nodes[0]);
         e[i] = true;
         certificate[i] = res;
      }
   }

   // subset R of teams that eliminates given team; null if not eliminated
   public Iterable<String> certificateOfElimination(String team)  
   {
      checkTeam(team);
      int i = teams.get(team);
      if (!checked[i]) {
         Elimination(team);
         checked[i] = true;
      }
      return certificate[i];
   }


   // build the nodes of the flow
   private String[] buildNode(String team) {
      String[] nodes = new String[N];
      nodes[0] = team;
      int i = 1;
      for (String t : teams()) {
         if (!t.equals(team)) nodes[i++] = t;
      }
      return nodes;
   }

   // build the flow network
   private void buildFlowNetwork(String[] nodes){
      // only consider the other N-1 teams,
      // so there are (N-1) * (N-2)/2 games,
      // total vertices is (N-1)*(N-2)/2 + (N-1) = (N-1)*N/2 
      // plus the source (s) and drain (t), total V is:

      int V = 2 + N * (N - 1) / 2;
      flownetwork = new FlowNetwork(V);

      String team = nodes[0];
 
      // game vertices first, then team vertices, so:
      // game vertices i-j index is: N-2, N-3 + N-4
      // team vertices index is: N * (N-1)/2 + i
      // s = 0; t = V - 1;

      int v = 1;
      for (int i = 1; i < N; i++) {
         for (int j = i; j < N; j++) {
            if (i == j) {
               // team vertice is : (i-1)*(2*N-i)/2 + 1
               String t = nodes[i];
               flownetwork.addEdge(new FlowEdge(v, V-1, comparew(team, t)));
               v++;
            } else {
               // game vertices
               String t1 = nodes[i];
               String t2 = nodes[j];
               int vi = (i - 1) * (2*N - i) / 2 + 1;  // index of the team vertice i
               int vj = (j - 1) * (2*N - j) / 2 + 1;  // index of the team vertice j
               flownetwork.addEdge(new FlowEdge(0, v, against(t1, t2)));
               flownetwork.addEdge(new FlowEdge(v, vi, Double.POSITIVE_INFINITY));
               flownetwork.addEdge(new FlowEdge(v, vj, Double.POSITIVE_INFINITY));
               v++;
            }
         }
      }
   }

   private int comparew(String t1, String t2) {
       return wins(t1) + remaining(t1) - wins(t2);
   }


   // main method
   public static void main(String[] args) {
       BaseballElimination division = new BaseballElimination(args[0]);

       for (String team : division.teams()) {
           if (division.isEliminated(team)) {
               StdOut.print(team + " is eliminated by the subset R = { ");
               for (String t : division.certificateOfElimination(team)) {
                   StdOut.print(t + " ");
               }
               StdOut.println("}");
           }
           else {
               StdOut.println(team + " is not eliminated");
           }
       }
   }
   //
 
}
