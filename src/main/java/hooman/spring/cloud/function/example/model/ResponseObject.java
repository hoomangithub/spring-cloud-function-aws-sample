package hooman.spring.cloud.function.example.model;

public class ResponseObject {

	private  int id;
	private String name;

    public ResponseObject() {}

    public ResponseObject(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "=> ResponseObject{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
