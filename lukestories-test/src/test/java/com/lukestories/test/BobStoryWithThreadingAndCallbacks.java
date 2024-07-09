package com.lukestories.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

public class BobStoryWithThreadingAndCallbacks {

    @Test
    void castWizardSpellsToEnemiesSync() {

        System.out.println("Show me how wizard Bob can cast his spells to enemy");
        spell(() -> System.out.println("change visibility"));
        spell(() -> System.out.println("change size"));
        System.out.println("Done");
    }

    @Test
    void castWizardSpellsToEnemiesAsync() {
        System.out.println("Show me how wizard Bob can cast his spells to enemy");
        spellAsync(() -> System.out.println("change visibility"));
        spellAsync(() -> System.out.println("change size"));
        System.out.println("Lets wait until Bob finish his spells");
    }

    private void spell(Runnable runnable) {
        runnable.run();
    }
    private void spellAsync(Runnable runnable) {
        new Thread(runnable).start();
    }
}
