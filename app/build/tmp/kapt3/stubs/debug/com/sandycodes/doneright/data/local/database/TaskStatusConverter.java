package com.sandycodes.doneright.data.local.database;

import androidx.room.TypeConverter;
import com.sandycodes.doneright.data.local.Entity.TaskStatus;

@kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0007J\u0010\u0010\b\u001a\u00020\u00072\u0006\u0010\u0006\u001a\u00020\u0005H\u0007\u00a8\u0006\t"}, d2 = {"Lcom/sandycodes/doneright/data/local/database/TaskStatusConverter;", "", "<init>", "()V", "fromStatus", "", "status", "Lcom/sandycodes/doneright/data/local/Entity/TaskStatus;", "toStatus", "app_debug"})
public final class TaskStatusConverter {
    
    public TaskStatusConverter() {
        super();
    }
    
    @androidx.room.TypeConverter()
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String fromStatus(@org.jetbrains.annotations.NotNull()
    com.sandycodes.doneright.data.local.Entity.TaskStatus status) {
        return null;
    }
    
    @androidx.room.TypeConverter()
    @org.jetbrains.annotations.NotNull()
    public final com.sandycodes.doneright.data.local.Entity.TaskStatus toStatus(@org.jetbrains.annotations.NotNull()
    java.lang.String status) {
        return null;
    }
}