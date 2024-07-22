package hawaiiappbuilders.omniversapp.model;

public class SideDish {

    private int _ID;
    private String _SideName;
    private double _priceBump;
    private boolean _isSelected;

    public int get_ID() {
        return _ID;
    }

    public void set_ID(int _ID) {
        this._ID = _ID;
    }

    public String get_SideName() {
        return _SideName;
    }

    public void set_SideName(String _SideName) {
        this._SideName = _SideName;
    }

    public double get_priceBump() {
        return _priceBump;
    }

    public void set_priceBump(double _priceBump) {
        this._priceBump = _priceBump;
    }

    public boolean is_isSelected() {
        return _isSelected;
    }

    public void set_isSelected(boolean _isSelected) {
        this._isSelected = _isSelected;
    }

    public SideDish(int _ID, String _SideName, double _priceBump) {
        this._ID = _ID;
        this._SideName = _SideName;
        this._priceBump = _priceBump;
    }
}
