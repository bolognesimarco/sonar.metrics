package it.bnl.devops.sonar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import com.google.gson.Gson;

import it.bnl.devops.sonar.dto.MeasuresComponent;

public class Measures {

	private String serverUrl;
	public Measures(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	public Component component(String key, List<String> metricKeys) throws IOException {
		URL url = new URL(serverUrl + "/api/measures/component?component="+key+"&metricKeys=ncloc_language_distribution");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");
		if (conn.getResponseCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));

		String output;
		StringBuffer buffer = new StringBuffer();
		while ((output = br.readLine()) != null) {
			buffer.append(output);
		}
		conn.disconnect();
		
		Gson gson = new Gson();
		MeasuresComponent measuresComponent = gson.fromJson(buffer.toString(), MeasuresComponent.class);
		return measuresComponent.component;
	}

}
