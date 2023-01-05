public class Ability {
    private String name;
    private String description;
    private Image smallImage;
    private Image bigImage;

    public Ability(String name, String description, Image smallImage, Image bigImage){
        this.name = name;
        this.description = description;
        this.smallImage = smallImage;
        this.bigImage = bigImage;
    }
}
