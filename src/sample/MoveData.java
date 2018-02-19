package sample;

public class MoveData {
    private Pair<Integer, Integer> position;
    private int stonesCaptured;

    MoveData(int x, int y, int stonesCaptured) {
        position = new Pair<>(x, y);
        this.stonesCaptured = stonesCaptured;
    }

    MoveData(Pair<Integer, Integer> pair, int stonesCaptured) {
        this.position = pair;
        this.stonesCaptured = stonesCaptured;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof MoveData && (o == this || ((MoveData) o).stonesCaptured == this.stonesCaptured && ((MoveData) o).position.equals(this.position));
    }
}
