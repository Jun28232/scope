package com.aiteam.orchestrator.controller;

import com.aiteam.orchestrator.*;
import com.aiteam.orchestrator.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 项目控制器 - 实现AgentCorp SaaS平台的核心API
 */
@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "*")
public class ProjectController {

    @Autowired
    private Dispatcher dispatcher;

    @Autowired
    private AgentOrchestrator orchestrator;

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private ProjectStateRepository stateRepository;

    /**
     * 创建新项目 (对应设计图页面1: 组织架构管理)
     */
    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@RequestBody CreateProjectRequest request) {
        try {
            // 1. 使用Dispatcher拆解自然语言任务
            ProjectPlan projectPlan = dispatcher.decomposeTask(request.getDescription());

            // 2. 创建项目响应
            ProjectResponse response = new ProjectResponse();
            response.setProjectId(projectPlan.getProjectId());
            response.setTitle(projectPlan.getTitle());
            response.setDescription(projectPlan.getDescription());
            response.setStatus("CREATED");
            response.setCreatedAt(projectPlan.getCreatedAt());

            // 3. 初始化项目状态
            ProjectState state = new ProjectState(projectPlan.getProjectId());
            projectPlan.getTasks().forEach(state::addTask);
            stateRepository.save(state);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ProjectResponse("ERROR", "Failed to create project", null, null)
            );
        }
    }

    /**
     * 获取项目列表 (组织架构管理)
     */
    @GetMapping
    public ResponseEntity<List<ProjectSummaryResponse>> getProjects() {
        // TODO: 从数据库获取项目列表
        return ResponseEntity.ok(java.util.Collections.emptyList());
    }

    /**
     * 获取项目详情 (看板视图)
     */
    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDetailResponse> getProject(@PathVariable String projectId) {
        try {
            ProjectState state = stateRepository.load(projectId);
            if (state == null) {
                return ResponseEntity.notFound().build();
            }

            ProjectDetailResponse response = new ProjectDetailResponse();
            response.setProjectId(state.getProjectId());
            response.setStatus(state.getOverallStatus().toString());
            response.setLastUpdated(state.getLastUpdated());

            // 转换为看板格式
            response.setKanbanColumns(generateKanbanColumns(state));
            response.setTasks(state.getTaskMap().values().stream().toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 执行项目 (页面3: 项目协作看板)
     */
    @PostMapping("/{projectId}/execute")
    public ResponseEntity<ExecutionResponse> executeProject(@PathVariable String projectId) {
        try {
            ProjectState state = stateRepository.load(projectId);
            if (state == null) {
                return ResponseEntity.notFound().build();
            }

            ProjectPlan projectPlan = new ProjectPlan(
                state.getProjectId(),
                "Executing Project",
                "Running execution",
                state.getTaskMap().values().stream().toList(),
                java.time.LocalDateTime.now(),
                java.time.LocalDateTime.now()
            );

            // 异步执行项目
            orchestrator.executeProject(projectPlan);

            ExecutionResponse response = new ExecutionResponse();
            response.setProjectId(projectId);
            response.setStatus("EXECUTING");
            response.setMessage("Project execution started");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ExecutionResponse(projectId, "ERROR", e.getMessage())
            );
        }
    }

    /**
     * 断点续传
     */
    @PostMapping("/{projectId}/resume")
    public ResponseEntity<ExecutionResponse> resumeProject(@PathVariable String projectId) {
        try {
            orchestrator.resumeExecution(projectId);

            ExecutionResponse response = new ExecutionResponse();
            response.setProjectId(projectId);
            response.setStatus("RESUMED");
            response.setMessage("Project resumed from checkpoint");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new ExecutionResponse(projectId, "ERROR", e.getMessage())
            );
        }
    }

    /**
     * 获取项目状态 (实时更新)
     */
    @GetMapping("/{projectId}/status")
    public ResponseEntity<ProjectStatusResponse> getProjectStatus(@PathVariable String projectId) {
        try {
            ProjectState state = stateRepository.load(projectId);
            if (state == null) {
                return ResponseEntity.notFound().build();
            }

            ProjectStatusResponse response = new ProjectStatusResponse();
            response.setProjectId(state.getProjectId());
            response.setOverallStatus(state.getOverallStatus().toString());
            response.setTaskCount(state.getTaskMap().size());
            response.setCompletedTasks(state.getTaskMap().values().stream()
                .mapToInt(task -> task.getStatus() == Task.TaskStatus.COMPLETED ? 1 : 0)
                .sum());
            response.setFailedTasks(state.getTaskMap().values().stream()
                .mapToInt(task -> task.getStatus() == Task.TaskStatus.FAILED ? 1 : 0)
                .sum());
            response.setRunningTasks(state.getTaskMap().values().stream()
                .mapToInt(task -> task.getStatus() == Task.TaskStatus.RUNNING ? 1 : 0)
                .sum());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 生成看板列数据
     */
    private List<KanbanColumn> generateKanbanColumns(ProjectState state) {
        return java.util.Arrays.asList(
            new KanbanColumn("待处理", "pending",
                state.getTaskMap().values().stream()
                    .filter(task -> task.getStatus() == Task.TaskStatus.PENDING)
                    .toList()),
            new KanbanColumn("需求分析中", "analyzing",
                state.getTaskMap().values().stream()
                    .filter(task -> task.getStatus() == Task.TaskStatus.RUNNING)
                    .toList()),
            new KanbanColumn("开发中", "developing",
                java.util.Collections.emptyList()), // TODO: 根据实际状态过滤
            new KanbanColumn("测试中", "testing",
                java.util.Collections.emptyList()), // TODO: 根据实际状态过滤
            new KanbanColumn("已完成", "completed",
                state.getTaskMap().values().stream()
                    .filter(task -> task.getStatus() == Task.TaskStatus.COMPLETED)
                    .toList())
        );
    }
}