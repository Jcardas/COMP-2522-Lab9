import javafx.application.Application;
import javafx.stage.Stage;

public class QuizApp extends Application
{

//    getClass().getResources("/styles.css")

    @Override
    public void start(final Stage primaryStage)
    {
        primaryStage.setTitle("Quiz App");
        primaryStage.show();
    }

    public static void main(final String[] args)
    {
        launch(args);
        System.out.println("Success");
    }
}
