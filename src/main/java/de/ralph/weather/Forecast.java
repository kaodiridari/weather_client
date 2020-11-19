package de.ralph.weather;

/**
 * Holds a set of weather forecast data.
 * 
 * @author user
 *
 */
public class Forecast {
	
	private String name = "";
	private String zipcode = "";

	private String temperature = "";
	private String wind  = "";
	private String clouds  = "";
	private String time = "";
	
	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	private long id = -1;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	
	public String getTemperature() {
		return temperature;
	}
	
	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}
	
	public String getWind() {
		return wind;
	}
	
	public void setWind(String wind) {
		this.wind = wind;
	}
	
	public String getClouds() {
		return clouds;
	}
	
	public void setClouds(String clouds) {
		this.clouds = clouds;
	}	
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Forecast [name=" + name + ", zipcode=" + zipcode + ", temperature=" + temperature + ", wind=" + wind + ", clouds=" + clouds + ", time=" + time
				+ ", id=" + id + "]";
	}

	
}
