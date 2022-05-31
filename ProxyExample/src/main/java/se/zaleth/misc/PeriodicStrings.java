/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.zaleth.misc;

/**
 *
 * @author krister
 */
public class PeriodicStrings {
    
    public static final int NUM_ELEMENTS = 118;
    public static final String SINGLE_CODES = " BC  F HI K  NOP  S UVW Y "; // 14 elements
    public static final String[] DOUBLE_CODES = {
        "Ac, Ag, Al, Am, Ar, As, At, Au",               //  8
        "Ba, Be, Bh, Bi, Bk, Br",                       //  6
        "Ca, Cd, Ce, Cf, Cl, Cm, Cn, Co, Cr, Cs, Cu",   // 11
        "Db, Ds, Dy",                                   //  3
        "Er, Es, Eu",                                   //  3
        "Fe, Fl, Fm, Fr",                               //  4
        "Ga, Gd, Ge",                                   //  3
        "He, Hf, Hg, Ho, Hs",                           //  5
        "In, Ir",                                       //  2
        "",                                           // = 45
        "Kr",                                           //  1
        "La, Li, Lr, Lu, Lv",                           //  5
        "Mc, Md, Mg, Mn, Mo, Mt",                       //  6
        "Na, Nb, Nd, Ne, Nh, Ni, No, Np",               //  8
        "Og, Os",                                       //  2
        "Pa, Pb, Pd, Pm, Po, Pr, Pt, Pu",               //  8
        "Ra, Rb, Re, Rf, Rg, Rh, Rn, Ru",               //  8
        "Sb, Sc, Se, Sg, Si, Sm, Sn, Sr",               //  8
        "Ta, Tb, Tc, Te, Th, Ti, Tl, Tm, Ts",           //  9
        "",                                           // = 55
        "",
        "",
        "Xe",                                           //  1
        "Yb",                                           //  1
        "Zn, Zr"                                        //  2
    };
    
    public static boolean testHasAllElements() {
        int sum = 0;
        for(char c : SINGLE_CODES.toCharArray() ) {
            if(Character.isLetter(c))
                sum++;
        }
        for(String line : DOUBLE_CODES) {
            sum += line.length() > 0 ? line.split(",").length : 0;
            //System.out.println("Adding " + line.split(",").length);
        }
        return sum == NUM_ELEMENTS;
    }
    
    public static int scoreWord(String word) {
        if(word.length() == 0)
            return 0;
        if(word.indexOf(" ") > -1)
            word = word.split(" ")[0];
        byte[] bytes = word.getBytes();
        bytes[0] &= 0xDF; // clear bit 5 => force upper case
        word = new String(bytes);
        System.out.println(word);
        
        switch(word.length()) {
            case 0:
                return 0;
                
            case 1:
                return SINGLE_CODES.contains(word) ? 0 : 1;
                
            default:
                if(DOUBLE_CODES[bytes[0] - 'A'].contains(word)) {
                    if(SINGLE_CODES.contains(word.subSequence(0, 0)))
                        return Math.min(scoreWord(word.substring(2)), scoreWord(word.substring(1)));
                    return 1 + scoreWord(word.substring(1));
                } else if(SINGLE_CODES.contains(word.subSequence(0, 0))) {
                    return scoreWord(word.substring(1));
                } else {
                    return 1 + scoreWord(word.substring(1));
                }
        }
    }
    
    public static void main(String[] args) {
        System.out.println("" + testHasAllElements());
        System.out.println("" + scoreWord("Sverige"));
        System.out.println("" + scoreWord("Krister"));
    }

}
