package com.maher.n2c.app.ApiStructure;

public interface Constants {

        interface URL {
            String BASE_URL = "https://murmuring-plains-47925.herokuapp.com/user/";

            String SIGNUP = BASE_URL + "register";
            String LOGIN = BASE_URL + "login";
            String UPDATE_LOCATION = BASE_URL + "update_location";
            String ADD_FVRT = BASE_URL + "add_favourite_contacts";
            String GET_FVRT = BASE_URL + "get_favourite_contacts";
            String NOTIFY = BASE_URL + "notify";
            String GETPROFILE = BASE_URL + "profile";
            String UPDATEPROFILE = BASE_URL + "update_profile";


        }
}
