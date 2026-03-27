import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

public class CmdLineUI implements UI{
    private final Scanner iscanner = new Scanner(System.in);
    private final PrintStream ostream = System.out;
    private Listener listener;

    public CmdLineUI() { }

    // Set Listener
    @Override
    public void setListener(Listener listener) { this.listener = listener; }

    // Helper Visual Methods
    public void clearConsole() {
        this.ostream.print("\033[H\033[2J");
        this.ostream.flush();
    }

    // Full Run Method
    @Override
    public void runMainMenu() {
        this.ostream.print("~~~~~~ Welcome to EcoSCOOP! ~~~~~~\n\n");

        boolean running = true;
        while (running){
            this.ostream.print("Available Actions:\n 0. Exit Program \n 1. Article Tab \n Response:");
            final int actionInt = this.iscanner.nextInt();

            switch (actionInt){
                case (0):
                    running = false;
                    break;
                case (1):
                    listener.onViewArticleTab();
                    break;
            }

        }

        this.ostream.print("~~~~~~ Thank you for visiting EcoSCOOP! ~~~~~~\n\n");

    }

    // Run Navigation Methods
    @Override
    public void runArticleTab(){
        clearConsole();
        this.ostream.print("~~~~~~ Article Tab ~~~~~~\n\n");

        boolean running = true;

        while (running){
            listener.onDisplayArticleList();
            this.ostream.print("Available Actions: \n 0. Return to Main Menu \n 1. Choose Article \n Response: ");

            final int actionInt = this.iscanner.nextInt();

            switch (actionInt){
                case (0):
                    running = false;
                    break;
                case (1):
                    listener.onChooseArticle();
                    break;
            }

        }

        return;


    }


    // Run Article Tab Methods
    @Override
    public void runDisplayArticle(Article article){
        clearConsole();

        boolean running = true;

        while(running){
            this.ostream.print("Available Actions: \n 0. Return to Article List \n");
            this.ostream.print(article.printArticle());
            this.ostream.print("\n Available Actions: \n 0. Return to Article List \n Response: ");


            final int actionInt = this.iscanner.nextInt();

            switch (actionInt){
                case (0):
                    running = false;
                    break;
                case (1):
                    running = false;
                    break;
            }
        }

        return;

    }

    @Override
    public void runChooseArticle() {
        while(true) {
            this.ostream.print("\n Enter ID (0 for none): ");
            final int articleId = this.iscanner.nextInt();
            if (articleId == 0) {
                break;
            } else {
                this.listener.onGetArticle(articleId);
            }

        }

        listener.onDisplayArticleList();

    }

    @Override
    public void runDisplayArticleList(ArrayList<Article> articles){
        for (Article article : articles){
            this.ostream.println(article.getSummary());
            this.ostream.println("--------------------------------------------------");
        }

    }



}
