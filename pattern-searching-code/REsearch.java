/*
Name: Jordan Akroyd
ID: 1280741
Name: Jade Myers
ID: 1287182
*/

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

//searches text for a match to the regular expression
public class REsearch {

    //holds a state
    static class State {
        int stateNum;
        String character;
        int next1;
        int next2;

        public State(int _stateNum, String _character, int _next1, int _next2) {
            stateNum = _stateNum;
            character = _character;
            next1 = _next1;
            next2 = _next2;
        }
    }

    //hold current and next states
    static class Deque {

        //linked list of states
        static class Node {

            State s;
            Node next;
            Node prev;

            public Node(State _s, Node _prev, Node _next) {
                s = _s;
                prev = _prev;
                next = _next;
            }
        }

        Node head;
        Node last;
        int length;

        //adds to the head of the deque
        public void addToHead(State _s) {

            if (head == null) {
                head = new Node(_s, null, null);
                last = head;
            } else {
                Node tmp = new Node(_s, null, head);
                head.prev = tmp;
                head = tmp;
            }
            length++;
        }

        //adds to the end of the deque
        public void addToTail(State _s) {
            if (head == null) {
                head = new Node(_s, null, null);
                last = head;
            } else {
                Node tmp = new Node(_s, last, null);
                last.next = tmp;
                last = tmp;
            }
            length++;
        }

        //pops of the end of the deque
        public Node pop() {
            if (head == null) {
                return null;
            } else {
                Node tmp = last;
                last = last.prev;
                if (last == null) {
                    length--;
                    return tmp;
                }
                last.next = null;
                length--;
                return tmp;
            }

        }
    }

    public static void main(String args[]) {

        Deque deque;
        String line;
        char[] readArray;
        String[] state;
        ArrayList<State> statesArray = new ArrayList<>();
        State s;

        try {

            BufferedReader br = new BufferedReader(new FileReader(args[0]));
            Scanner sc = new Scanner(System.in);

            //reads in states from REcompile
            while (sc.hasNextLine()) {
                line = sc.nextLine();
                state = line.split(Character.toString((char) 254));
                s = new State(Integer.parseInt(state[0]), state[1], Integer.parseInt(state[2]), Integer.parseInt(state[3]));
                statesArray.add(s);
            }

            //reads text line by line
            while ((line = br.readLine()) != null) {

                int counter = 0;
                deque = new Deque();
                readArray = line.toCharArray();

                //initializes deque
                deque.addToHead(new State(-1, "SCAN", -1, -1));
                deque.addToTail(statesArray.get(0));

                while (true) {

                    //pops deque
                    s = deque.pop().s;
                    //if the characters are in a not state
                    if ((s.character.length() >= 2) && s.character.charAt(0) != 'S') {

                        //takes of ! at start
                        String q = s.character.substring(1, s.character.length());

                        //if the counter is greater than line length
                        if (counter >= readArray.length) {
                            break;
                        }

                        //if the character we are looking at on the line doesn't match a charcter in the not state
                        if (q.indexOf(readArray[counter]) < 0) {
                            //open states
                            deque = openDummy(s, deque, statesArray);
                        }
                    } else {

                        //if the expression matches
                        if (s.character.charAt(0) == 255) {
                            System.out.println(line);
                            break;
                        }

                        //if no match
                        if (deque.length == 0 || s.character.charAt(0) == 253) {
                            deque = new Deque();
                            deque.addToHead(new State(-1, "SCAN", -1, -1));
                            deque.addToTail(statesArray.get(0));
                            counter++;

                        } else {

                            //if its a scane move along a character in the line and reset scan
                            if (s.character.equals("SCAN")) {
                                counter++;
                                deque.addToHead(s);

                            } else {

                                //if the character is not a dummy state
                                if (s.character.charAt(0) != 0) {

                                    //break if end of line
                                    if (counter >= readArray.length) {
                                        break;
                                    }

                                    //if it is a character or a . which matches anything
                                    if (s.character.charAt(0) == readArray[counter] || s.character.charAt(0) == '.') {

                                        int next1 = s.next1;
                                        int next2 = s.next2;

                                        if (next1 == next2) {
                                            deque.addToHead(statesArray.get(next1));
                                        } else {
                                            deque.addToHead(statesArray.get(next1));
                                            deque.addToHead(statesArray.get(next2));
                                        }
                                    }
                                } else {

                                    //open states
                                    deque = openDummy(s, deque, statesArray);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error in expression, stack trace below.");
            e.printStackTrace();
            //System.err.println(e);
        }
    }

    //expands dummy state until final states are the only ones present
    private static Deque openDummy(State s, Deque deque, ArrayList<State> statesArray) {

        int next1 = s.next1;
        int next2 = s.next2;

        //if the states are the same
        if (next1 == next2) {
            if (statesArray.get(next1).character.charAt(0) == 0) {
                openDummy(statesArray.get(next1), deque, statesArray);
            } else {
                deque.addToTail(statesArray.get(next1));
            }
        } else {
            if (statesArray.get(next1).character.charAt(0) == 0) {
                openDummy(statesArray.get(next1), deque, statesArray);
            } else {
                deque.addToTail(statesArray.get(next1));
            }
            if (statesArray.get(next2).character.charAt(0) == 0) {
                openDummy(statesArray.get(next2), deque, statesArray);
            } else {
                deque.addToTail(statesArray.get(next2));
            }


        }
        return deque;

    }
}