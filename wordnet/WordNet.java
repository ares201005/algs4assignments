import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.DirectedCycle;

import java.util.HashMap;

public class WordNet {

   private final HashMap<String, Queue<Integer>> nounmap;
   private final HashMap<Integer,Queue<String>>  idmap;

   private final SAP sap;
   private Digraph G;
   private int V;
   private boolean[] NotRoot;   

   // constructor takes the name of the two input files
   public WordNet(String synsets, String hypernyms)
   {
      idmap = new HashMap<Integer,Queue<String>>();
      nounmap = new HashMap<String,Queue<Integer>>();

      readSynsets(synsets);

      G = new Digraph(V);
      NotRoot = new boolean[V];

      readHypernyms(hypernyms);

      sap = new SAP(G);

      checkCycle();

      checkRoot();

   }


   // returns all WordNet nouns
   public Iterable<String> nouns()
   {
      return nounmap.keySet();
   }

   // is the word a WordNet noun?
   public boolean isNoun(String word)
   {
      if (word == null) throw new java.lang.IllegalArgumentException();
      return nounmap.containsKey(word);
   }

   // distance between nounA and nounB (defined below)
   public int distance(String nounA, String nounB)
   {
      if (!isNoun(nounA) || !isNoun(nounB))  throw new IllegalArgumentException("nounA or B is not a noun");
      Iterable<Integer> idA = nounmap.get(nounA);
      Iterable<Integer> idB = nounmap.get(nounB);

      return sap.length(idA,idB);
   }

   // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
   // in a shortest ancestral path (defined below)
   public String sap(String nounA, String nounB)
   {
      if (!isNoun(nounA) || !isNoun(nounB))  throw new IllegalArgumentException("nounA or B is not a noun");
      Iterable<Integer> idA = nounmap.get(nounA);
      Iterable<Integer> idB = nounmap.get(nounB);

      int ancestorID = sap.ancestor(idA,idB);
      Queue<String> result = idmap.get(ancestorID);

      String nouns = "";
      for (String noun : result){
        nouns = nouns + " " + noun;
      }
      return nouns.trim();
   }

   // read in files for synsets
   private void readSynsets(String synsets){
      In SynsetIn = new In(synsets);
      V = 0;

      while (SynsetIn.hasNextLine()){
        String line = SynsetIn.readLine();
        String[] sline = line.split(",");
        Integer id = Integer.parseInt(sline[0]);
        String[] nouns = sline[1].split(" ");
        Queue<String> words = new Queue<String>();

        for (int i = 0; i < nouns.length; i++){
           words.enqueue(nouns[i]);
           Queue<Integer> ids = new Queue<Integer>();
           if (this.nounmap.containsKey(nouns[i]))
              ids = this.nounmap.get(nouns[i]);
           ids.enqueue(id);
           this.nounmap.put(nouns[i],ids);
        }
        this.idmap.put(id,words);

        if (id > this.V) this.V = id;
      }
      this.V = this.V + 1;
   }


   //check whether digraph has cycle
   private void checkCycle(){
       DirectedCycle cycle = new DirectedCycle(G);
       if (cycle.hasCycle())
          throw new IllegalArgumentException("Digraph has cycle!");
   } 

   private void readHypernyms(String hypernyms){
      In HypernymIn = new In(hypernyms);

      while (HypernymIn.hasNextLine()){
         String line = HypernymIn.readLine();
         String[] sline = line.split(",");
         Integer v = Integer.parseInt(sline[0]);

         for (int i = 1; i < sline.length; i++){
            Integer w = Integer.parseInt(sline[i]);
            this.G.addEdge(v,w);
            if (i==1) NotRoot[v] = true;
         }
      }
   }

   // check whether digraph is rooted
   private void checkRoot(){
      int Nroot = 0;
      for ( boolean notroot : NotRoot){
         if (!notroot) Nroot++;
      }

      if (Nroot > 1)  throw new IllegalArgumentException("More than one root is found");
      if (Nroot < 1)  throw new IllegalArgumentException("No root is found");
   }


   // do unit testing of this class
   public static void main(String[] args)
   {
       String fsynset = args[0];
       String fhyper  = args[1];
       WordNet wordnet = new WordNet(fsynset, fhyper);

       StdOut.println("v="+wordnet.V);

       System.out.println(wordnet.distance("Hb", "resid"));
       System.out.println(wordnet.sap("Hb", "resid"));

   }

}


