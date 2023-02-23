import java.util.*;

// Guest class for everyone except the counter
class Guest implements Runnable 
{
    // Unique ID for the guests
    private final int id;
    // Randomizer
    private final Random r = new Random();
    // Instance of the main MinotaurParty class so we can access the global information
    private final MinotaurParty minotaur;
    // Flag to see if this person has eaten a cupcake yet or not
    private boolean hasEaten = false;

    // Simple constructor
    public Guest(int id, MinotaurParty minotaur) 
    {
        this.id = id;
        this.minotaur = minotaur;
    }

    // Overridden run function
    @Override
    public void run() 
    {
        // Keep going until the counter reaches N-1, since the counter won't eat one
        while (minotaur.getCount() != minotaur.NUM_GUESTS - 1) 
        {
            // Wait your turn....sleep for a random time
            try
            {
                Thread.sleep(r.nextInt(5000));
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }

            // See if the maze is actually open and there's no one in there already
            if (minotaur.isOpen()) 
            {
                synchronized (minotaur) 
                {
                    if (minotaur.isOpen())
                    {
                        // Enter the maze and close it off for others
                        System.out.println("Guest " + id + " enters the maze.");
                        minotaur.close();
                        // If the cupcake is present and we haven't eaten one already, eat it
                        if (minotaur.isCupcakeThere())
                        {
                            // Haven't eaten a cupcake yet
                            if (hasEaten == false)
                            {
                                // Eat it and mark that this guest has eaten a cupcake
                                minotaur.eat();
                                System.out.println("Guest " + id + " eats the cupcake");
                                hasEaten = true;
                            }
                            // The guest should skip past if they've already eaten one
                            else
                            {
                                System.out.println("Guest " + id + " skips past the cupcake");
                            }
                        }
                        // If there's no cupcake, just walk away, only the counter can request cupcakes.
                        else
                        {
                            minotaur.open();
                            System.out.println("Guest " + id + " found no cupcake and left");
                        }
                        // Mark the maze as open
                        minotaur.open();
                    }
                }
            }
            // The maze is closed, keep going
            else
            {
                System.out.println("Guest " + id + " tried to enter the maze but someone was already there!");
            }
        }
    }
}

// Counter class for unique functionality of the counter
class Counter implements Runnable 
{
    // Unique ID
    private final int id;
    // Randomizer
    private final Random r = new Random();
    // Instance of the main MinotaurParty class so we can access the global information
    private final MinotaurParty minotaur;
    // Flag to see if this person has eaten a cupcake yet or not
    private boolean hasEaten = false;

    // Simple constructor
    public Counter(int id, MinotaurParty minotaur) 
    {
        this.id = id;
        this.minotaur = minotaur;
    }

    // Overridden run function
    @Override
    public void run() 
    {
        // Keep going until the counter reaches N-1
        while (minotaur.numEaten() != minotaur.NUM_GUESTS - 1) 
        {
            // Sleep for a random amount of time to wait your turn
            try
            {
                Thread.sleep(r.nextInt(5000));
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }

            // Check if the maze is open or not
            if (minotaur.isOpen()) 
            {
                synchronized (minotaur) 
                {
                    if (minotaur.isOpen())
                    {
                        // Counter enters the maze
                        System.out.println("Counter enters the maze.");
                        minotaur.close();
                        // If the cupcake is there, the counter should just walk away
                        if (minotaur.isCupcakeThere())
                        {
                            minotaur.open();
                        }
                        // The counter replaces and increments the counter
                        else
                        {
                            // Replace the cupcake
                            minotaur.replace();
                            // Maze will now be opened
                            minotaur.open();
                            // Increment amount of times the cupcake was replaced
                            minotaur.addCounter();
                            System.out.println("Counter replaced the cupcake " + minotaur.getCount() + " times");
                        }
                        // Open the maze
                        minotaur.open();
                    }
                }
            }
            // Counter entered the maze but someone was already in there
            else
            {
                System.out.println("Counter tried to enter the maze but someone was already there!");
            }
        }
    }
}

public class MinotaurParty 
{
    // Number of guests in the party
    public static final int NUM_GUESTS = 5;
    // Randomizer
    private static final Random r = new Random();

    // Flags for whether the maze is open and whether there is a cupcake at the end
    private boolean cupcake = true;
    private boolean isMazeOpen = true;

    // Counters for how many times the counter replaces and how many times a guest eats the cupcake
    private static int counter = 0;
    private int nEaten = 0;

    // Gooooo!!!!
    public static void main(String[] args) 
    {
        // Create minotaur party instance
        MinotaurParty minotaur = new MinotaurParty();
        // Threads
        Thread [] threads = new Thread[NUM_GUESTS];

        // Go through and create/start the threads
        for (int i = 0; i < NUM_GUESTS; i++) 
        {
            // Only the 1st thread is the counter, the others are just guests
            if (i == 0)
            {
                Counter counter = new Counter(i, minotaur);
                threads[i] = new Thread(counter);
            }
            else
            {
                Guest guest = new Guest(i, minotaur);
                threads[i] = new Thread(guest);
            }
            // Goooo!!!
            threads[i].start();
        }
        
        // Join the threads (wait for them to finish)
        for (int i = 0; i < NUM_GUESTS; i++)
        {
            try
            {
                threads[i].join();
            }
            catch (Exception e)
            {}
        }
        // Announce to the minotaur that everyone has visited!
        System.out.println("Counter says: 'Mr. Minotaur, we have all visited the labyrinth!! Yay!!'");
    }

    // Increment counter
    public void addCounter() 
    {
        counter++;
    }

    // Get count
    public int getCount()
    {
        return counter;
    }

    // Simulate replacing the cupcake
    public void replace() 
    {
        this.cupcake = true;
    }

    // Simulate eating the cupcake
    public void eat()
    {
        nEaten++;
        this.cupcake = false;
    }

    // Check if the cupcake is present
    public boolean isCupcakeThere()
    {
        return cupcake;
    }

    // Check if the maze is open
    public boolean isOpen()
    {
        return isMazeOpen;
    }

    // Close the maze
    public void close()
    {
        this.isMazeOpen = false;
    }

    // Open the maze
    public void open()
    {
        this.isMazeOpen = true;
    }

    // Get number of people who ate cupcakes
    public int numEaten()
    {
        return nEaten;
    }
}