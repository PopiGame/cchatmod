package net.cchat.cchatmod.data.tasks;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TaskManager {
    private List<Task> tasks;
    private static final Gson gson = new Gson();
    private static final Path SAVE_PATH = FabricLoader.getInstance().getConfigDir().resolve("cchatmod/tasks.json");

    public TaskManager() {
        tasks = new ArrayList<>();
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public void clearTasks() {
        tasks.clear();
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public List<Task> getTasksByStatus(TaskStatus status) {
        List<Task> filtered = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getStatus() == status) {
                filtered.add(task);
            }
        }
        return filtered;
    }

    public void saveTasks() {
        try {
            Files.createDirectories(SAVE_PATH.getParent());
            try (Writer writer = new FileWriter(SAVE_PATH.toFile())) {
                gson.toJson(tasks, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadTasks() {
        if (!Files.exists(SAVE_PATH)) {
            return;
        }
        try (Reader reader = new FileReader(SAVE_PATH.toFile())) {
            Type listType = new TypeToken<List<Task>>(){}.getType();
            List<Task> loaded = gson.fromJson(reader, listType);
            if (loaded != null) {
                tasks = loaded;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}