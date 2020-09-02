package entities;

public class DuelistField extends BaseEntity {

    private Duelist duelist;

    private DuelCard[][] field;

    public DuelistField(Duelist duelist) {
        super();
        this.duelist = duelist;
        this.field = new DuelCard[2][5];
    }

    public Duelist getDuelist() {
        return duelist;
    }

    public DuelCard[][] getField() {
        return field;
    }
}
