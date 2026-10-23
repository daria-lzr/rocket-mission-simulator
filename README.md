# Rocket Mission Simulator 🚀

Rocket Mission Simulator is a desktop application in which you launch a rocket, follow its flight, respond to emergencies, and deploy a satellite. It uses **JavaFX**, as recommended by our project tutor.

## 1. What you need

- A computer running **Windows, macOS, or Linux**.
- **Java Development Kit (JDK) 21:** [Download Eclipse Temurin JDK 21](https://adoptium.net/temurin/releases/?version=21). Choose your operating system and download the **JDK** (not the JRE), then run the installer.
- An internet connection for the first launch so the required libraries can be downloaded automatically. **Maven and JavaFX do not need to be installed separately.**

## 2. Build and run

1. **Extract the project ZIP** onto your computer.
2. Open **Terminal** (macOS/Linux) or **Command Prompt** (Windows), then go to the **project root folder** using `cd`. This is the folder containing `pom.xml` and `README.md`.

   **macOS/Linux** — if the project is in `IdeaProjects` (as in the original project):
   ```bash
   cd ~/IdeaProjects/rocket-mission-simulator
   ```
   If you extracted the ZIP into Downloads instead, use:
   ```bash
   cd ~/Downloads/rocket-mission-simulator
   ```

   **Windows** — if you extracted the ZIP into Downloads:
   ```bat
   cd /d "%USERPROFILE%\Downloads\rocket-mission-simulator"
   ```

   If you saved the project elsewhere, replace the folder path with its actual location.

3. Check the installed Java version:
   ```bash
   java -version
   ```
   It should show **Java 21**.

4. **Build and launch the application** from the same folder:

   **macOS/Linux:**
   ```bash
   ./mvnw clean javafx:run
   ```

   **Windows:**
   ```bat
   .\mvnw.cmd clean javafx:run
   ```

   The simulator window will open when the application is ready. The first launch can take longer while dependencies download. On macOS/Linux, if you see `Permission denied`, run `chmod +x mvnw` and try the launch command again.

   To **build without launching**, use `./mvnw clean package` on macOS/Linux or `.\mvnw.cmd clean package` on Windows.

## 3. How to use the simulator

1. Click **Launch** to start the rocket's automatic flight.
2. Watch the **Flight Telemetry** for live information and the **Mission Log** for updates and instructions.
3. If an emergency appears, select a response **within 20 seconds**. Some responses or a missed deadline may cause mission failure.
4. When the mission reaches **ALTITUDE REACHED**, click **Deploy Satellite**.
5. After success or failure, choose **Restart Mission** to play again or **Exit Application** to close the program.

The rocket is guided automatically. Its flight model is simplified; it does not simulate a physically stable orbit.

## 4. Where to find the final submission files

All files are inside the extracted `rocket-mission-simulator` project folder:

| Required item | Location |
| --- | --- |
| Runnable Java project and complete source code | The **entire project folder**; Java code is in `src/main/java/`, and images are in `src/main/resources/` |
| Build and usage instructions | **`README.md`**, in the project root |
| Final Product Backlog | [`docs/Rocket_Mission_Simulator_Final_Product_Backlog.pdf`](docs/Rocket_Mission_Simulator_Final_Product_Backlog.pdf) |
| Reflection on the two independent topics | [`docs/Independent_Research_Reflection.pdf`](docs/Independent_Research_Reflection.pdf) |

The `docs/` folder also contains the original backlog and implementation plan for reference; the files labeled **Final** are the ones intended for assessment.

## 5. Programming concepts used

The application uses **Object-Oriented Programming (OOP)** to organize the code into classes, each with a specific responsibility. Examples include:

- **Encapsulation:** A class protects its internal data and provides methods for other parts of the program to interact with it. For example, `Rocket` stores values such as altitude, velocity, fuel, and throttle in private fields. Other classes read these values using methods such as `getAltitude()` and `getThrottle()`, while `updateFlight()` calculates how they change. Similarly, `Mission` controls mission progress through methods such as `launch()` and `deploySatellite()`.
- **Abstraction:** Shared details are grouped behind a simpler interface, so other classes do not need to know how everything works internally. For instance, the abstract class `MissionOutcomeOverlay` handles the layout, buttons, and fade-in animation of a mission result screen. Other classes can simply call `showOutcome()` to display it.
- **Inheritance:** One class can reuse the properties and methods of another. `SuccessOverlay` and `FailureOverlay` both extend `MissionOutcomeOverlay`, inheriting the same screen structure and Restart/Exit buttons instead of implementing them twice.
- **Polymorphism:** Different subclasses can respond differently to the same method call. Both `SuccessOverlay` and `FailureOverlay` override `applyOutcomeAppearance()` to set their own colors and styling. When `showOutcome()` calls that method, Java uses the appropriate version for the actual overlay being displayed.

The two independently researched topics are **enums** (for mission states and events) and **multithreading** (for running simulation calculations separately from JavaFX interface updates). For more details, read [`Independent_Research_Reflection.pdf`](docs/Independent_Research_Reflection.pdf).

## 6. AI use

We used AI to research the mathematical models and calculations behind the simulator’s simplified rocket flight physics and engine plume effects. We then implemented these calculations in our code, allowing us to focus more on the application's functionality than on deriving the physics ourselves.

We also used AI to explore programming topics and learn how to implement them, as well as to generate graphical assets for the application.
