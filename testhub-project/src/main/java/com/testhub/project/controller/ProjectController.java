package com.testhub.project.controller;

import com.testhub.common.dto.Result;
import com.testhub.project.entity.Project;
import com.testhub.project.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @GetMapping("/projects")
    public Result<List<Project>> listProjects() {
        List<Project> projects = projectService.listProjects();
        return Result.success(projects);
    }

    @GetMapping("/projects/{id}")
    public Result<Project> getProject(@PathVariable Long id) {
        Project project = projectService.getProjectById(id);
        return Result.success(project);
    }

    @PostMapping("/projects")
    public Result<Long> createProject(@RequestBody Project project) {
        Long id = projectService.createProject(project);
        return Result.success("创建成功", id);
    }

    @PutMapping("/projects/{id}")
    public Result<String> updateProject(@PathVariable Long id, @RequestBody Project project) {
        project.setId(id);
        projectService.updateProject(project);
        return Result.success("更新成功");
    }

    @DeleteMapping("/projects/{id}")
    public Result<String> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return Result.success("删除成功");
    }
}
