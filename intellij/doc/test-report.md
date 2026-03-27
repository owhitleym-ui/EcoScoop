# EcoScoop - Prototype Test Report

## Overview

This report describes the system tests performed on the EcoScoop prototype (Phase 2). All tests were run using the command-line interface by launching the app with `mvn exec:java -Dexec.mainClass="Controller"`. No unit tests were written for this iteration. Each test describes the inputs given, the expected output, and whether the actual output matched.

---

## Test 1 — App Launch and Feed Loading

**What we tested:** Starting the app and confirming that articles load from the RSS feeds (Grist and Carbon Brief).

**Input:** Run the app. No user input yet.

**Expected output:** The app prints a fetching message for each feed, shows how many articles were found per feed, and then displays the main menu with a welcome message.

**Actual output:**

```
Fetching: https://grist.org/feed/
  → 20 articles from Grist
Fetching: https://www.carbonbrief.org/feed/
  → 10 articles from Carbon Brief
~~~~~~ Welcome to EcoSCOOP! ~~~~~~

Available Actions:
 0. Exit Program
 1. Article Tab
 Response: 1
```

**Result:** __Pass__  / Fail

---

## Test 2 — Navigate to Article Tab

**What we tested:** Choosing option 1 from the main menu to enter the Article Tab.

**Input:** At the main menu, enter `1`.

**Expected output:** The console clears and displays the Article Tab header followed by a list of article summaries, each showing an ID, title, author, description snippet, source name, publish date, and tags. The available actions menu is shown below the list.

**Actual output:**

```
~~~~~~ Article Tab ~~~~~~

ID: 1 Iran was already running out of water. Then came the ‘war on infrastructure.’
[Frida Garza]
 
Drought, a legacy of overpumping, and now military strikes are driving the
country’s fragile water and food systems to the brink.
 
Grist -- Thu, 26 Mar 2026 08:45:00 +0000
[Energy, Food and Agriculture, International]
--------------------------------------------------
ID: 23 Factcheck: Nine false or misleading myths about North Sea oil and gas
[Carbon Brief Staff]
 
The Iran war has triggered another fossil-fuel energy crisis, with surging
global prices and increasing... The post Factcheck: Nine false or misleading
myths about North Sea oil and gas appeared first on Carbon Brief .
 
Carbon Brief -- Wed, 25 Mar 2026 16:10:07 +0000
[Energy, Factchecks, UK policy]
```

**Result:** __Pass__ / Fail

---

## Test 3 — Read an Article

**What we tested:** Choosing an article by ID and reading its full content.

**Input:** From the Article Tab, enter `1` to choose an article. When prompted for an ID, enter the ID of any article shown in the list (e.g. `1`).

**Expected output:** The console clears and displays the full article including title, author, body text word-wrapped at 80 characters (cut for shortness), source info, tags, and a like/dislike count of 0. A menu with option 0 to return is shown. 

**Actual output:**

```
Available Actions:
 0. Return to Main Menu
 1. Choose Article
 2. Search Articles
 Response: 1


Enter ID (0 to go back): 1
Iran was already running out of water. Then came the ‘war on infrastructure.’
[Frida Garza]
 
Last week, following attacks on critical energy and water facilities escalating,
the Israeli-U.S. war in Iran entered a new stage. “Now the war on infrastructure
has started,” said Kaveh Madani, a water researcher at the United Nations
University and former deputy vice president of Iran. On March 18, Israel struck
the South Pars gas field in Iran, the largest natural gas field in the world.
Iran is heavily dependent on South Pars for its energy supply; by some
estimates, the field accounts for 90 percent of the country’s domestic energy
use . The assault on South Pars kicked off a series of retaliatory attacks from
Iran on energy facilities throughout the region, including an aerial strike that
caused considerable damage to Ras Laffan, a sprawling liquified natural gas
facility in Qatar — the world’s largest LNG export hub. About one fifth of the
globe’s LNG supply comes from this plant, according to Bloomberg. Qatar’s energy
minister said the damage could take three to five years to repair . This
escalation in hostilities has added more pressure to an already fraught
situation affecting the energy industry in the Persian Gulf, which will continue
to have implications both regionally and worldwide. The price of Brent crude,
considered the global benchmark of crude oil prices, spiked after the South Pars
attack, reaching nearly $120 per barrel. It has since fallen a bit, to just
under $100 per barrel. Read Next The war in Iran could plunge the world into
hunger Ayurella Horn-Muller But another casualty of the attacks on
infrastructure is water security. Even before oil and gas facilities became
targets of war, water desalination plants in the Gulf were being struck. In a
region with scarce freshwater, these plants deliver clean drinking water to
...
 
Grist https://grist.org/food-and-agriculture/iran-was-already-running-out-of-water-then-came-the-war-on-infrastructure/ Thu, 26 Mar 2026 08:45:00 +0000
[Energy, Food and Agriculture, International]
 Likes: 0 | Dislikes: 0

Available Actions:
 0. Return to Article List
```

**Result:** __Pass__ / Fail

---

## Test 4 — Save an Article to a Folder

**What we tested:** Saving an article to a folder after reading it.

**Input:** After reading an article, enter `0` to return. When prompted with the save question, enter `1` (Yes). Enter a folder name, e.g. `climate`.

**Expected output:** The app prints `Saved to folder 'climate'.` and then returns to the article list without crashing.

**Actual output:**

```
Available Actions:
 0. Return to Article List
 Response: 0

Save this article to a folder?
 0. No
 1. Yes
 Response: 1
Enter folder name: Iran
Saved to folder 'Iran'.
```

**Result:** __Pass__ / Fail

---

## Test 5 — Decline to Save an Article

**What we tested:** Choosing not to save an article after reading it.

**Input:** After reading an article, enter `0` to return. When prompted with the save question, enter `0` (No).

**Expected output:** The app skips the save and returns directly to the article list.

**Actual output:**

```
[paste terminal output here]
```

**Result:** __Pass__ / Fail

---

## Test 6 — Search Articles by Keyword

**What we tested:** Searching for articles using a keyword.

**Input:** From the Article Tab, enter `2` for Search Articles. Choose `1` for Keyword. Enter a search term that should appear in at least one article, e.g. `climate`.

**Expected output:** A list of matching articles is displayed with a count of results found. Articles that contain the keyword in their title, description, author, or tags appear in the results.

**Actual output:**

```
Search Type:
 0. Return
 1. Keyword
 2. Tag
 3. Author
 Response: 1
Enter search query: Trump

--- Search Results (15 found) ---

ID: 1 Iran was already running out of water. Then came the ‘war on infrastructure.’
[Frida Garza]
 
Drought, a legacy of overpumping, and now military strikes are driving the
country’s fragile water and food systems to the brink.
 
Grist -- Thu, 26 Mar 2026 08:45:00 +0000
[Energy, Food and Agriculture, International]
--------------------------------------------------
ID: 4 The frantic, high-tech fight to stop climate-fueled dengue fever
[Zoya Teirstein]
 
Scientists in Brazil and Peru may have found a way to beat mosquitoes at their
own game. The U.S. may soon need to do the same.
 
Grist -- Wed, 25 Mar 2026 08:45:00 +0000
[Health, International, Solutions]
--------------------------------------------------
ID: 6 Trump’s $1B payoff to stop offshore wind is even stranger than it sounds
[Jake Bittle, Rebecca Egan McCarthy]
 
The government is paying TotalEnergies to halt a wind farm it isn’t building, in
exchange for fossil fuel investments it’s already making.
 
Grist -- Wed, 25 Mar 2026 08:00:00 +0000
[Energy, Politics]
--------------------------------------------------
ID: 8 Utah Republicans see storing nuclear waste as a ‘once in a lifetime opportunity’
[Leia Larsen, Matt Ward]
 
Some think a Trump administration plan is a chance to boost communities that
hemorrhaged jobs after coal plants closed.
 
Grist -- Tue, 24 Mar 2026 08:30:00 +0000
[Energy]
--------------------------------------------------
ID: 9 This $400B Biden climate program is surviving the Trump administration
[Jake Bittle]
 
Trump’s energy secretary says he’s canceled billions of dollars in clean energy
loans. The Biden official who made those loans says the number is “fake.”
 
Grist -- Mon, 23 Mar 2026 08:45:00 +0000
[Energy, Politics]
--------------------------------------------------

```

**Result:** __Pass__ / Fail

---

## Test 7 — Search Returns No Results

**What we tested:** Searching for a term that does not appear in any article.

**Input:** From the Article Tab, enter `2` for Search Articles. Choose `1` for Keyword. Enter a nonsense term, e.g. `zzzzzzz`.

**Expected output:** The app prints `No articles found.` and returns to the search menu without crashing.

**Actual output:**

```
Search Type:
 0. Return
 1. Keyword
 2. Tag
 3. Author
 Response: 1
Enter search query: fjekwfhwef
```

**Result:** __Pass__ / Fail

---

## Test 8 — Invalid Menu Input (Wrong Number)

**What we tested:** Entering a menu option that does not exist.

**Input:** From the main menu, enter a number that is not listed, e.g. `9`.

**Expected output:** The app prints `Invalid option, please try again.` and shows the menu again without crashing.

**Actual output:**

```
Available Actions:
 0. Exit Program
 1. Article Tab
 Response: 9
Invalid option, please try again.
Available Actions:
 0. Exit Program
 1. Article Tab
```

**Result:** __Pass__ / Fail

---

## Test 9 — Invalid Menu Input (Not a Number)

**What we tested:** Entering a non-number where the app expects a number.

**Input:** From the main menu, type `abc` and press enter.

**Expected output:** The app prints `Invalid input. Please enter a number.` and shows the menu again without crashing.

**Actual output:**

```
Available Actions:
 0. Exit Program
 1. Article Tab
 Response: abc
Invalid input. Please enter a number.
Invalid option, please try again.
Available Actions:
 0. Exit Program
 1. Article Tab
```

**Result:** __Pass__ / Fail

---

## Test 10 — Invalid Article ID

**What we tested:** Entering an article ID that does not exist.

**Input:** From the Article Tab, enter `1` to choose an article. When prompted for an ID, enter a number that is not in the list, e.g. `9999`.

**Expected output:** The app does not crash. It either skips silently or shows an error, and returns to the ID prompt.

**Actual output:**

```
Available Actions:
 0. Return to Main Menu
 1. Choose Article
 2. Search Articles
 Response: 1

Enter ID (0 to go back): 4567
Article ID not found. Please enter an ID between 1 and 30.
```

**Result:** __Pass__ / Fail

---

## Test 11 — Return to Main Menu from Article Tab

**What we tested:** Navigating back to the main menu from the Article Tab.

**Input:** From the Article Tab, enter `0`.

**Expected output:** The app returns to the main menu and displays it again.

**Actual output:**

```
Available Actions:
 0. Return to Main Menu
 1. Choose Article
 2. Search Articles
 Response: 0
Available Actions:
 0. Exit Program
 1. Article Tab
```

**Result:** __Pass__ / Fail

---

## Test 12 — Exit the App

**What we tested:** Exiting the application cleanly.

**Input:** From the main menu, enter `0`.

**Expected output:** The app prints `~~~~~~ Thank you for visiting EcoSCOOP! ~~~~~~` and exits without errors.

**Actual output:**

```
Available Actions:
 0. Exit Program
 1. Article Tab
 Response: 0
~~~~~~ Thank you for visiting EcoSCOOP! ~~~~~~

```

**Result:** __Pass__ / Fail

