package turfease;

public abstract class Person {

    private static int idCounter = 1000;

    private final int id;
    private String name;
    private String email;
    private String phone;
    private String passwordSalt = "";
    private String passwordHash = "";

    protected Person(String name, String email, String phone) {
        this.id = idCounter++;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    // ---------- login details (the password is never stored as plain text) ----------

    public void setPassword(String password) {
        String salt = PasswordUtil.newSalt();
        setCredentials(salt, PasswordUtil.hash(password, salt));
    }

    void setCredentials(String salt, String hash) {
        this.passwordSalt = salt;
        this.passwordHash = hash;
    }

    String getPasswordSalt() {
        return passwordSalt;
    }

    String getPasswordHash() {
        return passwordHash;
    }

    public boolean checkPassword(String password) {
        return !passwordHash.isEmpty()
                && PasswordUtil.matches(password, passwordSalt, passwordHash);
    }

    public abstract String getRole();

    @Override
    public String toString() {
        return String.format("[%s] #%d %s (%s, %s)", getRole(), id, name, email, phone);
    }
}