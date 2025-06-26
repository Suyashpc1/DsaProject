import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class phonebook {
    // Trie Node class for prefix search
    static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        boolean isEndOfWord;
    }

    // Trie class for managing contact names
    static class Trie {
        private TrieNode root;

        public Trie() {
            root = new TrieNode();
        }

        // Insert a name into the Trie
        public void insert(String word) {
            TrieNode node = root;
            for (char ch : word.toCharArray()) {
                node = node.children.computeIfAbsent(ch, c -> new TrieNode());
            }
            node.isEndOfWord = true;
        }

        // Search for all names with the given prefix
        public java.util.List<String> searchByPrefix(String prefix) {
            java.util.List<String> results = new java.util.ArrayList<>();
            TrieNode node = root;
            for (char ch : prefix.toCharArray()) {
                node = node.children.get(ch);
                if (node == null)
                    return results;
            }
            dfs(node, new StringBuilder(prefix), results);
            return results;
        }

        // Helper DFS to collect all words from a given node
        private void dfs(TrieNode node, StringBuilder prefix, java.util.List<String> results) {
            if (node.isEndOfWord) {
                results.add(prefix.toString());
            }
            for (java.util.Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
                prefix.append(entry.getKey());
                dfs(entry.getValue(), prefix, results);
                prefix.deleteCharAt(prefix.length() - 1);
            }
        }
    }

    // Phonebook class
    static class PhoneBook {
        private Trie trie;
        private java.util.Map<String, String> nameToNumber;

        public PhoneBook() {
            trie = new Trie();
            nameToNumber = new java.util.HashMap<>();
        }

        // Add a contact
        public void addContact(String name, String number) {
            trie.insert(name);
            nameToNumber.put(name, number);
        }

        // Search contacts by prefix
        public java.util.List<String> searchByPrefix(String prefix) {
            return trie.searchByPrefix(prefix);
        }

        // Get number by name
        public String getNumber(String name) {
            return nameToNumber.get(name);
        }
    }

    // Main method with Swing GUI
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PhoneBookGUI(new PhoneBook()));
    }

    // Swing GUI class
    static class PhoneBookGUI extends JFrame {
        private final PhoneBook phoneBook;
        private final JTextField nameField = new JTextField(20);
        private final JTextField numberField = new JTextField(20);
        private final JTextField prefixField = new JTextField(20);
        private final JTextField searchNameField = new JTextField(20);
        private final JTextArea resultArea = new JTextArea(8, 30);

        public PhoneBookGUI(PhoneBook phoneBook) {
            super("Phonebook Application");
            this.phoneBook = phoneBook;
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new BorderLayout());
            resultArea.setEditable(false);
            resultArea.setLineWrap(true);
            resultArea.setWrapStyleWord(true);

            // Panel for adding contact
            JPanel addPanel = new JPanel();
            addPanel.setBorder(BorderFactory.createTitledBorder("Add New Contact"));
            addPanel.add(new JLabel("Name:"));
            addPanel.add(nameField);
            addPanel.add(new JLabel("Number:"));
            addPanel.add(numberField);
            JButton addButton = new JButton("Add Contact");
            addPanel.add(addButton);

            // Panel for searching by prefix
            JPanel searchPanel = new JPanel();
            searchPanel.setBorder(BorderFactory.createTitledBorder("Search by Name Prefix"));
            searchPanel.add(new JLabel("Prefix:"));
            searchPanel.add(prefixField);
            JButton searchButton = new JButton("Search");
            searchPanel.add(searchButton);

            // Panel for getting number by name
            JPanel getPanel = new JPanel();
            getPanel.setBorder(BorderFactory.createTitledBorder("Get Number by Name"));
            getPanel.add(new JLabel("Name:"));
            getPanel.add(searchNameField);
            JButton getButton = new JButton("Get Number");
            getPanel.add(getButton);

            // Panel for results
            JPanel resultPanel = new JPanel();
            resultPanel.setBorder(BorderFactory.createTitledBorder("Results"));
            resultPanel.setLayout(new BorderLayout());
            resultPanel.add(new JScrollPane(resultArea), BorderLayout.CENTER);

            // Layout
            JPanel topPanel = new JPanel();
            topPanel.setLayout(new GridLayout(3, 1));
            topPanel.add(addPanel);
            topPanel.add(searchPanel);
            topPanel.add(getPanel);
            add(topPanel, BorderLayout.NORTH);
            add(resultPanel, BorderLayout.CENTER);

            // Button actions
            addButton.addActionListener(e -> {
                String name = nameField.getText().trim();
                String number = numberField.getText().trim();
                if (name.isEmpty() || number.isEmpty()) {
                    showMessage("Please enter both name and number.");
                } else {
                    phoneBook.addContact(name, number);
                    showMessage("Contact added: " + name + " - " + number);
                    nameField.setText("");
                    numberField.setText("");
                }
            });

            searchButton.addActionListener(e -> {
                String prefix = prefixField.getText().trim();
                if (prefix.isEmpty()) {
                    showMessage("Please enter a prefix to search.");
                } else {
                    java.util.List<String> matches = phoneBook.searchByPrefix(prefix);
                    if (matches.isEmpty()) {
                        showMessage("No contacts found with that prefix.");
                    } else {
                        StringBuilder sb = new StringBuilder("Contacts with prefix '" + prefix + "':\n");
                        for (String contact : matches) {
                            sb.append(contact).append("\n");
                        }
                        showMessage(sb.toString());
                    }
                }
            });

            getButton.addActionListener(e -> {
                String name = searchNameField.getText().trim();
                if (name.isEmpty()) {
                    showMessage("Please enter a contact name.");
                } else {
                    String number = phoneBook.getNumber(name);
                    if (number != null) {
                        showMessage("Number for '" + name + "': " + number);
                    } else {
                        showMessage("Contact not found.");
                    }
                }
            });

            pack();
            setLocationRelativeTo(null);
            setVisible(true);
        }

        private void showMessage(String message) {
            resultArea.setText(message);
        }
    }
}
