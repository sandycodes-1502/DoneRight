package com.sandycodes.doneright.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.sandycodes.doneright.data.local.Entity.TaskEntity;
import com.sandycodes.doneright.data.repository.TaskRepository;

@kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u0006\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\b0\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b\u00a8\u0006\f"}, d2 = {"Lcom/sandycodes/doneright/ui/home/HomeViewModel;", "Landroidx/lifecycle/ViewModel;", "repository", "Lcom/sandycodes/doneright/data/repository/TaskRepository;", "<init>", "(Lcom/sandycodes/doneright/data/repository/TaskRepository;)V", "tasks", "Landroidx/lifecycle/LiveData;", "", "Lcom/sandycodes/doneright/data/local/Entity/TaskEntity;", "getTasks", "()Landroidx/lifecycle/LiveData;", "app_debug"})
public final class HomeViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.sandycodes.doneright.data.repository.TaskRepository repository = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.LiveData<java.util.List<com.sandycodes.doneright.data.local.Entity.TaskEntity>> tasks = null;
    
    public HomeViewModel(@org.jetbrains.annotations.NotNull()
    com.sandycodes.doneright.data.repository.TaskRepository repository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.util.List<com.sandycodes.doneright.data.local.Entity.TaskEntity>> getTasks() {
        return null;
    }
}