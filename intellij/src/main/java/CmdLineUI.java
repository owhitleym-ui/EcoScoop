import java.io.PrintStream;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Command-line implementation of the EcoScoop UI.
 * Uses numbered menus printed to the terminal and reads responses with a Scanner.
 * To switch to a different platform (e.g. Android), create a new class that implements UI.
 */
public class CmdLineUI implements UI {
    private final Scanner iscanner = new Scanner(System.in);
    private final PrintStream ostream = System.out;
    private Listener listener;

    public CmdLineUI() { }

    @Override
    public void setListener(Listener listener) { this.listener = listener; }

    // Reads an integer from input. Returns -1 and prints an error on bad input.
    private int readInt() {
        try {
            return iscanner.nextInt();
        } catch (InputMismatchException e) {
            iscanner.nextLine(); // clear the bad input from the buffer
            ostream.println("Invalid input. Please enter a number.");
            return -1;
        }
    }

    public void clearConsole() {
        this.ostream.print("\033[H\033[2J");
        this.ostream.flush();
    }

    @Override
    public void runMainMenu() {
        this.ostream.print("~~~~~~ Welcome to EcoSCOOP! ~~~~~~\n\n");

        boolean running = true;
        while (running) {
            this.ostream.print("Available Actions:\n 0. Exit Program\n 1. Article Tab\n Response: ");
            switch (readInt()) {
                case 0:
                    running = false;
                    break;
                case 1:
                    listener.onViewArticleTab();
                    break;
                default:
                    this.ostream.println("Invalid option, please try again.");
                    break;
            }
        }

        this.ostream.print("~~~~~~ Thank you for visiting EcoSCOOP! ~~~~~~\n\n");
    }

    @Override
    public void runArticleTab() {
        clearConsole();
        this.ostream.print("~~~~~~ Article Tab ~~~~~~\n\n");

        boolean running = true;
        while (running) {
            listener.onDisplayArticleList();
            this.ostream.print("Available Actions:\n 0. Return to Main Menu\n 1. Choose Article\n 2. Search Articles\n Response: ");
            switch (readInt()) {
                case 0:
                    running = false;
                    break;
                case 1:
                    listener.onChooseArticle();
                    break;
                case 2:
                    listener.onSearchArticles();
                    break;
                default:
                    this.ostream.println("Invalid option, please try again.");
                    break;
            }
        }
    }

    @Override
    public void runDisplayArticle(Article article) {
        clearConsole();
        this.ostream.print(article.printArticle());
        this.ostream.print("\n\nAvailable Actions:\n 0. Return to Article List\n Response: ");

        boolean running = true;
        while (running) {
            switch (readInt()) {
                case 0:
                    runSavePrompt(article);
                    running = false;
                    break;
                default:
                    this.ostream.println("Invalid option, please try again.");
                    this.ostream.print(" Response: ");
                    break;
            }
        }
    }

    // Called when the user returns from an article and asks if they want to save it.
    private void runSavePrompt(Article article) {
        boolean running = true;
        while (running) {
            this.ostream.print("\nSave this article to a folder?\n 0. No\n 1. Yes\n Response: ");
            switch (readInt()) {
                case 0:
                    running = false;
                    break;
                case 1:
                    this.ostream.print("Enter folder name: ");
                    String folderName = iscanner.next();
                    listener.onSaveToFolder(article.getId(), folderName);
                    this.ostream.println("Saved to folder '" + folderName + "'.");
                    running = false;
                    break;
                default:
                    this.ostream.println("Invalid option, please try again.");
                    break;
            }
        }
    }

    @Override
    public void runChooseArticle() {
        while (true) {
            this.ostream.print("\nEnter ID (0 to go back): ");
            int articleId = readInt();
            if (articleId == 0) {
                break;
            } else if (articleId == -1) {
                // readInt already printed the error
            } else if (articleId < 1 || articleId > listener.getArticleCount()) {
                this.ostream.println("Article ID not found. Please enter an ID between 1 and " + listener.getArticleCount() + ".");
            } else {
                this.listener.onGetArticle(articleId);
            }
        }
        listener.onDisplayArticleList();
    }

    @Override
    public void runDisplayArticleList(ArrayList<Article> articles) {
        for (Article article : articles) {
            this.ostream.println(article.getSummary());
            this.ostream.println("--------------------------------------------------");
        }
    }

    @Override
    public void runSearchInput() {
        this.ostream.print("Enter search query: ");
        String query = this.iscanner.nextLine();

        this.ostream.print("Search Type:\n 1. Keyword\n 2. Tag\n 3. Author\n Response: ");
        int searchType = readInt();
        this.iscanner.nextLine();

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
            int searchType = readInt();
            this.iscanner.nextLine(); // consume newline left by readInt

            if (searchType == 0) {
                running = false;
                break;
            }

            String type;
            switch (searchType) {
                case 2:  type = "tag";     break;
                case 3:  type = "author";  break;
                case -1: continue; // readInt already printed error
                default: type = "keyword"; break;
            }

            this.ostream.print("Enter search query: ");
            String query = this.iscanner.nextLine();

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
        int sortChoice = readInt();

        if (sortChoice == 0 || sortChoice == -1) return;

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
