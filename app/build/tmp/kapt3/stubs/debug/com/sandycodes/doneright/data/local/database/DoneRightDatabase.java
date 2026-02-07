package com.sandycodes.doneright.data.local.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import com.sandycodes.doneright.data.local.Dao.TaskDao;
import com.sandycodes.doneright.data.local.Entity.TaskEntity;

@kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\b\'\u0018\u0000 \u00062\u00020\u0001:\u0001\u0006B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0004\u001a\u00020\u0005H&\u00a8\u0006\u0007"}, d2 = {"Lcom/sandycodes/doneright/data/local/database/DoneRightDatabase;", "Landroidx/room/RoomDatabase;", "<init>", "()V", "taskDao", "Lcom/sandycodes/doneright/data/local/Dao/TaskDao;", "Companion", "app_debug"})
@androidx.room.Database(entities = {com.sandycodes.doneright.data.local.Entity.TaskEntity.class}, version = 1, exportSchema = false)
@androidx.room.TypeConverters(value = {com.sandycodes.doneright.data.local.database.TaskStatusConverter.class})
public abstract class DoneRightDatabase extends androidx.room.RoomDatabase {
    @kotlin.jvm.Volatile()
    @org.jetbrains.annotations.Nullable()
    private static volatile com.sandycodes.doneright.data.local.database.DoneRightDatabase INSTANCE;
    @org.jetbrains.annotations.NotNull()
    public static final com.sandycodes.doneright.data.local.database.DoneRightDatabase.Companion Companion = null;
    
    public DoneRightDatabase() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.sandycodes.doneright.data.local.Dao.TaskDao taskDao();
    
    @kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0006\u001a\u00020\u00052\u0006\u0010\u0007\u001a\u00020\bR\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\t"}, d2 = {"Lcom/sandycodes/doneright/data/local/database/DoneRightDatabase$Companion;", "", "<init>", "()V", "INSTANCE", "Lcom/sandycodes/doneright/data/local/database/DoneRightDatabase;", "getInstance", "context", "Landroid/content/Context;", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.sandycodes.doneright.data.local.database.DoneRightDatabase getInstance(@org.jetbrains.annotations.NotNull()
        android.content.Context context) {
            return null;
        }
    }
}