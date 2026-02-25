package com.aiteam.orchestrator.websocket;

import com.aiteam.orchestrator.ProjectState;
import com.aiteam.orchestrator.ProjectStateRepository;
import com.aiteam.orchestrator.dto.ProjectStatusResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * WebSocket服务 - 实时推送项目状态更新
 * (对应设计图的实时状态更新功能)
 */
@Component
@ServerEndpoint("/ws/project/{projectId}")
public class ProjectWebSocket {

    private static final ConcurrentHashMap<String, Session> sessions = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ProjectStateRepository stateRepository;

    /**
     * 连接建立时调用
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("projectId") String projectId) {
        sessions.put(projectId + ":" + session.getId(), session);
        System.out.println("WebSocket连接建立: " + projectId + " - " + session.getId());

        // 发送初始状态
        sendInitialStatus(projectId, session);
    }

    /**
     * 连接关闭时调用
     */
    @OnClose
    public void onClose(Session session, @PathParam("projectId") String projectId) {
        sessions.remove(projectId + ":" + session.getId());
        System.out.println("WebSocket连接关闭: " + projectId + " - " + session.getId());
    }

    /**
     * 收到消息时调用
     */
    @OnMessage
    public void onMessage(String message, Session session, @PathParam("projectId") String projectId) {
        System.out.println("收到消息: " + projectId + " - " + message);
        // TODO: 处理客户端消息
    }

    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Session session, Throwable error, @PathParam("projectId") String projectId) {
        System.err.println("WebSocket错误: " + projectId + " - " + error.getMessage());
        error.printStackTrace();
    }

    /**
     * 定时推送项目状态更新
     */
    public void startStatusUpdates() {
        scheduler.scheduleAtFixedRate(() -> {
            for (String key : sessions.keySet()) {
                String[] parts = key.split(":");
                String projectId = parts[0];
                Session session = sessions.get(key);

                if (session.isOpen()) {
                    sendProjectStatus(projectId, session);
                }
            }
        }, 0, 5, TimeUnit.SECONDS); // 每5秒推送一次状态更新
    }

    /**
     * 发送初始状态
     */
    private void sendInitialStatus(String projectId, Session session) {
        try {
            ProjectState state = stateRepository.load(projectId);
            if (state != null) {
                ProjectStatusResponse response = new ProjectStatusResponse();
                response.setProjectId(state.getProjectId());
                response.setOverallStatus(state.getOverallStatus().toString());
                response.setTaskCount(state.getTaskMap().size());
                response.setCompletedTasks(state.getTaskMap().values().stream()
                    .mapToInt(task -> task.getStatus() == com.aiteam.orchestrator.Task.TaskStatus.COMPLETED ? 1 : 0)
                    .sum());
                response.setFailedTasks(state.getTaskMap().values().stream()
                    .mapToInt(task -> task.getStatus() == com.aiteam.orchestrator.Task.TaskStatus.FAILED ? 1 : 0)
                    .sum());
                response.setRunningTasks(state.getTaskMap().values().stream()
                    .mapToInt(task -> task.getStatus() == com.aiteam.orchestrator.Task.TaskStatus.RUNNING ? 1 : 0)
                    .sum());

                sendMessage(session, "INITIAL_STATUS", objectMapper.writeValueAsString(response));
            }
        } catch (Exception e) {
            System.err.println("发送初始状态失败: " + e.getMessage());
        }
    }

    /**
     * 发送项目状态更新
     */
    private void sendProjectStatus(String projectId, Session session) {
        try {
            ProjectState state = stateRepository.load(projectId);
            if (state != null) {
                ProjectStatusResponse response = new ProjectStatusResponse();
                response.setProjectId(state.getProjectId());
                response.setOverallStatus(state.getOverallStatus().toString());
                response.setTaskCount(state.getTaskMap().size());
                response.setCompletedTasks(state.getTaskMap().values().stream()
                    .mapToInt(task -> task.getStatus() == com.aiteam.orchestrator.Task.TaskStatus.COMPLETED ? 1 : 0)
                    .sum());
                response.setFailedTasks(state.getTaskMap().values().stream()
                    .mapToInt(task -> task.getStatus() == com.aiteam.orchestrator.Task.TaskStatus.FAILED ? 1 : 0)
                    .sum());
                response.setRunningTasks(state.getTaskMap().values().stream()
                    .mapToInt(task -> task.getStatus() == com.aiteam.orchestrator.Task.TaskStatus.RUNNING ? 1 : 0)
                    .sum());

                sendMessage(session, "STATUS_UPDATE", objectMapper.writeValueAsString(response));
            }
        } catch (Exception e) {
            System.err.println("发送状态更新失败: " + e.getMessage());
        }
    }

    /**
     * 发送任务完成通知
     */
    public void sendTaskCompletedNotification(String projectId, String taskId) {
        Session session = sessions.get(projectId + ":");
        if (session != null && session.isOpen()) {
            try {
                String message = objectMapper.writeValueAsString(java.util.Map.of(
                    "type", "TASK_COMPLETED",
                    "projectId", projectId,
                    "taskId", taskId,
                    "timestamp", System.currentTimeMillis()
                ));
                sendMessage(session, "TASK_COMPLETED", message);
            } catch (Exception e) {
                System.err.println("发送任务完成通知失败: " + e.getMessage());
            }
        }
    }

    /**
     * 发送错误通知
     */
    public void sendErrorNotification(String projectId, String error) {
        Session session = sessions.get(projectId + ":");
        if (session != null && session.isOpen()) {
            try {
                String message = objectMapper.writeValueAsString(java.util.Map.of(
                    "type", "ERROR",
                    "projectId", projectId,
                    "error", error,
                    "timestamp", System.currentTimeMillis()
                ));
                sendMessage(session, "ERROR", message);
            } catch (Exception e) {
                System.err.println("发送错误通知失败: " + e.getMessage());
            }
        }
    }

    /**
     * 发送消息到指定会话
     */
    private void sendMessage(Session session, String type, String message) {
        try {
            session.getBasicRemote().sendText("{\"type\":\"" + type + "\"," + message.substring(1));
        } catch (IOException e) {
            System.err.println("发送消息失败: " + e.getMessage());
            sessions.remove(session.getId());
        }
    }
}