package faltkullen;

import java.util.ArrayList;

public class MapSector {
    private ArrayList<Unit>[] contains;

    public int x, y;

    public MapSector(int forces) {
        contains = new ArrayList[forces];
        for (int a = 0; a < forces; a++) {
            contains[a] = new ArrayList<Unit>();
        }
    }

    public void addToSector(Unit u) {
        contains[u.team].add(u);
    }

    public void removeFromSector(Unit u) {
        contains[u.team].remove(u);
    }

    public ArrayList<Unit> getUnitsFromTeam(int a) {
        return contains[a];
    }

    public void clear(){
      for(int a=0;a<contains.length;a++){
        contains[a].clear();
      }
    }
}