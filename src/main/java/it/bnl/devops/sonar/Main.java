package it.bnl.devops.sonar;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
	
	private static final String oldSonarUrl = "http://localhost:9000"; //"http://10.237.11.10:9000";//?
	private static final String newSonarUrl = "http://localhost:9000";//?

	public static void main(String[] args) throws IOException {
		

		Map<String, Map<String, Integer>> nclocByLangByProjectOldSonar = new HashMap<String, Map<String,Integer>>();
		
		
		Sonar oldsonar = new Sonar(oldSonarUrl);
		List<Project> oldprojects = oldsonar.projects().search();//"TRK" (project) è il default
		for(Project project : oldprojects) {
			Component component = oldsonar.measures().component(project.key, Arrays.asList("ncloc_language_distribution "));
			String[] metricValuePerLanguage = component.measures.get(0).value.split(";");
			for(String langNclocPair : metricValuePerLanguage) {
				String[] pair = langNclocPair.split("=");
				String projectLang = pair[0];
				Integer projectLangNcloc = Integer.valueOf(pair[1]);
//				String ss = project.name.substring(0, project.name.indexOf("-"));// OPPURE project.key?
				
				Map<String, Integer> nclocByLang = nclocByLangByProjectOldSonar.get(project.key);
				if(nclocByLang == null) {
					nclocByLang = new HashMap<String, Integer>();
					nclocByLangByProjectOldSonar.put(project.key, nclocByLang);
				}
				
				Integer ncloc = nclocByLang.get(projectLang);
				if(ncloc == null) {
					ncloc = projectLangNcloc;
					nclocByLang.put(projectLang, ncloc);
				} else {
					ncloc += projectLangNcloc;
					//non so se essendo un integer devo rifare: 
					//langNcloc.put(projectLang, ncloc);
				}
			}
			
		}
		
		
		
		Map<String, Map<String, Integer>> nclocByLangByProjectNewSonar = new HashMap<String, Map<String,Integer>>();
		
		Sonar newsonar = new Sonar(newSonarUrl);
		List<Project> newprojects = newsonar.projects().search();//"TRK" (project) è il default
		for(Project project : newprojects) {
			Component component = newsonar.measures().component(project.key, Arrays.asList("ncloc_language_distribution "));
			String[] metricValuePerLanguage = component.measures.get(0).value.split(";");
			for(String langNclocPair : metricValuePerLanguage) {
				String[] pair = langNclocPair.split("=");
				String projectLang = pair[0];
				Integer projectLangNcloc = Integer.valueOf(pair[1]);
//				String ss = project.name.substring(0, project.name.indexOf("-"));// OPPURE project.key?
				
				Map<String, Integer> nclocByLang = nclocByLangByProjectNewSonar.get(project.key);
				if(nclocByLang == null) {
					nclocByLang = new HashMap<String, Integer>();
					nclocByLangByProjectNewSonar.put(project.key, nclocByLang);
				}
				
				Integer ncloc = nclocByLang.get(projectLang);
				if(ncloc == null) {
					ncloc = projectLangNcloc;
					nclocByLang.put(projectLang, ncloc);
				} else {
					ncloc += projectLangNcloc;
					//non so se essendo un integer devo rifare: 
					//langNcloc.put(projectLang, ncloc);
				}
			}
			
		}
		
		
		
		
		
		Map<String, Map<String, Integer>> nclocByLangByProjectSonar = new HashMap<String, Map<String,Integer>>();
		nclocByLangByProjectSonar.putAll(nclocByLangByProjectOldSonar);
		nclocByLangByProjectSonar.putAll(nclocByLangByProjectNewSonar);
		
		
		//fin qui ho la lista dei progetti, eliminati i doppioni a favore della nuova piattaforma, con le ncloc per linguaggio		
		nclocByLangByProjectSonar.forEach((projKey, nlocByLang) -> {System.out.println(projKey);nlocByLang.forEach((lang, nloc) -> {System.out.println(lang+":"+nloc);});});
		
		
		//faccio un'altra mappa di mappe, non per progetto ma per SS 
		Map<String, Map<String, Integer>> nclocByLangBySsSonar = new HashMap<String, Map<String,Integer>>();
		
		nclocByLangByProjectSonar.forEach((projKey, nclocByLangOfProject) -> {
			String ss = projKey.substring(0, projKey.indexOf(":"));
			if(nclocByLangBySsSonar.containsKey(ss)) {
				Map<String, Integer> nclocByLangofSs = nclocByLangBySsSonar.get(ss);
				nclocByLangOfProject.forEach((lang, nclocofproject)-> {
					if(nclocByLangofSs.containsKey(lang)) {
						Integer nclocTemp = nclocByLangofSs.get(lang);
						nclocTemp += nclocofproject;
						nclocByLangofSs.put(lang, nclocTemp);
					} else {
						nclocByLangofSs.put(lang, nclocofproject);
					}
				});
			} else {
				nclocByLangBySsSonar.put(ss, nclocByLangOfProject);
			}
		});
		
		nclocByLangBySsSonar.forEach((ssKey, nlocByLang) -> {System.out.println(ssKey);nlocByLang.forEach((lang, nloc) -> {System.out.println(lang+":"+nloc);});});

		
		System.out.println("mah...");
		
	}

}