package com.example.softwareprojectv.model;

public class ProgramApplicants {
    private String name;
    private String email;

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

    public ProgramApplicants(String name, String email) {
        this.name = name;
        this.email = email;
    }

}
