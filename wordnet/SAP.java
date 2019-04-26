import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class SAP {
   private Digraph G;

   // constructor takes a digraph (not necessarily a DAG)
   public SAP(Digraph G)
   {
      this.G = new Digraph(G);
   }


   // find the shortest path between v and w
   private int[] shortestpath(int v, int w){
      int[] shortest = new int[2];
      DeluxeBFS vBFS = new DeluxeBFS(G,v);
      DeluxeBFS wBFS = new DeluxeBFS(G,w);

      boolean[] vmarked = vBFS.getmarked();
      boolean[] wmarked = wBFS.getmarked();

      int length = Integer.MAX_VALUE;
      int tmpLen = Integer.MAX_VALUE;
      int sAncestor = Integer.MAX_VALUE;

      for (int i=0; i < vmarked.length; i++){
         if (vmarked[i] && wmarked[i]){
            tmpLen = vBFS.distTo(i) + wBFS.distTo(i);
            if (tmpLen < length ){
               length = tmpLen;
               sAncestor = i;
            }
         }
      }

      if (length == Integer.MAX_VALUE) {
         shortest[0] = -1;
         shortest[1] = -1;
         return shortest;
      }

      shortest[0] = length;
      shortest[1] = sAncestor;

      return shortest;
   }

   // find the shortest path between two sets of vertices
   private int[] shortestpath(Iterable<Integer> v, Iterable<Integer> w){
      int[] shortest = new int[2];
      DeluxeBFS vBFS = new DeluxeBFS(G,v);
      DeluxeBFS wBFS = new DeluxeBFS(G,w);

      boolean[] vmarked = vBFS.getmarked();
      boolean[] wmarked = wBFS.getmarked();

      int length = Integer.MAX_VALUE;
      int tmpLen = Integer.MAX_VALUE;
      int sAncestor = Integer.MAX_VALUE;

      for (int i=0; i < vmarked.length; i++){
         if (vmarked[i] && wmarked[i]){
            tmpLen = vBFS.distTo(i) + wBFS.distTo(i);
            if (tmpLen < length ){
               length = tmpLen;
               sAncestor = i;
            }
         }
      }

      if (length == Integer.MAX_VALUE) {
         shortest[0] = -1;
         shortest[1] = -1;
         return shortest;
      }

      shortest[0] = length;
      shortest[1] = sAncestor;

      return shortest;
   }


   // length of shortest ancestral path between v and w; -1 if no such path
   public int length(int v, int w)
   {
      validate(v);
      validate(w);

      int[] result = shortestpath(v,w);
      return result[0];
   }

   // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
   public int ancestor(int v, int w)
   {
      validate(v);
      validate(w);

      int[] result = shortestpath(v,w);
      return result[1];
   }

   // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
   public int length(Iterable<Integer> v, Iterable<Integer> w)
   {
      validate(v);
      validate(w);

      int[] result = shortestpath(v,w);
      return result[0];
   }

   // a common ancestor that participates in shortest ancestral path; -1 if no such path
   public int ancestor(Iterable<Integer> v, Iterable<Integer> w)
   {
      validate(v);
      validate(w);

      int[] result = shortestpath(v,w);
      return result[1];
   }

   private void validate(int v){
     if (v < 0 || v > G.V() - 1) throw new java.lang.IllegalArgumentException();
   }

   private void validate(Iterable<Integer> v){
      if (v == null) throw new java.lang.IllegalArgumentException();
      for (Integer i : v){
        if (i == null) throw new java.lang.IllegalArgumentException();
        validate(i);
      }
   }

   // do unit testing of this class
   public static void main(String[] args)
   {
       In in = new In(args[0]);
       Digraph G = new Digraph(in);
       SAP sap = new SAP(G);
       while (!StdIn.isEmpty()) {
           int v = StdIn.readInt();
           int w = StdIn.readInt();
           int length   = sap.length(v, w);
           int ancestor = sap.ancestor(v, w);
           StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
       }
   }

}
