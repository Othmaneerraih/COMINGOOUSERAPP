package comingoo.vone.tahae.comingoodriver;

public class Car {
    String name, description, selected, id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Car() {

    }

    public Car(String name, String description, String selected, String id) {

        this.name = name;
        this.description = description;
        this.selected = selected;
        this.id = id;
    }
}
