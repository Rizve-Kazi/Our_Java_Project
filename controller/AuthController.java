package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import util.FileHandler;
import util.SecurityUtil;

/** Register and login. Accounts live in users.txt as: username,salt,password_hash */
public class AuthController {
    private static final String FILE = "users.txt";
    private static final Pattern USERNAME = Pattern.compile("[A-Za-z0-9_]{3,20}");

    private final String dir;

    public AuthController() {
        this("data");
    }

    public AuthController(String dir) {
        this.dir = dir;
    }

    /** Every user gets their own folder, so each student's data stays separate. */
    public static String userDir(String username) {
        return "data/users/" + username.trim().toLowerCase(Locale.ROOT);
    }

    private String[] find(String username) {
        String u = username.trim().toLowerCase(Locale.ROOT);
        for (String line : FileHandler.readLines(dir, FILE)) {
            String[] p = line.split(",", -1);
            if (p.length == 3 && p[0].equals(u)) {
                return p;
            }
        }
        return null;
    }

    public void register(String username, String password, String confirm) {
        String u = username == null ? "" : username.trim();
        if (!USERNAME.matcher(u).matches()) {
            throw new IllegalArgumentException("Username must be 3-20 letters, numbers or _ .");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters.");
        }
        if (!password.equals(confirm)) {
            throw new IllegalArgumentException("The two passwords do not match.");
        }
        if (find(u) != null) {
            throw new IllegalArgumentException("That username is already taken.");
        }
        String salt = SecurityUtil.newSalt();
        List<String> lines = new ArrayList<String>(FileHandler.readLines(dir, FILE));
        lines.add(u.toLowerCase(Locale.ROOT) + "," + salt + "," + SecurityUtil.hash(password, salt));
        FileHandler.writeLines(dir, FILE, lines);
    }

    public boolean login(String username, String password) {
        if (username == null || password == null) {
            return false;
        }
        String[] rec = find(username);
        return rec != null && SecurityUtil.matches(password, rec[1], rec[2]);
    }
}
