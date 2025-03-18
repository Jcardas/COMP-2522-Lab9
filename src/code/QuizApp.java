import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * The {@code QuizApp} class represents a simple JavaFX-based quiz application.
 * It allows users to answer questions, track their score, and review missed questions.
 *
 * @author Justin Cardas
 */
public class QuizApp extends Application
{
    private List<String[]> questions            = new ArrayList<>();
    private int            currentQuestionIndex = 0;
    private int            score                = 0;
    private List<String[]> missedQuestions      = new ArrayList<>();

    private Label     questionLabel;
    private TextField answerField;
    private Button    submitButton;
    private Button    startButton;
    private Label     scoreLabel;
    private TextArea  missedTextArea;

    /**
     * The main entry point for the JavaFX application.
     *
     * @param primaryStage the primary stage for this application
     */
    @Override
    public void start(Stage primaryStage)
    {
        loadQuestions();

        // UI Components
        questionLabel = new Label("Press 'Start Quiz' to begin.");
        answerField   = new TextField();
        submitButton  = new Button("Submit Answer");
        startButton   = new Button("Start Quiz");
        scoreLabel    = new Label("Score: 0/10");

        // TextArea for missed questions
        missedTextArea = new TextArea();
        missedTextArea.setEditable(false);
        missedTextArea.setWrapText(true);
        missedTextArea.setPrefHeight(150); // Set height to allow scrolling

        // Layout
        VBox layout = new VBox(10, startButton, questionLabel, answerField, submitButton, scoreLabel, missedTextArea);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        Scene scene = new Scene(layout, 400, 400);

        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        // Event Handlers
        startButton.setOnAction(e -> startQuiz());
        submitButton.setOnAction(e -> checkAnswer());
        answerField.setOnAction(e -> checkAnswer()); // Handle ENTER key press

        // Initial UI state
        submitButton.setDisable(true);
        answerField.setDisable(true);
        missedTextArea.setVisible(false); // Hide at start

        primaryStage.setScene(scene);
        primaryStage.setTitle("JavaFX Quiz App");
        primaryStage.show();
    }

    /*
     * Loads quiz questions from a file and stores them in a list.
     * The questions are formatted as {@code "Question|Answer"} pairs.
     */
    private void loadQuestions()
    {
        try
        {
            List<String> lines = Files.readAllLines(Paths.get("src/resources/quiz.txt"));
            for(String line : lines)
            {
                String[] parts = line.split("\\|");
                if(parts.length == 2)
                {
                    questions.add(parts);
                }
            }
            Collections.shuffle(questions); // Randomize question order
        } catch(IOException e)
        {
            System.out.println("Error loading questions: " + e.getMessage());
        }
    }

    /*
     * Starts the quiz, resetting the score and shuffling the questions.
     */
    private void startQuiz()
    {
        // Reset quiz
        currentQuestionIndex = 0;
        score                = 0;
        missedQuestions.clear();
        Collections.shuffle(questions);

        // UI updates
        scoreLabel.setText("Score: 0/10");
        questionLabel.setText(questions.get(currentQuestionIndex)[0]);
        missedTextArea.clear();
        missedTextArea.setVisible(false); // Hide missed questions at restart
        submitButton.setDisable(false);
        answerField.setDisable(false);
        startButton.setDisable(true);
        answerField.clear();
    }

    /*
     * Checks the user's answer against the correct answer.
     * Updates the score and tracks missed questions.
     */
    private void checkAnswer()
    {
        if(currentQuestionIndex >= 10)
        {
            return;
        }

        String userAnswer    = answerField.getText().trim();
        String correctAnswer = questions.get(currentQuestionIndex)[1].trim();

        if(userAnswer.equalsIgnoreCase(correctAnswer))
        {
            score++;
        } else
        {
            missedQuestions.add(questions.get(currentQuestionIndex)); // Track missed question
        }

        currentQuestionIndex++;
        answerField.clear();

        if(currentQuestionIndex < 10)
        {
            questionLabel.setText(questions.get(currentQuestionIndex)[0]);
        } else
        {
            showResults();
        }

        scoreLabel.setText("Score: " + score + "/10");
    }

    /*
     * Displays the final quiz results, including the user's score
     * and any missed questions.
     */
    private void showResults()
    {
        StringBuilder missedText = new StringBuilder();
        if(!missedQuestions.isEmpty())
        {
            missedText.append("Missed Questions:\n\n");
            for(String[] q : missedQuestions)
            {
                missedText.append("Q: ").append(q[0]).append("\nA: ").append(q[1]).append("\n\n");
            }
        } else
        {
            missedText.append("Great job! You got everything right!");
        }

        questionLabel.setText("Quiz Over! Your score: " + score + "/10");
        missedTextArea.setText(missedText.toString());
        missedTextArea.setVisible(true); // Show missed questions
        submitButton.setDisable(true);
        answerField.setDisable(true);
        startButton.setDisable(false);
    }

    // Main
    public static void main(String[] args)
    {
        launch(args);
    }
}
