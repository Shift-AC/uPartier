package com.github.shiftac.upartier.network;

import java.security.SecureRandom;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.KeyGenerator;

import com.github.shiftac.upartier.Util;

public class AES128Key
{
    byte[] seed = null;
    SecretKey key = null;
    IvParameterSpec spec = null;

    static int lfsr113_Bits(int z1, int z2, int z3, int z4)
    {
       int b;
       b  = ((z1 << 6) ^ z1) >>> 13;
       z1 = ((z1 & -2) << 18) ^ b;
       b  = ((z2 << 2) ^ z2) >>> 27; 
       z2 = ((z2 & -8) << 2) ^ b;
       b  = ((z3 << 13) ^ z3) >>> 21;
       z3 = ((z3 & -16) << 7) ^ b;
       b  = ((z4 << 3) ^ z4) >>> 12;
       z4 = ((z4 & -128) << 13) ^ b;
       return z1 ^ z2 ^ z3 ^ z4;
    }

    public AES128Key(int sa, int id, long timestamp)
    {
        int th = (int)(timestamp >>> 32);
        int tl = (int)timestamp;
        int[] key = new int[4];
        key[0] = lfsr113_Bits(sa, id, th, tl);
        key[1] = lfsr113_Bits(id, sa, tl, th);
        key[2] = lfsr113_Bits(tl, th, id, sa);
        key[3] = lfsr113_Bits(th, tl, sa, id);

        seed = new byte[16];
        for (int i = 0; i < 4; ++i)
        {
            seed[(i << 2) + 0] = (byte)(key[i] >> 24);
            seed[(i << 2) + 1] = (byte)(key[i] >> 16);
            seed[(i << 2) + 2] = (byte)(key[i] >> 8);
            seed[(i << 2) + 3] = (byte)key[i];
        }
        try
        {
            KeyGenerator gen = KeyGenerator.getInstance("AES");
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            sr.setSeed(seed);
            gen.init(128, sr);
            this.key = gen.generateKey();
            spec = new IvParameterSpec(seed);
        }
        catch(Exception e)
        {
            e.printStackTrace(Util.log.dest);
        }
    }
}