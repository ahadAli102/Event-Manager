package com.example.softwareprojectv;

import com.example.softwareprojectv.model.AdminAbout;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean validate(String emailAddress) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailAddress);
        return matcher.find();
    }

    public static ArrayList<String> getUniqueEmail(ArrayList<String> email) {
        return new ArrayList<>(new TreeSet<>(email));
    }

    public static List<AdminAbout> getSortedAboutList(List<AdminAbout> aboutList) {
        List<AdminAbout> newList = new ArrayList<>();
        for(AdminAbout aa: aboutList){
            if(aa.getDesignation().trim().equalsIgnoreCase("President")){
                newList.add(aa);
            }
        }
        for(AdminAbout aa: aboutList){
            if(aa.getDesignation().trim().equalsIgnoreCase("Assistant President")){
                newList.add(aa);
            }
        }
        for(AdminAbout aa: aboutList){
            if(aa.getDesignation().trim().equalsIgnoreCase("Secretary")){
                newList.add(aa);
            }
        }
        for(AdminAbout aa: aboutList){
            if(aa.getDesignation().trim().equalsIgnoreCase("Assistant Secretary")){
                newList.add(aa);
            }
        }
        for(AdminAbout aa: aboutList){
            boolean b = aa.getDesignation().trim().equalsIgnoreCase("Secretary") ||
                    aa.getDesignation().trim().equalsIgnoreCase("President") ||
                    aa.getDesignation().trim().equalsIgnoreCase("Assistant President") ||
                    aa.getDesignation().trim().equalsIgnoreCase("Assistant Secretary");
            if(!b){
                newList.add(aa);
            }
        }
        return newList;
    }
}
