# TaskApp - Android Task Management Application

## 1. Overview

TaskApp is an Android application designed for managing tasks using a Kanban-style interface. Users can create, view, edit, and delete tasks, organize them into "Upcoming," "In Progress," and "Finished" columns, and set properties like due dates and priorities. The application utilizes Firebase Firestore for real-time data persistence and synchronization. It features a clean, Material Design 3 interface with support for both light and dark themes.

## 2. Features

*   **Task Creation & Management:** Add new tasks with a title, description, due date, and priority.
*   **Task Editing:** Modify existing task details.
*   **Task Deletion:** Remove tasks with a confirmation dialog.
*   **Kanban Board:**
    *   View tasks in three columns: "Upcoming," "In Progress," and "Finished."
    *   Move tasks between columns (e.g., "Start Task" moves from Upcoming to In Progress, "Finish Task" moves from In Progress to Finished).
    *   Mark tasks as complete/incomplete.
*   **Due Dates:** Assign and display due dates for tasks.
*   **Priority Levels:** Assign "Low," "Medium," or "High" priority to tasks, visually indicated.
*   **Sorting:** Sort tasks within columns by Due Date, Priority, or Alphabetically.
*   **Data Persistence:** Tasks are saved to and retrieved from Firebase Firestore.
*   **Real-time Updates:** Changes to tasks are reflected across the app in real-time.
*   **Splash Screen:** A simple introductory screen on app launch.
*   **Material Design 3:** Modern UI components and theming.
*   **Light/Dark Theme Support:** Adapts to system theme settings.

## 3. Tech Stack & Architecture

*   **Language:** Java
*   **Architecture:** Model-View-ViewModel (MVVM)
    *   **View:** Activities (`MainActivity`, `AddEditTaskActivity`, `SplashActivity`) and Fragments (`TaskListFragment`).
    *   **ViewModel:** `TaskViewModel` (manages UI-related data and business logic, survives configuration changes).
    *   **Model:** `TaskRepository` (handles data operations) and `Task` (data class).
*   **UI:**
    *   Android XML Layouts
    *   Material Design 3 Components (`MaterialToolbar`, `TabLayout`, `ViewPager2`, `FloatingActionButton`, `MaterialCardView`, `TextInputEditText`, `MaterialButton`, `Spinner`)
    *   `RecyclerView` for displaying lists of tasks.
*   **Data Persistence:**
    *   Firebase Firestore (NoSQL cloud database for real-time data storage).
*   **Asynchronous Operations:**
    *   `LiveData` for observing data changes.
*   **Dependency Management:**
    *   Gradle with Kotlin DSL (`.kts`)
    *   Version Catalog (`libs.versions.toml`)
*   **Key Libraries:**
    *   `androidx.appcompat`: For backward compatibility.
    *   `com.google.android.material`: Material Design components.
    *   `androidx.lifecycle`: ViewModel and LiveData.
    *   `androidx.recyclerview`: Efficiently display large sets of data.
    *   `androidx.viewpager2`: For swipeable views (Kanban columns).
    *   `com.google.firebase:firebase-firestore`: Firestore client library.
    *   `androidx.core:core-splashscreen`: For the splash screen.

## 5. Core Components (Java Classes)

### 5.1. Activities

*   **`SplashActivity.java`**
    *   The launcher activity of the application.
    *   Displays a simple splash screen (`layout/activity_splash.xml`) for a short duration (`SPLASH_DELAY`).
    *   Navigates to `MainActivity` after the delay.
*   **`MainActivity.java`**
    *   The main screen of the app, hosting the Kanban board.
    *   Uses a `ViewPager2` and `TabLayout` with `KanbanPagerAdapter` to display three `TaskListFragment` instances (Upcoming, In Progress, Finished).
    *   Initializes `TaskViewModel` to observe and display tasks.
    *   Handles task data distribution to the appropriate fragments based on task status and completion.
    *   Implements `TaskListFragment.OnTaskInteractionListener` to handle clicks, checkbox changes, and actions (delete, start, finish) on tasks originating from fragments.
    *   Provides sorting functionality (Due Date, Priority, Alphabetically) through an options menu (`menu/main_menu.xml`).
    *   A `FloatingActionButton` (FAB) launches `AddEditTaskActivity` for creating new tasks.
*   **`AddEditTaskActivity.java`**
    *   Allows users to create new tasks or edit existing ones.
    *   The layout (`layout/activity_add_edit_task.xml`) includes fields for task title, description, due date (with a `DatePickerDialog`), and priority (Spinner).
    *   If an existing `Task` object is passed via `Intent` (using `EXTRA_TASK`), the activity populates the fields for editing.
    *   Uses `TaskViewModel` to `insert` new tasks or `update` existing tasks in Firestore.
    *   Includes a toolbar with a close button to navigate back.

### 5.2. Fragments

*   **`TaskListFragment.java`**
    *   A reusable fragment that displays a list of tasks for a specific category (Upcoming, In Progress, or Finished).
    *   Receives the task status type (`TYPE_UPCOMING`, `TYPE_IN_PROGRESS`, `TYPE_FINISHED`) as an argument.
    *   Uses a `RecyclerView` with `TaskAdapter` to display the tasks.
    *   Shows an "empty view" message if no tasks are present in its category.
    *   Communicates user interactions (task clicks, completion changes, etc.) back to the hosting Activity (`MainActivity`) via the `OnTaskInteractionListener` interface.

### 5.3. Adapters

*   **`KanbanPagerAdapter.java`**
    *   A `FragmentStateAdapter` for the `ViewPager2` in `MainActivity`.
    *   Manages the three `TaskListFragment` instances, one for each Kanban column (Upcoming, In Progress, Finished).
*   **`TaskAdapter.java`**
    *   A `RecyclerView.Adapter` responsible for binding `Task` data to the `list_item_task.xml` layout.
    *   Displays task title, due date (if any), and priority (with a colored indicator).
    *   Handles visual changes for completed tasks (strikethrough text, different background color).
    *   Dynamically shows/hides "Start Task", "Finish Task" buttons, and the completion checkbox based on the task's status and completion state.
    *   Provides an `OnItemClickListener` interface to notify the listener (typically `TaskListFragment`) about user interactions like item clicks, checkbox changes, delete clicks, start clicks, and finish clicks.
    *   Adapts card background for night mode.

### 5.4. Data Model

*   **`Task.java`**
    *   A Plain Old Java Object (POJO) representing a single task.
    *   Implements `Serializable` to be passed between activities via Intents.
    *   Fields:
        *   `id`: `String` (Firestore document ID, annotated with `@DocumentId`).
        *   `title`: `String` (Task title).
        *   `description`: `String` (Task description).
        *   `dueDate`: `Long` (Timestamp for the due date).
        *   `priority`: `String` ("Low", "Medium", "High").
        *   `completed`: `boolean` (Indicates if the task is completed).
        *   `status`: `String` (Current status, e.g., `STATUS_UPCOMING`, `STATUS_IN_PROGRESS`. `null` if completed).
        *   `lastUpdated`: `Date` (Timestamp for the last update, annotated with `@ServerTimestamp` for Firebase).
    *   Static constants `STATUS_UPCOMING` and `STATUS_IN_PROGRESS`.

### 5.5. ViewModel & Repository (MVVM)

*   **`TaskViewModel.java`**
    *   Extends `AndroidViewModel`.
    *   Acts as a bridge between the UI (`MainActivity`, `AddEditTaskActivity`) and the `TaskRepository`.
    *   Holds `LiveData<List<Task>>` observed by the UI for real-time updates.
    *   Provides methods (`insert`, `update`, `delete`) that delegate calls to the `TaskRepository`.
*   **`TaskRepository.java`**
    *   Manages all data operations related to tasks.
    *   Interacts directly with Firebase Firestore's `tasks` collection.
    *   Uses a Firestore `addSnapshotListener` to listen for real-time updates to the `tasks` collection and posts the updated list to `allTasksLiveData`.
    *   Tasks are initially fetched ordered by `lastUpdated` descending.
    *   Provides methods:
        *   `getAllTasks()`: Returns `LiveData<List<Task>>`.
        *   `addTask(Task task)`: Adds a new task to Firestore.
        *   `updateTask(Task task)`: Updates an existing task in Firestore.
        *   `deleteTask(Task task)`: Deletes a task from Firestore.

### 5.6. Utilities

*   **`DateUtils.java`**
    *   A simple utility class with a static method `formatDate(Long timestamp)` to convert a `Long` timestamp into a human-readable date string (e.g., "MMM dd, yyyy").

## 6. Key Configuration Files

*   **`app/build.gradle.kts`**:
    *   Specifies Android SDK versions (`compileSdk = 35`, `minSdk = 24`, `targetSdk = 35`).
    *   Declares application ID, version code, and version name.
    *   Manages dependencies (see `gradle/libs.versions.toml`).
    *   Applies `com.google.gms.google-services` plugin for Firebase integration.
    *   Configures Java 11 compatibility.
*   **`AndroidManifest.xml`**:
    *   Declares application components: `SplashActivity` (launcher), `MainActivity`, `AddEditTaskActivity`.
    *   Requests `android.permission.INTERNET` for Firebase communication.
    *   Sets application icon, label, theme, and backup rules.
*   **`google-services.json`**:
    *   Located in the `app/` directory.
    *   Contains Firebase project configuration details (project ID, app ID, API key) necessary for connecting the app to your Firebase project. **This file should be obtained from your Firebase console.**
*   **`gradle/libs.versions.toml`**:
    *   Gradle Version Catalog for centralized dependency version management. Simplifies updating library versions and ensures consistency.
*   **`.gitignore` files**:
    *   Specify files and directories to be ignored by Git (e.g., build outputs, IDE files).

## 7. Build and Run

1.  **Prerequisites:**
    *   Android Studio (latest stable version recommended).
    *   Java Development Kit (JDK).
    *   Android SDK configured in Android Studio.
2.  **Firebase Setup:**
    *   Create a Firebase project at [https://console.firebase.google.com/](https://console.firebase.google.com/).
    *   Add an Android app to your Firebase project.
        *   Use `com.example.taskapp` as the package name (or update it in `app/build.gradle.kts` and `AndroidManifest.xml` if you change it).
    *   Download the `google-services.json` file from your Firebase project settings.
    *   Place the `google-services.json` file in the `app/` directory of this project.
    *   In the Firebase console, navigate to "Firestore Database" and create a database. Choose your desired region and start in "test mode" for easier development (remember to set up proper security rules for production).
3.  **Clone the Repository:**
    ```bash
    git clone <repository-url>
    cd <project-directory>
    ```
4.  **Open in Android Studio:**
    *   Open Android Studio.
    *   Select "Open an Existing Project" and navigate to the cloned project directory.
    *   Android Studio will sync the Gradle project.
5.  **Run the Application:**
    *   Connect an Android device or start an Android emulator.
    *   Click the "Run" button (green play icon) in Android Studio or select "Run" > "Run 'app'".

## 8. Dependencies

Key dependencies are managed in `gradle/libs.versions.toml` and declared in `app/build.gradle.kts`.

*   **AndroidX Libraries:**
    *   `appcompat`: For material design backward compatibility.
    *   `material`: Material Design Components.
    *   `activity`: Base class for activities.
    *   `constraintlayout`: For flexible UI design.
    *   `lifecycle-viewmodel`, `lifecycle-livedata`: For MVVM architecture.
    *   `recyclerview`: For efficient list display.
    *   `core-splashscreen`: For the Android 12+ splash screen API.
*   **Firebase:**
    *   `firebase-bom`: Firebase Bill of Materials to manage versions.
    *   `firebase-firestore`: For Cloud Firestore database.
*   **Testing:**
    *   `junit`: For unit testing.
    *   `androidx.test.ext:junit`, `androidx.test.espresso:espresso-core`: For instrumented testing.

## 9. Firebase Integration

*   **Firestore Database:**
    *   Tasks are stored in a Firestore collection named `tasks`.
    *   Each task is a document within this collection.
    *   The `Task.java` model class is used for serialization/deserialization with Firestore.
    *   `@DocumentId` annotation on `Task.id` maps the Firestore document ID.
    *   `@ServerTimestamp` annotation on `Task.lastUpdated` automatically populates the server timestamp.
*   **Real-time Updates:**
    *   `TaskRepository` uses `addSnapshotListener` to listen for changes in the `tasks` collection, providing real-time updates to the UI via `LiveData`.
*   **Configuration:**
    *   `google-services.json` file connects the app to the Firebase project.
    *   The `com.google.gms.google-services` Gradle plugin processes this file.

## 10. UI and Styling

*   **Layouts:** Defined in XML files under `app/src/main/res/layout/`.
*   **Themes:**
    *   Defined in `app/src/main/res/values/themes.xml` and `app/src/main/res/values-night/themes.xml`.
    *   Uses Material 3 (`Theme.Material3.DayNight`).
    *   Custom styles for Toolbar title (`App.Toolbar.Title`).
    *   Specific theme for `SplashActivity` (`Theme.Taskapp.Splash`).
*   **Colors:** Defined in `app/src/main/res/values/colors.xml`, including Material 3 theme colors, priority colors, and custom app UI colors for light and dark modes.
*   **Drawables:** Custom vector icons and shapes are in `app/src/main/res/drawable/`.
    *   `priority_indicator_shape.xml`: Used for the colored priority dot.
    *   `date_textview_background.xml`: Custom background for the due date display.
    *   `spinner_background_material.xml`: Custom background for spinners to match Material 3 style.
*   **Strings and Dimensions:** Managed in `app/src/main/res/values/strings.xml` and `app/src/main/res/values/dimens.xml`.

## 11. Testing

*   **Unit Tests:**
    *   Located in `app/src/test/java/`.
    *   `ExampleUnitTest.java` provides a basic example.
*   **Instrumented Tests:**
    *   Located in `app/src/androidTest/java/`.
    *   `ExampleInstrumentedTest.java` provides a basic example for tests that run on an Android device/emulator.

---

This documentation provides a comprehensive overview for developers to understand, build, and contribute to the TaskApp project.
