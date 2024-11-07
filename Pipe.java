package tes;

import java.awt.Image;

public class Pipe {
    int x;
    int y;
    int width;
    int height;
    Image img;
    boolean passed = false;

    Pipe(Image img) {
        this.img = img;
    }
}