package entities;

public class Card extends BaseEntity {

    private String name;

    private int attack;

    private int defense;

    public Card() {
        super();
    }

    public Card(String name, int attack, int defense) {
        super();
        this.name = name;
        this.attack = attack;
        this.defense = defense;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public int getPower() {
        return (int) ((2 * getAttack() + 1.5 * getDefense()) / 3);
    }
}
