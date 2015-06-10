



/**
 * A simple java class to represent a message that is to put inside the queue
 * @author Joker
 *
 */
public class QueueMessage {
	private Integer id;
	private String name;
	private String abbreviation;
	private String country;
	
	/**
	 * QueueMessage Constructor
	 * @param id
	 * @param name
	 * @param abbreviation
	 * @param country
	 */
	public QueueMessage(Integer id,String name,String abbreviation,String country){
		this.id = id;
		this.name = name;
		this.abbreviation = abbreviation;
		this.country = country;
	}
	public QueueMessage(){
		this.id = 0;
		this.name = null;
		this.abbreviation = null;
		this.country = null;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAbbreviation() {
		return abbreviation;
	}
	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	
	public String toString(){
		return "State [id="+id+", name="+ name + ", abbr="+ abbreviation + ", country="+country+"]";
	}
	
	
}
