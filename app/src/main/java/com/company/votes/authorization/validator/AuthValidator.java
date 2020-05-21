package com.company.votes.authorization.validator;

public class AuthValidator {
    public boolean emailValid(String email){
        if(email.length() == 0){
            return false;
        }
        if(!email.matches(".+[@].+")){
            return false;
        }
        return true;
    }

    public boolean passwordValid(String password){
        if(password.length() < 6){
            return false;
        }
        return true;
    }

    public boolean nameValid(String name){
        if(name.length() == 0){
            return false;
        }
        return true;
    }

    public boolean phoneValid(String phone){
        if(!phone.matches("[+]?\\d+")){
            return false;
        }
        return true;
    }
}
