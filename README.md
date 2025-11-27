# DSA Learning Simulator
The **DSA Learning Simulator** is an innovative desktop application built with JavaFX that transforms how students and developers learn Data Structures and Algorithms. Unlike traditional learning methods, our simulator provides an interactive, hands-on approach with real-time progress tracking, personalized recommendations, and comprehensive practice materials.


###  Core Learning Features
- **Algorithm Explanations**: Detailed explanations with step-by-step breakdowns
- **Interactive Visualizations**: Visual representations of algorithm execution
- **Practice Problems**: Curated quizzes and coding challenges
- **Multiple Languages**: Code examples in Java, Python, JavaScript, C++, and C

###  Progress Tracking
- **Personalized Dashboard**: Track learning progress across algorithms
- **Quiz Performance**: Monitor scores and completion rates
- **Problem Completion**: Track solved coding challenges

###  Advanced Features
- **Search & Filter**: Find algorithms by name with real-time search
- **Sorting Options**: Sort by name, quiz count, or problem count
- **Category-based Learning**: Learn algorithms by categories (Sorting, Searching, Graph, etc.)
- **MongoDB Integration**: Persistent data storage for user progress

##  Tech Stack

### Frontend
- **JavaFX**: Modern UI framework for desktop applications
- **CSS Styling**: Custom gradients and animations
- **Mermaid.js**: Algorithm visualization diagrams

### Backend
- **Java 17+**: Core application logic
- **MongoDB**: NoSQL database for data persistence
- **MongoDB Java Driver**: Database connectivity

### Database Collections
- `algorithms`: Algorithm definitions and content
- `practice`: Practice problems and quizzes
- `user_progress`: User learning progress and analytics (created at runtime)

##  Installation Guide

### Prerequisites

Before installing, ensure you have the following installed on your system:

- Java Development Kit (JDK) 17 or higher
- MongoDB Atlas account (free tier available) or local MongoDB instance**
- Git for version control
- Maven 3.6+ for dependency management

### Step-by-Step Setup

1. Clone the Repository
   ```bash
   git clone https://github.com/your-username/dsa-simulator.git
   cd dsa-simulator
2. Configure MongoDB Connection
Update the connection string in all Java files:
  ```bash
  MongoClient mongoClient = MongoClients.create("your-mongodb-connection-string");
```
3. Import Data

Ensure your MongoDB has the required collections present in the folder : src/main/resources

4. Build and Run 
```bash
mvn clean compile
mvn javafx:run
```
## Usage

### Getting Started

1. **Launch the Application**: Run `HomePage.java` as the main class
2. **Explore Algorithms**: Click "Learn Algorithms" to browse available algorithms
3. **Start Practicing**: Use "Practice Problems" for quizzes and coding challenges
4. **Track Progress**: Monitor your learning journey in "View Progress"

### Learning Flow

1. **Browse**: View algorithms by category or search for specific ones
2. **Learn**: Study detailed explanations with code examples
3. **Practice**: Test knowledge with quizzes and coding problems
4. **Track**: Monitor progress and get personalized recommendations
