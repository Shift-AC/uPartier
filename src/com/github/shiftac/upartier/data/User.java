package com.github.shiftac.upartier.data;

import java.util.ArrayList;

public class User
{
    public static interface Gender
    {
        public static final int male = 0;
        public static final int female = 1;
        public static final int unknown = 2;
    }

    int id = 0;
    int age = 0;
    int gender = Gender.unknown;
    String mailAccount = null;
    String nickname = null;
    Image profile = null;
    int postCount = 0;
    ArrayList<Post> myPosts = null;
}