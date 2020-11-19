package de.ralph.weather;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriBuilderFactory;

@Controller
public class WeatherController {

	private static final Logger logger = LoggerFactory.getLogger(WeatherController.class);
	
	public final static String RESOURCE_SERVER = "http://localhost:8081/sso-resource-server";
	
	public final int[] knownPlaces = new int[]{15000, 26000, 37000, 43000, 48000, 55000, 64000, 78000, 80000, 90000}; 
	
    //@Value("${resourceserver.api.url}")
    private String fooApiUrl = RESOURCE_SERVER + "/api/foos/zipcode/D-37000";

	@Value("${resourceserver.api.url}")
	private String apiUrl;
    
    @Autowired
    private WebClient webClient;
	
	@GetMapping("/weather")
	public String getWeather(Model model) {
		logger.info("getWeather()");
		model.addAttribute("city", new City());
		return "weather";
	}
	
	/**
	 * User has entered a city.
	 * 
	 * @param city The name or zip-code.
	 * @param model The Spring MVC model.
	 * @return link to the page, which displays the weather forcast.
	 */
	@PostMapping("/weather")
	public String weatherSubmit(@ModelAttribute City city, Model model) {
		logger.info("submitCity(): " + city);
		model.addAttribute("city", city);
		
		//Find "nearest" - city		
		String usersZip = org.apache.commons.lang3.StringUtils.substringAfter(city.getZipcode(), "-");
		String prefix = org.apache.commons.lang3.StringUtils.substringBefore(city.getZipcode(), "-");
		int usersZipAsInt = Integer.parseInt(usersZip);
		int nearestKnownPlace = -1;
		int distance = (int)1e7;
		for (int knownPlace : knownPlaces) {
			int d = Math.abs(usersZipAsInt - knownPlace);
			if (d < distance) {
				distance = d;
				nearestKnownPlace = knownPlace;
				if (d == 0) {
					break;  //save some time
				}
			}
		}
		logger.info("nearestKnownPlace: " + nearestKnownPlace + " distance: " + distance);		
		
		//uribuilder-> does not work????
		String zipcode = prefix + "-" + String.valueOf(nearestKnownPlace);
		logger.info("zipcode: " + zipcode);
		try {
			zipcode = URLEncoder.encode(zipcode, StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException e) {
			logger.error("Can't encode this: " + city.getZipcode(), e);
			return "";
		}
		String uri = StringUtils.trimTrailingWhitespace(apiUrl);
		uri = StringUtils.trimLeadingWhitespace(uri);
		uri = StringUtils.trimTrailingCharacter(uri, '/');
		uri = uri + "/zipcode/" + zipcode;
		logger.info("Calling: " + uri);
		
		List<Forecast> forcasts = this.webClient.get().uri(uri).retrieve()
				.bodyToMono(new ParameterizedTypeReference<List<Forecast>>() {})
				.block();
		
		if (forcasts != null && forcasts.size() != 0) {
			logger.info("forcasts: " + forcasts.size());
			logger.info("first: " + forcasts.get(0));
		} else {
			logger.warn("Nothing returned.");
		}
		
		model.addAttribute("forecasts", forcasts);
		
		return "result";
	}
}
