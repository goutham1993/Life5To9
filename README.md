# Life 5To9 - After Work Activity Tracker

A comprehensive Android application built with Java that helps you track and analyze how you spend your time after work (from 5 PM onwards). The app provides insights into your daily activities, helping you reflect and introspect on your time management.

## Features

### 📱 Three Main Tabs
- **Activities**: View all your recorded activities in card format
- **Categories**: Manage and view activity categories
- **Summary**: Analyze your time usage with weekly and monthly summaries

### 🎯 Core Functionality
- **Add Activities**: Track activities with category, notes, and time spent
- **Category Management**: Pre-defined categories (Fitness, Family, Learning, Entertainment, Chores, Social, Rest)
- **Time Analysis**: Weekly and monthly time summaries with category breakdowns
- **Clean UI**: Material Design 3 with theme-aware components

### 🏗️ Architecture
- **Clean Architecture**: Separation of concerns with Repository, Service, and ViewModel layers
- **Room Database**: Local data persistence with migration support
- **MVVM Pattern**: LiveData and ViewModel for reactive UI updates
- **Dependency Injection**: Manual DI with proper interfaces

## Technical Stack

- **Language**: Java
- **Database**: Room (SQLite)
- **UI**: Material Design 3, ConstraintLayout, ViewPager2, RecyclerView
- **Architecture**: MVVM with Repository pattern
- **Navigation**: TabLayout with ViewPager2
- **State Management**: LiveData and ViewModel

## Project Structure

```
app/src/main/java/com/journal/life5to9/
├── data/
│   ├── entity/           # Room entities (Activity, Category)
│   ├── dao/              # Data Access Objects
│   ├── database/         # Room database configuration
│   └── repository/       # Repository interfaces and implementations
├── service/              # Business logic services
├── ui/
│   ├── fragments/        # Main UI fragments
│   ├── adapters/         # RecyclerView adapters
│   └── dialogs/          # Custom dialogs
├── viewmodel/            # ViewModels and ViewModelFactory
└── MainActivity.java     # Main activity with TabLayout setup
```

## Database Schema

### Activity Entity
- `id`: Primary key (auto-generated)
- `categoryId`: Foreign key to Category
- `notes`: Activity description
- `timeSpentHours`: Time spent in hours
- `date`: Activity date
- `createdAt`: Record creation timestamp
- `updatedAt`: Record update timestamp

### Category Entity
- `id`: Primary key (auto-generated)
- `name`: Category name
- `color`: Category color (hex)
- `icon`: Category icon identifier
- `isDefault`: Whether it's a default category
- `createdAt`: Record creation timestamp
- `updatedAt`: Record update timestamp

## Default Categories

The app comes with 7 pre-defined categories:
1. **Fitness** - Physical activities and exercise
2. **Family** - Time spent with family members
3. **Learning** - Educational activities and skill development
4. **Entertainment** - Movies, games, and recreational activities
5. **Chores** - Household tasks and maintenance
6. **Social** - Socializing with friends and colleagues
7. **Rest** - Relaxation and downtime

## Key Features Implementation

### 🎨 UI Components
- **AppBarLayout + Toolbar**: Custom styled with primary colors
- **TabLayout**: Three tabs with proper indicators
- **ViewPager2**: Smooth fragment transitions
- **RecyclerView**: Efficient list rendering with custom adapters
- **FloatingActionButton**: Quick access to add activities
- **Material Cards**: Clean card-based design for activities

### 🗄️ Data Management
- **Room Database**: Type-safe database access
- **Migration Strategy**: Future-proof database updates
- **LiveData**: Reactive data updates
- **Repository Pattern**: Clean data access layer

### 🎯 Activity Tracking
- **Add Activity Dialog**: Comprehensive form with validation
- **Category Selection**: Dropdown with all available categories
- **Time Input**: Decimal hours with validation
- **Date/Time Selection**: Current date/time as default

### 📊 Analytics
- **Weekly Summary**: Total time tracked this week
- **Monthly Summary**: Total time tracked this month
- **Category Breakdown**: Visual representation of time distribution
- **Progress Bars**: Visual progress indicators

## Getting Started

1. **Clone the repository**
2. **Open in Android Studio**
3. **Sync Gradle files**
4. **Run on device or emulator**

The app will automatically:
- Initialize the Room database
- Create default categories
- Set up the UI with three tabs
- Provide a working FAB for adding activities

## Usage

1. **Adding Activities**: Tap the FAB (+) button to add new activities
2. **Viewing Activities**: Switch to the Activities tab to see all recorded activities
3. **Managing Categories**: Use the Categories tab to view available categories
4. **Analyzing Time**: Check the Summary tab for insights and analytics

## Future Enhancements

- Date and time pickers for activity creation
- Edit/delete functionality for activities and categories
- Export data functionality
- Charts and graphs for better visualization
- Backup and restore functionality
- Dark theme support
- Widget support for quick activity logging

## Dependencies

- **AndroidX**: Core Android libraries
- **Material Design**: UI components
- **Room**: Database persistence
- **Lifecycle**: ViewModel and LiveData
- **ViewPager2**: Tab navigation
- **RecyclerView**: List management

## License

This project is created for personal use and learning purposes.

---

**Life 5To9** - Track your after-work time, reflect on your choices, and make the most of your evenings! 🌟
