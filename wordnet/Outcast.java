import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
   private WordNet wordnet;
   public Outcast(WordNet wordnet)         // constructor takes a WordNet object
   {
      this.wordnet = wordnet;
   }


   public String outcast(String[] nouns)   // given an array of WordNet nouns, return an outcast
   {
     int[] dist = new int[nouns.length];
     int d = 0;

     for (int i = 0; i < nouns.length; i++){
        for (int j = i; j < nouns.length; j++){
           int distance = wordnet.distance(nouns[j],nouns[i]);
           //StdOut.println("i,j,dist = "+nouns[j]+" "+nouns[i]+" "+distance);
           dist[i]  += distance;
           dist[j]  += distance;
        }
        //StdOut.println("i, dist[i] = "+i+" "+dist[i]);
     }

     int maxdist = 0;
     int id = 0;
     for (int i = 0; i < nouns.length; i++){
        if (dist[i] > maxdist) {
          maxdist = dist[i];
          id = i;
        }
     }

     return nouns[id];
   }

   public static void main(String[] args)  // see test client below
   {
      WordNet wordnet = new WordNet(args[0], args[1]);
      Outcast outcast = new Outcast(wordnet);
      for (int t = 2; t < args.length; t++) {
          In in = new In(args[t]);
          String[] nouns = in.readAllStrings();
          StdOut.println(args[t] + ": " + outcast.outcast(nouns));
      }
   }
}
