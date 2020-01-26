package it.bnl.devops.sonar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import com.google.gson.Gson;

import it.bnl.devops.sonar.dto.ProjectsSearch;

public class Projects {

	private String serverUrl;
	public Projects(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	public List<Project> search() throws IOException {
		
		Gson gson = new Gson();
		
		List<Project> projects = new ArrayList<Project>();
		
		int p = 1;
		int ps = 500;
		int total = 501;//per fargli fare la prima chiamata
		while( p*ps < total) {
			String auth = "admin:admin";
			byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
			String authHeaderValue = "Basic " + new String(encodedAuth);
			
			URL url = new URL(serverUrl + "/api/projects/search?ps=500&p="+p);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Authorization", authHeaderValue);
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
			
			ProjectsSearch projectsSearch = gson.fromJson(buffer.toString(), ProjectsSearch.class);
			p = projectsSearch.paging.pageIndex;
			ps = projectsSearch.paging.pageSize;
			total = projectsSearch.paging.total;
			
			projects.addAll(projectsSearch.components);
			
		}
		
		return projects;
	}

}
