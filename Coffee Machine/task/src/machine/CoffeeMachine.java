package machine;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CoffeeMachine {

    private static final Scanner scanner = new Scanner(System.in);

    private static final ComponentSet espresso = new ComponentSet(
            250, 0, 16, 1, 4);
    private static final ComponentSet latte = new ComponentSet(
            350, 75, 20, 1, 7);
    private static final ComponentSet cappuccino = new ComponentSet(
            200, 100, 12, 1, 6);
    private static final ComponentSet initialMachineComponents = new ComponentSet(
            400, 540, 120, 9, 550);

    private static final Map<String, ComponentSet> coffeeSelector = new HashMap<>();

    static {
        coffeeSelector.put("1", espresso);
        coffeeSelector.put("2", latte);
        coffeeSelector.put("3", cappuccino);
    }

    private final ComponentSet storage;
    private MachineState state;

    public CoffeeMachine() {
        storage = new ComponentSet(
                initialMachineComponents.water,
                initialMachineComponents.milk,
                initialMachineComponents.beans,
                initialMachineComponents.cups,
                initialMachineComponents.money);
        state = MachineState.IDLE;
        prompt(state);
    }

    private static void message(String text) {
        System.out.println(text);
    }

    private static void message(MachineState machineState) {
        message(machineState.getPrompt());
    }

    private static void prompt(MachineState machineState) {
        message(machineState);
        System.out.print("> ");
    }

    public static void main(String[] args) {
        CoffeeMachine machine = new CoffeeMachine();
        String command = scanner.next();
        while (!"exit".equals(command)) {
            machine.enterCommand(command);
            command = scanner.next();
        }
    }

    @SuppressWarnings("unused")
    public void enterCommand(String command) {
        switch (state) {
            case IDLE:
                state = handleCommand(command);
                break;
            case SELL:
                state = handleSell(command);
                break;
            case REPLENISH_WATER:
                storage.water += Integer.parseInt(command);
                state = MachineState.REPLENISH_MILK;
                break;
            case REPLENISH_MILK:
                storage.milk += Integer.parseInt(command);
                state = MachineState.REPLENISH_BEANS;
                break;
            case REPLENISH_BEANS:
                storage.beans += Integer.parseInt(command);
                state = MachineState.REPLENISH_CUPS;
                break;
            case REPLENISH_CUPS:
                storage.cups += Integer.parseInt(command);
                state = MachineState.IDLE;
                break;
            default:
        }
        prompt(state);
    }

    private MachineState handleCommand(String command) {
        switch (command) {
            case "take":
                message("I gave you $" + storage.money);
                storage.money = 0;
                break;
            case "buy":
                return MachineState.SELL;
            case "remaining":
                message("The coffee machine has:");
                message(storage.toString());
                message("");
                break;
            case "fill":
                return MachineState.REPLENISH_WATER;
            default:
                System.exit(0);
        }
        return MachineState.IDLE;
    }

    private MachineState handleSell(String command) {
        ComponentSet coffee = coffeeSelector.get(command);
        if (coffee != null) {
            sell(coffee);
        }
        return MachineState.IDLE;
    }

    private void sell(ComponentSet coffee) {
        MachineState sellState;
        if (storage.water < coffee.water) {
            sellState = MachineState.NOT_ENOUGH_WATER;
        } else if (storage.milk < coffee.milk) {
            sellState = MachineState.NOT_ENOUGH_MILK;
        } else if (storage.beans < coffee.beans) {
            sellState = MachineState.NOT_ENOUGH_BEANS;
        } else if (storage.cups < coffee.cups) {
            sellState = MachineState.NOT_ENOUGH_CUPS;
        } else {
            storage.sell(coffee);
            sellState = MachineState.SOLD_SUCCESSFULLY;
        }
        message(sellState);
    }

    private enum MachineState {
        IDLE("Write action (buy, fill, take, remaining, exit):"),

        SELL("What do you want to buy? 1 - espresso, 2 - latte, 3 - cappuccino, back - to main menu:"),

        REPLENISH_WATER("Write how many ml of water do you want to add:"),
        REPLENISH_MILK("Write how many ml of milk do you want to add:"),
        REPLENISH_BEANS("Write how many grams of coffee beans do you want to add:"),
        REPLENISH_CUPS("Write how many disposable cups of coffee do you want to add:"),

        NOT_ENOUGH_WATER("Sorry, not enough water!"),
        NOT_ENOUGH_MILK("Sorry, not enough milk!"),
        NOT_ENOUGH_BEANS("Sorry, not enough beans!"),
        NOT_ENOUGH_CUPS("Sorry, not enough disposable cups!"),

        SOLD_SUCCESSFULLY("I have enough resources, making you a coffee!");

        private final String prompt;

        MachineState(String prompt) {
            this.prompt = prompt;
        }

        String getPrompt() {
            return prompt;
        }
    }

    private static class ComponentSet {
        int water;
        int milk;
        int beans;
        int cups;
        int money;

        public ComponentSet(int water, int milk, int beans, int cups, int money) {
            this.water = water;
            this.milk = milk;
            this.beans = beans;
            this.cups = cups;
            this.money = money;
        }

        public void sell(ComponentSet coffee) {
            this.water -= coffee.water;
            this.milk -= coffee.milk;
            this.beans -= coffee.beans;
            this.cups -= coffee.cups;
            this.money += coffee.money;
        }

        @Override
        public String toString() {
            return water + " of water\n"
                    + milk + " of milk\n"
                    + beans + " of coffee beans\n"
                    + cups + " of disposable cups\n"
                    + money + " of money";
        }
    }
}