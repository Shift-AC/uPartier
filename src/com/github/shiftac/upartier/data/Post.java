package com.github.shiftac.upartier.data;

import java.util.ArrayList;

public class Post
{
    int id = 0;
    int blockID = 0;
    String name = null;
    long time = 0;
    String label = null;
    String location = null;
    String note = null;
    User postUser = null;
    ArrayList<Message> messages = null;
    int userCount = 0;
    ArrayList<User> users = null;
}