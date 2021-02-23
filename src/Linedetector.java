
public class Linedetector
{
    boolean[][] board;
    public Linedetector(boolean[][] newBoard){
        board = newBoard;
        
    }
    public boolean[] fullLines() {
        boolean full = true;
        int w = 0;
        boolean[] lines = new boolean[20];
        for(int h=0;h<20;h++){
            while(full && w<10) {
                full = board[h][w];
                w++;    
            }
            w=0;
            lines[h] = full;
        }
        return lines;
    }
}
