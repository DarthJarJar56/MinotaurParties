import java.util.*;

// Guest class representing people
class Guest implements Runnable 
{
    // Unique Guest ID
    private final int id;
    private final Random r = new Random();
    private final MinotaurVase minotaur;

    // Guest constructor that takes an instance of a minotaur
    public Guest(int id, MinotaurVase minotaur) 
    {
        this.id = id;
        this.minotaur = minotaur;
    }

    // Overridden run method
    @Override
    public void run() 
    {
        // Keep going until the guest leaves
        while (true) 
        {
            // Sleep for a random amount of time, simulating waiting for their turn
            try
            {
                Thread.sleep(r.nextInt(5000));
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }

            // Check if the room is available or not
            if (minotaur.isAvailable()) 
            {
                synchronized (minotaur) 
                {
                    if (minotaur.isAvailable()) 
                    {
                        // If its available, go into the room and set availability to busy
                        minotaur.setAvailable(false);
                        System.out.println("Guest " + id + " enters the room.");

                        // Sleep for a random amount of time simulating admiring the vase
                        try 
                        {
                            System.out.println("Guest " + id + " is admiring the vase now.");
                            Thread.sleep(r.nextInt(5000));
                        } 
                        catch (InterruptedException e) 
                        {
                            e.printStackTrace();
                        }

                        // Add this guest to the guests who have already seen the vase
                        minotaur.addSeen(id);
                        System.out.println("Guest " + id + " sees the vase and leaves the room.");
                        
                        // The room is now available after the guest has finished admiring the vase
                        minotaur.setAvailable(true);
                        break;
                    }
                }
            }
            else
            {
                System.out.println("Guest " + id + " tried to enter the room but it was busy!");
            }
        }
    }
}

public class MinotaurVase 
{
    // Global final variables for guest count and randomizer
    private static final int NUM_GUESTS = 10;
    private static final Random r = new Random();

    // Busy or available sign flag
    private boolean available = true;

    // Set of all guests who have seen the vase already
    private final Set<Integer> seen = new HashSet<>();

    // Goooooo!!!!
    public static void main(String[] args) 
    {
        // Create an instance of this class
        MinotaurVase minotaur = new MinotaurVase();

        Thread [] threads = new Thread[NUM_GUESTS];

        // Go through each guest and create a thread for it, then start it
        for (int i = 0; i < NUM_GUESTS; i++) 
        {
            Guest guest = new Guest(i, minotaur);
            threads[i] = new Thread(guest);
            threads[i].start();
        }

        for (int i = 0; i < NUM_GUESTS; i++)
        {
            try
            {
                threads[i].join();
            }
            catch (Exception e)
            {}
        }
        System.out.println("Everyone has seen the vase!");
    }

    // Getters and setters
    public boolean isAvailable() 
    {
        return available;
    }

    public void setAvailable(boolean flag) 
    {
        this.available = flag;
    }

    // Add this guest to those who have seen the vase
    public synchronized void addSeen(int id) 
    {
        seen.add(id);
    }
}