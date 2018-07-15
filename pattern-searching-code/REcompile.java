/*
Name: Jordan Akroyd
ID: 1280741
Name: Jade Myers
ID: 1287182
*/

/*
Variables
---
E = Expression
T = Term
F = Factor
v = Band of Literals

Grammar
...

Expression
---
E -> T
E -> T | E

Term
---
T -> F
T -> F*
T -> F+
T -> F?
T -> FT

Factor
---
F -> v
F -> (E)
F -> [alt]
F -> ![alt]!
F -> \
F -> .

Literals
---
v -> v
v -> vv
*/

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

//computes states from a regexp
public class REcompile {
    static char[] charArray;
    static String lang = "!|+*()?[]\\.";
    static String flang = "|)]";
    static int index = 0;
    static int currState = 0;
    static States states = new States();

    //holds the states
    private static class States {
        static ArrayList<String> character = new ArrayList<String>();
        static Deque<Integer> start = new ArrayDeque<Integer>();
        static ArrayList<Integer> next1 = new ArrayList<Integer>();
        static ArrayList<Integer> next2 = new ArrayList<Integer>();

        //adds a state
        private static void setState(String _char, int _next1, int _next2) {
            character.add(_char);
            next1.add(_next1);
            next2.add(_next2);
        }

        //inserts a state at a position
        private static void insertState(int _index, int _next1, int _next2) {
            character.add(_index, Character.toString('\0'));
            for (int i = _index; i < next1.size(); i++) {
                next1.set(i, next1.get(i) + 1);
                next2.set(i, next2.get(i) + 1);
            }
            next1.add(_index, _next1);
            next2.add(_index, _next2);
        }

        //updates a state
        private static void updateState(int _index, int _next1, int _next2) {
            next1.set(_index, _next1);
            next2.set(_index, _next2);
        }

        //sets the start of an expression
        private static void setStart(int curr) {
            start.push(curr);
        }

        //gets the last start of an expression
        private static int getStart() {
            return start.pop();
        }

        //peeks the value of the start of the expression
        private static int peekStart() {
            return start.peekLast();
        }

        //print all of the states
        private static void printArrays() {
            for (int i = 0; i < character.size(); i++) {
                System.out.println(i + Character.toString((char) 254) + character.get(i) + Character.toString((char) 254) + next1.get(i) + Character.toString((char) 254) + next2.get(i));
            }
        }
    }

    public static void main(String args[]) {
        //correct number of args?
        if (args.length > 1) {
            System.err.println("<java REcompile \"string\" >");
        }
        //store the regexp
        charArray = args[0].toCharArray();
        //start
        parse();
    }

    //for every literal character inside of []
    private static void lit(int temp) {
        if (!inBounds()) {
            return;
        }
        if (charArray[index] != ']') {

            //haracter
            states.setState(Character.toString(charArray[index]), temp, temp);
            currState++;

            //dummy state
            states.setState(Character.toString('\0'), currState + 1, currState + 2);
            currState++;

            index++;
            if (!inBounds()) {
                return;
            }
            lit(temp);
        }

    }

    //for every literal inside ![]! append to string
    private static String notlit(String temp) {
        if (!inBounds()) {
            return temp;
        }
        if (charArray[index] != ']') {
            temp = temp + charArray[index];
            index++;
            temp = notlit(temp);
        }
        return temp;

    }

    //for every alt character inside of []
    private static void alt() {
        if (!inBounds()) {
            return;
        }

        //dummy state
        states.setState(Character.toString('\0'), currState + 2, currState + 3);
        currState++;

        //all states in the alt point to this dummy state that points to the next state
        states.setState(Character.toString('\0'), -1, -1);
        int temp = currState;
        currState++;

        //character
        states.setState(Character.toString(charArray[index]), temp, temp);
        currState++;

        //dummy state
        states.setState(Character.toString('\0'), currState + 1, currState + 2);
        currState++;

        index++;

        lit(temp);

        //update our DS state to go to the ending state
        states.updateState(temp, currState, currState);
        states.updateState(currState - 1, currState - 2, currState - 2);
    }

    //for every notalt character inside of ![]!
    private static void notAlt() {
        if (!inBounds()) {
            return;
        }

        //adds string from notlit
        String u = notlit("!");
        //adds string to state
        states.setState(u, currState + 1, currState + 1);
        currState++;
    }

    //for every factor () [] ![]! find its factors
    private static void fact() {
        if (!inBounds()) {
            return;
        }
        if (isLit()) {

            states.setState(Character.toString(charArray[index]), currState + 1, currState + 1);
            currState++;

            index++;
            if (!inBounds()) {
                return;
            }
        } else if (charArray[index] == '(') {
            index++;
            if (!inBounds()) {
                isError();
                return;
            }
            exp();
            if (!inBounds()) {
                isError();
                return;
            }
            if (charArray[index] == ')') {
                index++;
                if (!inBounds()) {
                    return;
                }
            } else {
                isError();
            }
        } else if (charArray[index] == '[') {
            index++;
            if (!inBounds()) {
                isError();
                return;
            }
            alt();
            if (!inBounds()) {
                isError();
                return;
            }
            if (charArray[index] == ']') {
                index++;
                if (!inBounds()) {

                    return;
                }
            } else {
                isError();
            }
        } else if (charArray[index] == '!') {
            index++;
            if (!inBounds()) {
                isError();
                return;
            }
            if (charArray[index] == '[') {
                index++;
                if (!inBounds()) {
                    isError();
                    return;
                }
                notAlt();
                if (!inBounds()) {
                    isError();
                    return;
                }
                if (charArray[index] == ']') {
                    index++;
                    if (!inBounds()) {
                        isError();
                        return;
                    }

                    if (charArray[index] == '!') {
                        index++;
                        if (!inBounds()) {
                            return;
                        }
                    } else {
                        isError();
                    }

                } else {
                    isError();
                }
            } else {
                isError();
            }

        } else if (charArray[index] == '\\') {
            index++;
            if (!inBounds()) {
                isError();
                return;
            }
            states.setState(Character.toString(charArray[index]), currState + 1, currState + 1);
            currState++;

            index++;
            if (!inBounds()) {
                return;
            }
        } else if (charArray[index] == '.') {

            states.setState(Character.toString(charArray[index]), currState + 1, currState + 1);
            currState++;

            index++;
            if (!inBounds()) {
                return;
            }
        } else {
            isError();
        }

    }

    //for every term do its purpose
    private static void term() {
        int currIndex = currState;

        fact();
        if (!inBounds()) {
            return;
        }
        if (charArray[index] == '*') {

            int ds = states.peekStart();

            states.insertState(currIndex, currIndex + 1, currState + 1);
            currState++;

            states.updateState(currState - 1, currIndex, currIndex);

            index++;
            if (!inBounds()) {
                return;
            }
        } else if (charArray[index] == '+') {
            int ds = states.peekStart();

            states.updateState(currState - 1, currState, currState);
            states.setState(Character.toString('\0'), currIndex, currState + 1);
            currState++;

            index++;
            if (!inBounds()) {
                return;
            }
        } else if (charArray[index] == '?') {

            int ds = states.peekStart();

            states.insertState(currIndex, currIndex + 1, currState + 1);
            currState++;

            states.setState(Character.toString('\0'), currState + 1, currState + 1);
            currState++;

            index++;
            if (!inBounds()) {
                return;
            }
        }
        if (flang.indexOf(charArray[index]) == -1) {
            term();
            if (!inBounds()) {
                return;
            }
        }
    }

    //for every expression find its factors
    private static void exp() {
        states.setStart(currState);
        states.setState(Character.toString('\0'), currState + 1, currState + 1);
        currState++;
        term();
        if (!inBounds()) {
            return;
        }
        if (charArray[index] == '|') {
            int ds = states.getStart();
            int tmp = currState;
            states.updateState(ds, ds + 1, currState + 1);

            index++;
            if (!inBounds()) {
                return;
            }
            exp();
            states.getStart();
            states.updateState(tmp, currState, currState);

        }

    }

    //is it a literal
    private static boolean isLit() {
        if (lang.indexOf(charArray[index]) == -1) {
            return true;
        }
        return false;
    }

    //is the index in the bounds of regexp
    private static boolean inBounds() {
        return (index >= 0) && (index < charArray.length);
    }

    private static void isError() {

        try {
            char c = charArray[-1];
        } catch (Exception e) {
            index = -1;

        }
        return;
    }

    //runs through the expression and prints it
    private static void parse() {
        exp();
        states.setState(Character.toString((char) 255), 0, 0);
        if (charArray.length == index) {
            states.printArrays();
        } else System.err.println("You have an error in your expression.");

    }


}
