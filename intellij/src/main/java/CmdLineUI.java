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

    // Full Run Method
    @Override
    public void runMainMenu() {
        this.ostream.print("~~~~~~ Welcome to EcoSCOOP! ~~~~~~\n\n");

        boolean running = true;
        while (running){
            this.ostream.print("Available Actions:\n 0. Exit Program \n 1. Article Tab");
            final int actionInt = this.iscanner.nextInt();

            switch (actionInt){
                case (0): running = false;
                case (1): listener.onViewArticleTab();
            }

        }

        this.ostream.print("~~~~~~ Thank you for visiting ECOScoop! ~~~~~~\n\n");

    }

    // Run Navigation Methods
    @Override
    public void runArticleTab(){
        this.ostream.print("~~~~~~ Article Tab ~~~~~~\n\n");

        boolean running = true;

        while (running){
            listener.onDisplayArticleList();
            this.ostream.print("Available Actions: \n 0. Return to Main Menu \n 1. Choose Article");

            final int actionInt = this.iscanner.nextInt();

            switch (actionInt){
                case (0): running = false;
                case (1):
                    listener.onChooseArticle();
            }

        }

        runMainMenu();


    }


    // Run Article Tab Methods

    @Override
    public void runDisplayArticle(Article article){

        while(true){
            this.ostream.print("Available Actions: 0. Return to Article List");
            this.ostream.print(article.getContent());

        }


    }

    @Override
    public void runChooseArticle() {
        while(true) {
            this.ostream.print("Enter ID (0 for none): ");
            final int articleId = this.iscanner.nextInt();
            if (articleId == 0) {
                break;
            }
            if (this.listener != null) {
                this.listener.onGetArticle(articleId);
            }
        }

        listener.onDisplayArticleList();

    }

    @Override
    public void runDisplayArticleList(ArrayList<Article> articles){
        for (Article article : articles){
            this.ostream.print(article);
        }

    }



}
