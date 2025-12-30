
class Yolcu extends User {
    public Yolcu(int id, String tc, String name, String password, String type) {
        super(id, tc, name, password, type);
    }
}

class Sofor extends User {
    private String location;
    public Sofor(int id, String tc, String name, String password, String type, String location) {
        super(id, tc, name, password, type);
        this.location = location;
    }
    public String getLocation() { return location; }
}

class Admin extends User {
    public Admin(int id, String tc, String name, String password, String type) {
        super(id, tc, name, password, type);
    }
}