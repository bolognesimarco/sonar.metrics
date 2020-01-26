package it.bnl.devops.sonar.dto;

import java.util.List;

import it.bnl.devops.sonar.Project;

public class ProjectsSearch {
	public Paging paging;
	public List<Project> components;
}
