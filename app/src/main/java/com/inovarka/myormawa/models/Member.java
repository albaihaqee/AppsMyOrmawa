package com.inovarka.myormawa.models;

public class Member {
    private String id;
    private String name;
    private String department;
    private String position;
    private String phone;
    private String prodi;

    public Member(String id, String name, String department, String position, String phone, String prodi) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.position = position;
        this.phone = phone;
        this.prodi = prodi;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public String getPosition() { return position; }
    public String getPhone() { return phone; }
    public String getProdi() { return prodi; }

    public String getInitial() {
        String[] parts = name.split(" ");
        if (parts.length >= 2) {
            return parts[0].substring(0, 1) + parts[1].substring(0, 1);
        }
        return name.substring(0, Math.min(2, name.length()));
    }
}
