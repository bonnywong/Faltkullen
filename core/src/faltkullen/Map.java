package faltkullen;

public class Map {
    public MapSector[][] sectors;
    public int xLimit;
    public int yLimit;

    public Map(int xSize, int ySize, int forces) {
        xLimit = (xSize / 50) + 1;
        yLimit = (ySize / 50) + 1;
        sectors = new MapSector[xLimit][yLimit];
        for (int a = 0; a < xLimit; a++) {
            for (int b = 0; b < yLimit; b++) {
                sectors[a][b] = new MapSector(forces);
                sectors[a][b].x = a;
                sectors[a][b].y = b;
            }
        }
    }
  
    public void clear(){
      for(int a=0;a<xLimit;a++){
        for(int b=0;b<yLimit;b++){
          sectors[a][b].clear();
        }
      }
    }
}