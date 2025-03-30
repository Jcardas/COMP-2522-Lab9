import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

// Timer
import javafx.animation.AnimationTimer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * The {@code QuizApp} class represents a simple JavaFX-based quiz application.
 * It allows users to answer questions, track their score, and review missed questions.
 *
 * @author Justin Cardas
 * @author Mohammad Sadeghi
 * @author Ted Ip
 * @version 1.0
 */
public class QuizApp extends Application
{

    final private List <String[]> questions            = new ArrayList <>();
    private       int             currentQuestionIndex = 0;
    private       int             score                = 0;
    final private List <String[]> missedQuestions      = new ArrayList <>();

    private static long time = 0;

    private Label     questionLabel;
    private TextField answerField;
    private Button    submitButton;
    private Button    startButton;
    private Label     scoreLabel;
    private TextArea  missedTextArea;

    // Timer
    private Label          timerLabel;
    private AnimationTimer timer;

    /**
     * The main entry point for the JavaFX application.
     *
     * @param primaryStage the primary stage for this application
     */
    @Override
    public void start(final Stage primaryStage)
    {
        loadQuestions();

        // UI Components

        // Timer
        timerLabel = new Label("Time: 0");


        timer = new AnimationTimer()
        {
            private long timestamp;
            private long fraction = 0;

            @Override
            public void start()
            {
                // reset the timer
                time      = 0;
                timestamp = System.currentTimeMillis() - fraction;
                super.start();

            }

            @Override
            public void stop()
            {
                super.stop();
                fraction = System.currentTimeMillis() - timestamp;
            }

            @Override

            public void handle(final long now)
            {
                long newTime = System.currentTimeMillis();
                if (timestamp + 1000 <= newTime)
                {
                    long deltaT = (newTime - timestamp) / 1000;
                    time += deltaT;
                    timestamp += 1000 * deltaT;
                    timerLabel.setText("Time: " + time);
                }
            }
        };


        questionLabel = new

                Label("Press 'Start Quiz' to begin.");
        questionLabel.setWrapText(true);

        startButton = new

                Button("Start Quiz");

        answerField = new

                TextField();

        submitButton = new

                Button("Submit Answer");

        scoreLabel = new

                Label("Score: 0/10");

        // TextArea for missed questions
        missedTextArea = new

                TextArea();
        missedTextArea.setEditable(false);
        missedTextArea.setWrapText(true);

        // Layout
        VBox layout = new VBox(10, timerLabel, questionLabel, answerField, submitButton, startButton, scoreLabel, missedTextArea);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        Scene scene = new Scene(layout, 400, 400);

        scene.getStylesheets().

             add(getClass().

                         getResource("/styles.css").

                         toExternalForm());

        // Event Handlers
        startButton.setOnAction(e ->

                                        startQuiz());
        submitButton.setOnAction(e ->

                                         checkAnswer());
        answerField.setOnAction(e ->

                                        checkAnswer()); // Handle ENTER key press

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
            final List <String> lines = Files.readAllLines(Paths.get("src/resources/quiz.txt"));
            for (String line : lines)
            {
                if (questions.size() >= 10) break; // Load max 10 questions

                if (line.trim().isEmpty()) continue; // Skip empty lines

                final String[] parts = line.split("\\|");
                if (parts.length == 2 && ! parts[0].trim().isEmpty() && ! parts[1].trim().isEmpty())
                {
                    questions.add(parts);
                } else
                {
                    System.out.println("Skipping malformed line: " + line);
                }
            }

            System.out.println("Loaded " + questions.size() + " valid questions (max 10).");

        } catch (IOException e)
        {
            System.out.println("Error loading questions: " + e.getMessage());
        }
    }


    /*
     * Starts the quiz, resetting the score and shuffling the questions.
     */
    private void startQuiz()
    {
        if (questions.isEmpty())
        {
            questionLabel.setText("No valid questions found. Please check your quiz.txt file.");
            return;
        }

        timer.start();

        // Reset quiz
        currentQuestionIndex = 0;
        score                = 0;
        missedQuestions.clear();
        Collections.shuffle(questions);

        scoreLabel.setText("Score: 0/" + questions.size());
        questionLabel.setText(questions.get(currentQuestionIndex)[0]);
        missedTextArea.clear();
        missedTextArea.setVisible(false);
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
        if (currentQuestionIndex >= questions.size())
        {
            return;
        }

        final String userAnswer    = answerField.getText().trim();
        final String correctAnswer = questions.get(currentQuestionIndex)[1].trim();

        if (userAnswer.equalsIgnoreCase(correctAnswer))
        {
            score++;
        } else
        {
            missedQuestions.add(questions.get(currentQuestionIndex));
        }

        currentQuestionIndex++;
        answerField.clear();

        if (currentQuestionIndex < questions.size())
        {
            questionLabel.setText(questions.get(currentQuestionIndex)[0]);
        } else
        {
            showResults();
        }

        scoreLabel.setText("Score: " + score + "/" + questions.size());
    }

    /*
     * Displays the final quiz results, including the user's score
     * and any missed questions.
     */
    private void showResults()
    {
        timer.stop();

        final StringBuilder missedText = new StringBuilder();
        if (! missedQuestions.isEmpty())
        {
            missedText.append("Time: ").append(time).append("\n");
            missedText.append("Missed Questions:\n\n");
            for (String[] q : missedQuestions)
            {
                missedText.append("Q: ").append(q[0]).append("\nA: ").append(q[1]).append("\n\n");
            }
        } else
        {
            missedText.append("Great job! You got everything right!");
        }

        questionLabel.setText("Quiz Over! Your score: " + score + "/" + questions.size());
        missedTextArea.setText(missedText.toString());
        missedTextArea.setVisible(true);
        submitButton.setDisable(true);
        answerField.setDisable(true);
        startButton.setDisable(false);
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
