public abstract class User {
    protected int id;
    protected String tc;
    protected String name;
    protected String password;
    protected String type;

    public User(int id, String tc, String name, String password, String type) {
        this.id = id;
        this.tc = tc;
        this.name = name;
        this.password = password;
        this.type = type;
    }

    public int getId() { return id; }
    public String getTc() { return tc; }
    public String getName() { return name; }
    public String getType() { return type; }
}