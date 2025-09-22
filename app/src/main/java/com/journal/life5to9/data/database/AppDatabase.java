package com.journal.life5to9.data.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.journal.life5to9.data.converter.DateConverter;

import android.content.Context;

import com.journal.life5to9.data.dao.ActivityDao;
import com.journal.life5to9.data.dao.CategoryDao;
import com.journal.life5to9.data.entity.Activity;
import com.journal.life5to9.data.entity.Category;

@Database(
    entities = {Activity.class, Category.class},
    version = 1,
    exportSchema = false
)
@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    
    private static volatile AppDatabase INSTANCE;
    
    public abstract ActivityDao activityDao();
    public abstract CategoryDao categoryDao();
    
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),
                        AppDatabase.class,
                        "life5to9_database"
                    )
                    .addCallback(new RoomDatabase.Callback() {
                        @Override
                        public void onCreate(SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            // Insert default categories when database is created
                            new Thread(() -> {
                                AppDatabase database = getDatabase(context);
                                database.categoryDao().insertCategory(new Category("Fitness", "#FF5722", "fitness_center", true));
                                database.categoryDao().insertCategory(new Category("Family", "#E91E63", "family_restroom", true));
                                database.categoryDao().insertCategory(new Category("Learning", "#9C27B0", "school", true));
                                database.categoryDao().insertCategory(new Category("Entertainment", "#3F51B5", "movie", true));
                                database.categoryDao().insertCategory(new Category("Chores", "#FF9800", "home", true));
                                database.categoryDao().insertCategory(new Category("Social", "#4CAF50", "people", true));
                                database.categoryDao().insertCategory(new Category("Rest", "#607D8B", "bedtime", true));
                            }).start();
                        }
                    })
                    .build();
                }
            }
        }
        return INSTANCE;
    }
    
    // Migration strategy for future database updates
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Add migration logic here when needed
            // Example: database.execSQL("ALTER TABLE activities ADD COLUMN new_column TEXT");
        }
    };
}
