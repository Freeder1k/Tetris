import java.util.Random;
public class Blockgenerator
{
    int [][][] coordinaten = new int [][][]{
        {{-1, 1}, {-1, 0}, {0, 0}, {0, -1}}, //lightning
        {{1, 1}, {1, 0}, {0, 0}, {0, -1}}, // reverse lightning
        {{1, 1}, {1, 0}, {0, 0}, {0, 1}}, // cube
        {{1, 1}, {1, 0}, {0, 0}, {-1, 0}}, //l-shape
        {{1, 1}, {1, 0}, {0, 0}, {-1, 0}}, //J-shape
        {{-1, 0}, {1, 0}, {0, 0}, {2, 0}}, // I
    };
    int seed;
    int n = 1;
    public Blockgenerator (int seed){
        this.seed = seed;
    }
    
    public int newBlock () {
        int block;
        Random gen = new Random(seed*n);
        block = gen.nextInt(6);
        n++;
        return block; 
    }
}
    
