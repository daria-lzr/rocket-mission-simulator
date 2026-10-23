# Rocket Mission Simulator 🚀

Rocket Mission Simulator is a desktop application in which you launch a rocket, follow its flight, respond to emergencies, and deploy a satellite.

The application is developed in **Java 21** and uses **JavaFX** instead of Java Swing, as explicitly approved by our project tutor.

## 1. What you need

- A computer running **Windows, macOS, or Linux**.
- **Java Development Kit (JDK) 21:** [Download Eclipse Temurin JDK 21](https://adoptium.net/temurin/releases/?version=21). Choose your operating system, download the **JDK** (not the JRE), and run the installer.
- An internet connection for the first launch so the required libraries can be downloaded automatically.

**Maven and JavaFX do not need to be installed separately.** The project includes the Maven Wrapper, which automatically downloads the required Maven version and project dependencies.

## 2. Build and run

### Step 1 — Extract the project

Extract the project ZIP archive onto your computer.

### Step 2 — Open the project directory

Open **Terminal** (macOS/Linux) or **Command Prompt** (Windows).

Navigate to the project's root directory using `cd`. This is the folder containing `pom.xml` and `README.md`.

**macOS/Linux** — if the project is located in `IdeaProjects`:

```bash
cd ~/IdeaProjects/rocket-mission-simulator-main
```

If you extracted the ZIP into Downloads instead:

```bash
cd ~/Downloads/rocket-mission-simulator-main
```

**Windows** — if you extracted the ZIP into Downloads:

```bat
cd /d "%USERPROFILE%\Downloads\rocket-mission-simulator-main"
```

If you saved the project elsewhere, replace the folder path with its actual location.

### Step 3 — Verify the Java installation

Check the installed Java version:

```bash
java -version
```

Then check the Java compiler:

```bash
javac -version
```

Both commands should indicate **Java 21**.

If Java is not recognized, ensure that JDK 21 is installed and correctly configured in your system's PATH.

### Step 4 — Build and launch the application

From the project root directory, execute the appropriate command for your operating system.

**macOS/Linux:**

```bash
./mvnw clean javafx:run
```

**Windows:**

```bat
mvnw.cmd clean javafx:run
```

The simulator window will open when the application is ready.

**Important notes:**

- The first launch may take longer because Maven needs to download the required dependencies.
- Subsequent launches should be faster because downloaded dependencies are cached locally.
- On macOS/Linux, if you encounter a `Permission denied` error, execute:

  ```bash
  chmod +x mvnw
  ```

  Then repeat the launch command.

### Step 5 — Build without launching (optional)

To compile and package the application without starting the simulator, use:

**macOS/Linux:**

```bash
./mvnw clean package
```

**Windows:**

```bat
mvnw.cmd clean package
```

## 3. How to use the simulator

1. Click **Launch** to start the rocket's automatic flight.
2. Watch the **Flight Telemetry** panel for continuously updated flight information, including altitude, velocity, and fuel.
3. Follow the **Mission Log** for important updates about the rocket and its mission.
4. If an emergency occurs, select a response **within 20 seconds**. Different responses may allow the mission to continue or cause it to fail. Failing to respond before the deadline may also result in mission failure.
5. Once the rocket reaches **ALTITUDE REACHED**, click **Deploy Satellite** to complete the mission.
6. After the mission succeeds or fails, select **Restart Mission** to begin another attempt or **Exit Application** to close the simulator.

### Correct emergency responses (for testing)

The emergencies occur during ascent in the following order. To complete a successful mission, click the indicated response in the Mission Log before the 20-second timer expires.

| Event | Approximate altitude | Correct response to select |
| --- | --- | --- |
| **Engine Overheating** | 400 m | **[1] REDUCE ENGINE THROTTLE** **or** **[2] ACTIVATE EMERGENCY COOLING** — both are accepted. |
| **Aerodynamic Stress (Max-Q)** | 8 km | **[2] REDUCE THROTTLE TO LOWER PRESSURE** |
| **Propellant Instability** | 50 km | **[3] ACTIVATE PROPELLANT SLOSH DAMPING** |
| **Attitude Control Failure** | 220 km | **[3] ISOLATE PRIMARY RCS / ENABLE BACKUP** |

Other responses, or letting the timer expire, cause mission failure. You can test the failure screen by deliberately choosing an incorrect answer, then selecting **Restart Mission** to try again.

The rocket is guided automatically. The flight model is simplified and does not simulate a physically stable orbit.

## 4. Final submission files

All required submission files are included in the extracted `rocket-mission-simulator-main` project folder.

| Required item | Location |
| --- | --- |
| Runnable Java project and complete source code | Entire project directory. Java source files are located in `src/main/java/`, and application resources are located in `src/main/resources/`. |
| Build and usage instructions | `README.md` in the project root directory |
| Final Product Backlog | [docs/Rocket_Mission_Simulator_Final_Product_Backlog.pdf](docs/Rocket_Mission_Simulator_Final_Product_Backlog.pdf) |
| Reflection on the two independently researched topics | [docs/Independent_Research_Reflection.pdf](docs/Independent_Research_Reflection.pdf) |

The `docs/` directory also contains earlier project documentation, including the original backlog and implementation plan, for reference.

The documents explicitly identified as the final Product Backlog and Independent Research Reflection are the versions intended for assessment.

## 5. Programming concepts used

The application uses **Object-Oriented Programming (OOP)** to organize its functionality into classes with clearly defined responsibilities.

### Encapsulation

Encapsulation protects the internal state of an object and provides controlled access through methods.

For example, the `Rocket` class stores values such as altitude, velocity, fuel, and throttle in private fields.

Other classes can access these values through methods such as `getAltitude()` and `getThrottle()`, while `updateFlight()` handles the calculations that change the rocket's flight state.

Similarly, the `Mission` class manages mission progression through methods such as `launch()` and `deploySatellite()`.


### Inheritance

Inheritance allows subclasses to reuse functionality defined in a parent class.

For example, `SuccessOverlay` and `FailureOverlay` both extend `MissionOutcomeOverlay`.

They inherit the shared structure and functionality of the outcome screen, including the Restart and Exit buttons, avoiding unnecessary code duplication.

### Polymorphism

Polymorphism allows different subclasses to provide their own implementations of the same method.

Both `SuccessOverlay` and `FailureOverlay` override `applyOutcomeAppearance()` to define their specific appearance.

When `showOutcome()` invokes this method, Java automatically selects the appropriate implementation based on the actual overlay object.

### Independently researched topics

The project also integrates two independently researched programming topics:

**1. Java Enums**

Enums are used to represent the different mission states and events.

They help organize the mission's progression and ensure that only predefined states and events are represented.

**2. Multithreading and Concurrency**

The application uses `ScheduledExecutorService` to execute simulation calculations on a separate thread.

This allows the rocket's flight data to update continuously while keeping the JavaFX graphical interface responsive.

JavaFX interface updates are performed on the JavaFX Application Thread to ensure thread safety.

For a more detailed explanation of both topics, see the [Independent Research Reflection](docs/Independent_Research_Reflection.pdf).

## 6. AI use

AI tools were used as supporting resources throughout the development process.

We used AI to research the mathematical models and calculations behind the simulator's simplified rocket flight physics and engine plume effects. We incorporated these concepts into the application to focus on implementing the simulator's functionality rather than deriving the mathematical models entirely from scratch.

We also used AI to explore programming concepts, understand implementation techniques, troubleshoot development challenges, and assist with code development.

Additionally, AI was used to generate graphical assets for the application.

We reviewed and worked to understand the code, concepts, and implementation decisions incorporated into the final project.
