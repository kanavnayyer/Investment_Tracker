# Investment Tracker

**Investment Tracker** is an Android app that allows users to store and manage their investments and expenses. The app provides an intuitive interface to track financial data, visualize it with pie charts, and monitor progress with a progress bar.

## Features

- **Track Investments & Expenses:** Users can add investments and expenses with descriptions, categories, and dates.
- **Automatic Time Recording:** The app automatically records the current time when an investment or expense is entered.
- **View Data by Month, Week, Year, or All Time:** Users can view their investments and expenses categorized by different time frames.
- **Pie Chart Visualization:** The app provides a pie chart to show the distribution of expenses and income.
- **Progress Bar for Expense Tracking:** A progress bar shows how much of the user's total budget has been spent.
- **Total Balance Calculation:** Users can see the total balance after accounting for investments and expenses.
- **Room Database:** All data is securely stored in a local Room database.

## Architecture

The app follows **MVVM (Model-View-ViewModel)** architecture for better separation of concerns and testability. The **Navigation Component** with a **NavGraph** is used for managing app navigation and ensuring a streamlined flow between fragments and activities.

### Navigation Component & NavGraph

The app uses Android's **Navigation Component** to handle the app's navigation. The NavGraph is defined in a navigation XML file and is used to manage transitions between different fragments and activities, ensuring that the app’s flow is clear and maintainable.

- **Fragments:** The app consists of multiple fragments, each handling specific features like displaying investments, expenses, or a pie chart.
- **NavGraph:** The `nav_graph.xml` file defines all the navigation actions and directions between these fragments.
- **Safe Args:** Safe Args plugin is used for type-safe navigation between fragments.

This helps in managing the navigation flow easily, reducing the boilerplate code, and providing a clear structure for the app’s navigation.

## Technologies Used

- **Kotlin:** The app is written in Kotlin.
- **XML:** XML is used for designing the UI.
- **Room Database:** Used for storing data locally in a structured format.
- **MPAndroidChart:** Used for displaying pie charts of expenses and income.
- **Navigation Component:** For managing navigation between different screens.
- **NavGraph:** XML file defining navigation actions and fragment transitions.

## Setup

1. Clone the repository:
    ```bash
    git clone https://github.com/yourusername/investment-tracker.git
    ```
2. Open the project in Android Studio.
3. Build and run the app on an Android device or emulator.


