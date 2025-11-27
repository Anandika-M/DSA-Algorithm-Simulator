package com.dsa.ui;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.FindIterable;
import org.bson.Document;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.ArrayList;
import java.util.List;

public class ViewVisualizationPage {

    private final Stage stage;
    private MongoCollection<Document> collection;
    private static Dotenv dotenv;

    static {
        dotenv = Dotenv.configure()
                .directory(System.getProperty("user.dir"))
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();
    }

    public ViewVisualizationPage(Stage stage) {
        this.stage = stage;
        initializeDatabase();
    }

    private void initializeDatabase() {
        try {
            String uri = dotenv.get("MONGODB_URI");
            String databaseName = dotenv.get("DATABASE_NAME");

            if (uri == null || uri.isEmpty()) {
                throw new IllegalStateException("MONGODB_URI not found in environment variables");
            }
            if (databaseName == null || databaseName.isEmpty()) {
                throw new IllegalStateException("DATABASE_NAME not found in environment variables");
            }

            MongoClient mongoClient = MongoClients.create(uri);
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            collection = database.getCollection("algorithms");
            
            System.out.println("✅ Connected to MongoDB for visualizations");
        } catch (Exception e) {
            System.err.println("Failed to connect to MongoDB: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void show() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #667eea, #764ba2);");

        // Header section
        Label title = new Label("Algorithm Visualizations");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setStyle("-fx-text-fill: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0.3, 2, 2);");

        Label subtitle = new Label("Run interactive simulations of algorithms");
        subtitle.setFont(Font.font("System", FontWeight.NORMAL, 14));
        subtitle.setStyle("-fx-text-fill: #e0e0e0;");

        VBox headerBox = new VBox(5, title, subtitle);
        headerBox.setAlignment(Pos.CENTER);

        // Loading indicator
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setVisible(true);

        // ListView for visualizations
        ListView<VisualizationData> listView = new ListView<>();
        listView.setPrefHeight(400);
        listView.setMaxWidth(650);
        listView.setStyle("-fx-background-color: transparent; -fx-border-color: rgba(255,255,255,0.2); " +
                         "-fx-border-radius: 10; -fx-background-radius: 10;");

        ObservableList<VisualizationData> visualizations = FXCollections.observableArrayList();

        // Load visualizations from MongoDB
        Thread loadDataThread = new Thread(() -> {
            List<VisualizationData> vizList = loadVisualizationsFromMongoDB();
            
            javafx.application.Platform.runLater(() -> {
                visualizations.setAll(vizList);
                progressIndicator.setVisible(false);
            });
        });
        
        loadDataThread.setDaemon(true);
        loadDataThread.start();

        // Custom cell factory for visualization cards
        listView.setCellFactory(param -> new ListCell<VisualizationData>() {
            private final StackPane container = new StackPane();
            private final VBox content = new VBox();
            private final Label nameLabel = new Label();
            private final Label categoryLabel = new Label();
            private final Label typeLabel = new Label();
            private final Label descriptionLabel = new Label();
            private final Button runButton = new Button("Run Visualization");
            
            {
                content.setSpacing(8);
                content.setPadding(new Insets(15));
                
                nameLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
                nameLabel.setStyle("-fx-text-fill: #2c3e50;");
                
                categoryLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
                categoryLabel.setStyle("-fx-text-fill: #7f8c8d;");
                
                typeLabel.setFont(Font.font("System", FontWeight.BOLD, 11));
                typeLabel.setStyle("-fx-text-fill: #e74c3c;");
                
                descriptionLabel.setFont(Font.font("System", 12));
                descriptionLabel.setStyle("-fx-text-fill: #34495e;");
                descriptionLabel.setWrapText(true);
                descriptionLabel.setMaxWidth(600);
                
                runButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
                runButton.setOnMouseEntered(e -> runButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;"));
                runButton.setOnMouseExited(e -> runButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;"));
                
                runButton.setOnAction(e -> {
                    VisualizationData viz = getItem();
                    if (viz != null) {
                        launchVisualization(viz);
                    }
                });
                
                content.getChildren().addAll(nameLabel, categoryLabel, typeLabel, descriptionLabel, runButton);
                container.getChildren().add(content);
                container.setPadding(new Insets(5));
            }

            @Override
            protected void updateItem(VisualizationData visualization, boolean empty) {
                super.updateItem(visualization, empty);
                
                if (empty || visualization == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    nameLabel.setText(visualization.getName());
                    categoryLabel.setText(visualization.getCategory());
                    typeLabel.setText("Type: " + visualization.getType());
                    descriptionLabel.setText(visualization.getDescription());
                    
                    // Card styling
                    String[] cardColors = {
                        "-fx-background-color: linear-gradient(to right, #ffffff, #f8f9fa); -fx-background-radius: 12; -fx-border-color: #e9ecef; -fx-border-radius: 12; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0.3, 2, 2);",
                        "-fx-background-color: linear-gradient(to right, #f8f9fa, #ffffff); -fx-background-radius: 12; -fx-border-color: #e9ecef; -fx-border-radius: 12; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0.3, 2, 2);"
                    };
                    
                    int colorIndex = getIndex() % 2;
                    content.setStyle(cardColors[colorIndex]);
                    
                    // Hover effect
                    setOnMouseEntered(e -> {
                        content.setStyle("-fx-background-color: linear-gradient(to right, #e3f2fd, #bbdefb); " +
                                       "-fx-background-radius: 12; -fx-border-color: #90caf9; -fx-border-radius: 12; " +
                                       "-fx-border-width: 2; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 12, 0.5, 3, 3);");
                    });
                    
                    setOnMouseExited(e -> {
                        content.setStyle(cardColors[colorIndex]);
                    });
                    
                    setGraphic(container);
                    setText(null);
                }
            }
        });

        listView.setItems(visualizations);

        // Back button
        Button backBtn = createBackButton();
        
        // Results count
        Label countLabel = new Label("Loading visualizations...");
        countLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
        countLabel.setStyle("-fx-text-fill: #e0e0e0;");

        visualizations.addListener((javafx.collections.ListChangeListener.Change<? extends VisualizationData> c) -> {
            countLabel.setText("Found " + visualizations.size() + " visualizations");
        });

        VBox contentBox = new VBox(15, headerBox, countLabel, progressIndicator, listView, backBtn);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setMaxWidth(700);

        root.getChildren().add(contentBox);

        Scene scene = new Scene(root, 800, 700);
        stage.setScene(scene);
        stage.setTitle("Algorithm Visualizations - DSA Simulator");
    }

    private List<VisualizationData> loadVisualizationsFromMongoDB() {
        List<VisualizationData> visualizations = new ArrayList<>();
        
        if (collection == null) {
            // Add default visualizations if DB connection fails
            addDefaultVisualizations(visualizations);
            return visualizations;
        }

        try {
            FindIterable<Document> algoDocuments = collection.find();
            
            for (Document doc : algoDocuments) {
                Document algorithmDoc = doc.get("algorithm", Document.class);
                Document metadata = doc.get("metadata", Document.class);
                Document visualization = doc.get("visualization", Document.class);
                
                if (visualization != null && visualization.getBoolean("available", false)) {
                    String id = algorithmDoc.getString("id");
                    String name = algorithmDoc.getString("name");
                    String category = algorithmDoc.getString("category");
                    String description = algorithmDoc.getString("description");
                    
                    String type = visualization.getString("type");
                    String className = visualization.getString("className");
                    
                    visualizations.add(new VisualizationData(id, name, category, description, type, className));
                }
            }
            
            // If no visualizations found in DB, add defaults
            if (visualizations.isEmpty()) {
                addDefaultVisualizations(visualizations);
            }
            
            System.out.println("✅ Loaded " + visualizations.size() + " visualizations from MongoDB");
        } catch (Exception e) {
            System.err.println("❌ Error loading visualizations from MongoDB: " + e.getMessage());
            addDefaultVisualizations(visualizations);
        }
        
        return visualizations;
    }

    private void addDefaultVisualizations(List<VisualizationData> visualizations) {
        // Add default sorting visualizations
        visualizations.add(new VisualizationData(
            "bubble-sort", "Bubble Sort", "Sorting", 
            "Visualize how bubble sort works by repeatedly swapping adjacent elements",
            "Sorting", "com.dsa.simulator.sorting.BubbleSortVisualizer"
        ));
        
        visualizations.add(new VisualizationData(
            "quick-sort", "Quick Sort", "Sorting", 
            "Visualize the divide and conquer approach of quick sort",
            "Sorting", "com.dsa.simulator.sorting.QuickSortVisualizer"
        ));
        
        visualizations.add(new VisualizationData(
            "merge-sort", "Merge Sort", "Sorting", 
            "Visualize the merging process in merge sort algorithm",
            "Sorting", "com.dsa.simulator.sorting.MergeSortVisualizer"
        ));
        
        // Add graph algorithm visualizations
        visualizations.add(new VisualizationData(
            "bfs", "Breadth First Search", "Graph Algorithms", 
            "Visualize BFS traversal on graphs level by level",
            "Graph", "com.dsa.simulator.graph.BFSVisualizer"
        ));
        
        visualizations.add(new VisualizationData(
            "dfs", "Depth First Search", "Graph Algorithms", 
            "Visualize DFS traversal exploring as far as possible along each branch",
            "Graph", "com.dsa.simulator.graph.DFSVisualizer"
        ));
    }

    private void launchVisualization(VisualizationData visualization) {
        try {
            System.out.println("🚀 Launching visualization: " + visualization.getClassName());
            
            // Use reflection to launch the visualization
            Class<?> vizClass = Class.forName(visualization.getClassName());
            javafx.application.Application vizApp = (javafx.application.Application) vizClass.getDeclaredConstructor().newInstance();
            
            // Create a new stage for the visualization
            Stage vizStage = new Stage();
            vizStage.setTitle(visualization.getName() + " - DSA Visualizer");
            
            // Start the visualization application
            vizApp.start(vizStage);
            
        } catch (Exception e) {
            System.err.println("❌ Failed to launch visualization: " + e.getMessage());
            e.printStackTrace();
            
            // Show error message
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Visualization Error");
            alert.setHeaderText("Failed to launch " + visualization.getName());
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private Button createBackButton() {
        Button backBtn = new Button("← Back to Home");
        backBtn.setPrefWidth(150);
        backBtn.setPrefHeight(35);
        backBtn.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
        
        String normalStyle = "-fx-background-color: #6c757d; -fx-text-fill: white; -fx-background-radius: 6; " +
                           "-fx-border-radius: 6; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 3, 0.2, 1, 1);";
        
        String hoverStyle = "-fx-background-color: #5a6268; -fx-text-fill: white; -fx-background-radius: 6; " +
                          "-fx-border-radius: 6; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0.3, 2, 2);";
        
        backBtn.setStyle(normalStyle);
        backBtn.setOnMouseEntered(e -> backBtn.setStyle(hoverStyle));
        backBtn.setOnMouseExited(e -> backBtn.setStyle(normalStyle));
        
        backBtn.setOnAction(e -> {
            HomePage home = new HomePage();
            try {
                home.start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        
        return backBtn;
    }

    // Data class to hold visualization information
    public static class VisualizationData {
        private final String id;
        private final String name;
        private final String category;
        private final String description;
        private final String type;
        private final String className;

        public VisualizationData(String id, String name, String category, String description, String type, String className) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.description = description;
            this.type = type;
            this.className = className;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public String getCategory() { return category; }
        public String getDescription() { return description; }
        public String getType() { return type; }
        public String getClassName() { return className; }
    }
}