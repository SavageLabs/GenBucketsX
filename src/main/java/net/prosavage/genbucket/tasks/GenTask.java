package net.prosavage.genbucket.tasks;

import net.prosavage.genbucket.gen.Generator;
import org.bukkit.scheduler.BukkitRunnable;


public class GenTask extends BukkitRunnable {

    private Generator generator;

    public GenTask(Generator generator) {
        this.generator = generator;
    }

    @Override
    public void run() {
        if (generator.isFinished()) {
            this.cancel();
        } else {
            generator.run();
        }
    }
}
