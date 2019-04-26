import edu.princeton.cs.algs4.Picture;
import java.awt.Color;

public class SeamCarver {
   private int width;
   private int height;
   private int[][] rgb;

   public SeamCarver(Picture picture)                // create a seam carver object based on the given picture
   {
      if (picture == null) throw new NullPointerException();
      width = picture.width();
      height = picture.height();

      //System.out.println("width in input="+width);
      
      rgb = new int[width][height];

      for (int col = 0; col < width; col++){
         for (int row = 0; row < height; row++){
            rgb[col][row] = picture.get(col,row).getRGB();
         }
      }
   }

   public Picture picture()                          // current picture
   {
      Picture p = new Picture(width, height);
      //System.out.println("width="+width);

      for (int col = 0; col < width; col++){
         for (int row = 0; row < height; row++){
            p.set(col,row,new Color(rgb[col][row]));
         }
      }

      return p;
   }

   public     int width()                            // width of current picture
   { 
      return width; 
   }

   public     int height()                           // height of current picture
   { 
      return height; 
   }

   public  double energy(int x, int y)               // energy of pixel at column x and row y
   { 
      validateX(x);
      validateY(y);
      if ( x == 0 || x == width -1)
          return 1000;
      if ( y == 0 || y == height -1)
          return 1000;
      double deltax = DeltaX(x, y);
      double deltay = DeltaY(x, y);
      return Math.sqrt(deltax + deltay);
   }

   private double DeltaX(int x, int y)
   { 
      validateX(x);
      validateY(y);
      //if ( x == 0 || x == width -1)
      //    return 1000;
      //if ( y == 0 || y == height -1)
      //    return 1000;

      int dred   = red(x+1,y)   - red(x-1,y);
      int dgreen = green(x+1,y) - green(x-1,y);
      int dblue  = blue(x+1,y)  - blue(x-1,y);
      return dred*dred + dgreen*dgreen + dblue*dblue;
   }

   private double DeltaY(int x, int y)
   {
      validateX(x);
      validateY(y);
      //if ( x == 0 || x == width -1)
      //    return 1000;
      //if ( y == 0 || y == height -1)
      //    return 1000;
      //    return 1000;

      int dred   = red(x,y+1)   - red(x,y-1);
      int dgreen = green(x,y+1) - green(x,y-1);
      int dblue  = blue(x,y+1)  - blue(x,y-1);

      return dred*dred + dgreen*dgreen + dblue*dblue;
   }

   public   int[] findHorizontalSeam()               // sequence of indices for horizontal seam
   { 
      this.rgb = transpose(this.rgb);
      int[] seam = findVerticalSeam();
      this.rgb = transpose(this.rgb);
      return seam;
   }

   public   int[] findVerticalSeam()                 // sequence of indices for vertical seam
   {
      int V = width * height;
      int[] nodeTo = new int[V];
      int[] seam = new int[height];
      double[] dist = new double[V];

      for (int i = 0; i < V; i++){
         if (i < width)
            dist[i] = 0;
         else
            dist[i] = Double.POSITIVE_INFINITY;
      }

      // find shortest path
      for (int i = 0; i < height-1; i++){
         for (int j = 0; j < width; j++){
            for (int k = j-1; k < j+2; k++){
               if (k < 0 || k > width-1) 
                   continue;
               else
                   relax(k, j, i, dist, nodeTo);
            }
         }
      }

      // find the Shortest distance

      double mindist = Double.POSITIVE_INFINITY;
      int    minnode = -1;

      for (int i = 0; i < width; i++){
         int node = index(i,height-1);
         if (dist[node] < mindist){
            mindist = dist[node];
            minnode = node;
         }
      }

      //StdOut.println("min energy is at node ="+minnode);
      // find the shorest path;
      for (int i = 0; i < height; i++){
         int row = height - 1 - i;
         int col = minnode - row * width;
         seam[row] = col;
         minnode = nodeTo[minnode]; 
      }
      return seam;
   }

   private void relax(int newcol, int col, int row, double[] dist, int[] nodeTo)
   { 
       if (dist[index(newcol,row+1)] > dist[index(col,row)] + energy(col,row))
       { 
           dist[index(newcol,row+1)] = dist[index(col,row)] + energy(col,row);
           nodeTo[index(newcol,row+1)] = index(col,row);
       }
   }


   private int index(int col, int row)
   { 
       return row * width + col;
   }

   private int[][] transpose(int[][] origin)
   {
       if (origin == null) throw new IllegalArgumentException();
       int[][] result = new int[height][width];
       for (int i = 0; i < width; i++){
          for (int j = 0; j < height; j++){
              result[j][i] = origin[i][j];
          }
       }
       int tmp = this.width;
       this.width = this.height;
       this.height = tmp;
       return result;
   }

   public    void removeHorizontalSeam(int[] seam)   // remove horizontal seam from current picture
   { 
      this.rgb = transpose(this.rgb);
      removeVerticalSeam(seam);
      this.rgb = transpose(this.rgb);
   }

   public    void removeVerticalSeam(int[] seam)     // remove vertical seam from current picture
   {
      if (seam == null) throw new IllegalArgumentException();
      if (seam.length != height) throw new IllegalArgumentException();

      for (int i = 0; i < seam.length; i++){
         if (seam[i] < 0 || seam[i] > width -1)
            throw new IllegalArgumentException();
         if (i < seam.length - 1 && Math.abs(seam[i] - seam[i+1]) > 1) 
            throw new IllegalArgumentException();
      }

      //System.out.println("width before removeVertical="+width);

      int[][] newrgb = new int[width-1][height];
      for (int j = 0; j < height; j++){
         for (int i = 0; i < width-1; i++){
            if (i < seam[j])
              newrgb[i][j] = this.rgb[i][j];
            else
             newrgb[i][j] = this.rgb[i+1][j];
         }
      }

      this.width--;
      this.rgb = newrgb;
      
      //System.out.println("this.width="+this.width);
   }

   private void validateX(int x)
   { 
      if ( x < 0 || x > width - 1) throw new java.lang.IllegalArgumentException();
   }

   private void validateY(int y)
   { 
      if ( y < 0 || y > height - 1) throw new java.lang.IllegalArgumentException();
   }

   private int red(int col, int row)
   {
      return ( rgb[col][row] >> 16) & 0xFF;
   }

   private int green(int col, int row)
   {
      return ( rgb[col][row] >> 8) & 0xFF;
   }

   private int blue(int col, int row)
   {
      return ( rgb[col][row] >> 0) & 0xFF;
   }

}
