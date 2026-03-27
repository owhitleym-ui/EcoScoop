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
            this.ostream.print("Available Actions: \n 0. Return to Main Menu \n 1. Choose Article \n 2. Search Articles \n Response: ");

            final int actionInt = this.iscanner.nextInt();

            switch (actionInt){
                case (0):
                    running = false;
                    break;
                case (1):
                    listener.onChooseArticle();
                    break;
                case (2):
                    listener.onSearchArticles();
                    break;
            }

        }

        return;


    }


    // Run Article Display Methods
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

    // Run Article Search Methods

    @Override
    public void runSearchInput() {
        this.ostream.print("Enter search query: ");
        String query = this.iscanner.nextLine();

        this.ostream.print("Search Type:\n 1. Keyword\n 2. Tag\n 3. Author\n Response: ");
        final int searchType = this.iscanner.nextInt();
        this.iscanner.nextLine(); // consume newline

        String type;
        switch (searchType) {
            case 2:  type = "tag";     break;
            case 3:  type = "author";  break;
            default: type = "keyword"; break;
        }

        ArrayList<Article> results = listener.onSearchQuery(query, type);

        if (results.isEmpty()) {
            this.ostream.println("\nNo articles found.\n");
        } else {
            runDisplaySearchResults(results);
            runSortOptions(results);
        }
    }

    @Override
    public void runSearchArticles() {
        clearConsole();
        this.ostream.print("~~~~~~ Search Articles ~~~~~~\n\n");

        boolean running = true;

        while (running) {
            this.ostream.print("Search Type:\n 0. Return\n 1. Keyword\n 2. Tag\n 3. Author\n Response: ");
            final int searchType = this.iscanner.nextInt();
            this.iscanner.nextLine(); // consume newline

            if (searchType == 0) {
                running = false;
                break;
            }

            String type;
            switch (searchType) {
                case 2:  type = "tag";     break;
                case 3:  type = "author";  break;
                default: type = "keyword"; break;
            }

            this.ostream.print("Enter search query: ");
            String query = this.iscanner.nextLine();

            // Delegate to controller via listener — controller calls retriever
            ArrayList<Article> results = listener.onSearchQuery(query, type);

            if (results.isEmpty()) {
                this.ostream.println("\nNo articles found.\n");
            } else {
                runDisplaySearchResults(results);
                runSortOptions(results);
            }
        }
    }

    @Override
    public void runDisplaySearchResults(ArrayList<Article> results) {
        this.ostream.println("\n--- Search Results (" + results.size() + " found) ---\n");
        for (Article article : results) {
            this.ostream.println(article.getSummary());
            this.ostream.println("--------------------------------------------------");
        }
    }

    @Override
    public void runSortOptions(ArrayList<Article> results) {
        this.ostream.print("Sort by:\n 0. Skip\n 1. Relevance\n 2. Date\n 3. Rating\n 4. Trending\n Response: ");
        final int sortChoice = this.iscanner.nextInt();

        if (sortChoice == 0) return;

        String criteria;
        switch (sortChoice) {
            case 2:  criteria = "date";      break;
            case 3:  criteria = "rating";    break;
            case 4:  criteria = "trending";  break;
            default: criteria = "relevance"; break;
        }

        ArrayList<Article> sorted = listener.onSortResults(results, criteria);
        runDisplaySearchResults(sorted);
    }



}
