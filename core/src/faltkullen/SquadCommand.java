package faltkullen;

public class SquadCommand {

    Position target;
    Position enemyDirection;
    int state;
    int formationCounter;//ska innehålla en målposition
    double unknown_speed;
    double known_speed;
    int angle;

    //ska innehålla 1/3 olika aktivitetsgrader
    public SquadCommand(int s, int x, int y) {
        state = s;
        target = new Position(x, y);
        formationCounter = -1;
        unknown_speed = 1;
        known_speed = 1;
        enemyDirection = new Position(-1, -1);
        angle = -1;
    }

    public SquadCommand(int a, Position p) {
        state = a;
        target = new Position(p.x, p.y);
        formationCounter = -1;
        unknown_speed = 1;
        known_speed = 1;
        enemyDirection = new Position(-1, -1);
        angle = -1;
    }

    public SquadCommand(int a, Position p, int c) {
        state = a;
        target = new Position(p.x, p.y);
        formationCounter = c;
        unknown_speed = 1;
        known_speed = 1;
        enemyDirection = new Position(-1, -1);
        angle = -1;
    }

    public SquadCommand(int state, Position p, int counter, double unSpeed, double knSpeed) {
        this.state = state;
        target = new Position(p.x, p.y);
        this.formationCounter = counter;
        unknown_speed = unSpeed;
        known_speed = knSpeed;
        enemyDirection = new Position(-1, -1);
        angle = -1;
    }

    public SquadCommand(int state, Position target, Position enemy, int angle) {
        this.state = state;
        this.target = new Position(target.x, target.y);
        formationCounter = 0;
        unknown_speed = 1;
        known_speed = 1;
        enemyDirection = enemy;
        this.angle = angle;
    }
}
