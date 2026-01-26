package com.testhub.project.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.testhub.common.exception.BusinessException;
import com.testhub.project.entity.Project;
import com.testhub.project.mapper.ProjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    @Autowired
    private ProjectMapper projectMapper;

    public List<Project> listProjects() {
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Project::getDeleted, 0).orderByDesc(Project::getCreateTime);
        return projectMapper.selectList(wrapper);
    }

    public Project getProjectById(Long id) {
        Project project = projectMapper.selectById(id);
        if (project == null || project.getDeleted() == 1) {
            throw new BusinessException(404, "项目不存在");
        }
        return project;
    }

    public Long createProject(Project project) {
        projectMapper.insert(project);
        return project.getId();
    }

    public void updateProject(Project project) {
        projectMapper.updateById(project);
    }

    public void deleteProject(Long id) {
        Project project = new Project();
        project.setId(id);
        project.setDeleted(1);
        projectMapper.updateById(project);
    }
}
