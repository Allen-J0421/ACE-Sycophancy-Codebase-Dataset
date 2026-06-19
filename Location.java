
public class Location {

	private final int row;
	private final int col;


	public Location(int row, int col) {
		this.row = row;
		this.col = col;
	}


	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Location) {
			Location other = (Location) obj;
			return row == other.getRow() && col == other.getCol();
		} else {
			return false;
		}
	}


	@Override
	public String toString() {
		return row + "," + col;
	}


	@Override
	public int hashCode() {
		return (row << 16) + col;
	}


	public int getRow() {
		return row;
	}


	public int getCol() {
		return col;
	}
}
