package it.bnl.devops.sonar;

import java.io.IOException;

public class Sonar {

	private String serverUrl;
	public Sonar(String serverUrl) throws IOException {
		this.serverUrl = serverUrl;
	}
	public Projects projects() {
		return new Projects(serverUrl);
	}
	public Measures measures() {
		// TODO Auto-generated method stub
		return new Measures(serverUrl);
	}

}
