package com.tuplaus.service;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class RandomCardService {
    public int getRandomCard() {
        var rnd = new Random();

        return rnd.nextInt(13) + 1;
    }
}
