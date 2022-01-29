package hooman.spring.cloud.function.example.model;

public class RequestObject {

	private int id;
	private String name;
	private String nachname;

	public RequestObject() {}

	public RequestObject(int id, String name, String nachname) {
		this.id = id;
		this.name = name;
		this.nachname = nachname;
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
	public String getNachname() {
		return nachname;
	}
	public void setNachname(String nachname) {
		this.nachname = nachname;
	}

	@Override
	public String toString() {
		return "=> RequestObject {" +
				"id=" + id +
				", name='" + name + '\'' +
				", nachname='" + nachname + '\'' +
				'}';
	}
}
